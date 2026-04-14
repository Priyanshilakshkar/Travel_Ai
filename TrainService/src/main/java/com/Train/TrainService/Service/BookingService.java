package com.Train.TrainService.Service;

import com.Train.TrainService.Entities.SeatHold;
import com.Train.TrainService.Entities.TrainBooking;
import com.Train.TrainService.Entities.Train_Schedule;
import com.Train.TrainService.Enum.BookingStatus;
import com.Train.TrainService.Enum.MealPreference;
import com.Train.TrainService.Events.TrainSeatLockedEvent;
import com.Train.TrainService.Exceptions.BuissnessValidationException;
import com.Train.TrainService.Exceptions.ResourceNotFoundException;
import com.Train.TrainService.Repository.SeatHoldRepository;
import com.Train.TrainService.Repository.TrainBookinRepository;
import com.Train.TrainService.Repository.TrainRouteRepository;
import com.Train.TrainService.Repository.TrainScheduleRepository;
import com.Train.TrainService.dto.*;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.core.EventPublisher;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import javax.naming.ServiceUnavailableException;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@Slf4j
@Transactional
public class BookingService {
    @Autowired
    private TrainScheduleRepository trainScheduleRepository;

    @Autowired
    private TrainRouteRepository trainRouteRepository;

    @Autowired
    private TrainBookinRepository trainBookinRepository;

    @Autowired
    private ApplicationEventPublisher eventPublisher;
    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private SeatHoldRepository seatHoldRepository;

    @Autowired
    private PaymentService paymentService;

    @Value("${booking.seat-lock-duration-minutes}")
    private Integer lockDurationMinutes;


    @RateLimiter(name = "bookingApi")
    public BookingHoldResponse seatHold(UUID userId , BookingHoldRequest request){
        Train_Schedule schedule = trainScheduleRepository.findById(request.getScheduleId())
                .orElseThrow(()->new ResourceNotFoundException("No schedule found"));

        if(schedule.getAvailableSeats()< request.getPassengers().size()){
            throw new BuissnessValidationException("Seats are not Available");
        }
        String BookingRef = BookingReference();

        List<String> allocatedSeats = allocateSeatsWithLock(schedule , request.getPassengers().size(),BookingRef);

        BigDecimal totalFare = CalculateFare(schedule , request.getPassengers() , request.getMealPreferences());

        TrainBooking booking = new TrainBooking();
        booking.setBookingref(BookingRef);
        booking.setDetails(request.getPassengers());
        booking.setSeatNumbers(allocatedSeats);
        booking.setStatus(BookingStatus.HOLD);
        booking.setSchedule(schedule);
        booking.setMealPreferences(convertMealPreferences(request.getMealPreferences(), allocatedSeats));
        booking.setTotalFare(totalFare);
        booking.setUserId(userId);
        booking.setBookedAt(Instant.now());
        booking.setLockExpiresAt(Instant.now().plus(lockDurationMinutes , ChronoUnit.MINUTES));

        trainBookinRepository.save(booking);

        schedule.setAvailableSeats(schedule.getAvailableSeats() - request.getPassengers().size());
        trainScheduleRepository.save(schedule);

        eventPublisher.publishEvent(new TrainSeatLockedEvent(booking.getId(), userId, allocatedSeats));

        scheduleAutoCancellation(booking.getId() , lockDurationMinutes);
        return BookingHoldResponse.builder()
                .bookingId(booking.getId())
                .bookingRef(BookingRef)
                .seatNumbers(allocatedSeats)
                .totalFare(totalFare)
                .lockExpiresAt(booking.getLockExpiresAt())
                .remainingSeconds((int) ChronoUnit.SECONDS.between(Instant.now(), booking.getLockExpiresAt()))
                .status(BookingStatus.HOLD)
                .build();
    }

     @CircuitBreaker(name = "PaymentService" , fallbackMethod = "confirmBookingFallback")
     @Retry(name = "PaymentService")
     public BookingConfirmResponse confirmBooking(UUID userId , BookingConfirmRequest request) {
         TrainBooking booking = trainBookinRepository.findById(request.getBookingId())
                 .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

         if (booking.getStatus() != BookingStatus.HOLD) {
             throw new BuissnessValidationException("Booking is not on Hold");
         }
         if (booking.getUserId() != userId) {
             throw new BuissnessValidationException("Unauthorized access");
         }
         if (Instant.now().isAfter(booking.getLockExpiresAt())) {
             booking.setStatus(BookingStatus.CANCELLED);
             trainBookinRepository.save(booking);
             releaseSeats(booking.getSchedule().getId(), booking.getSeatNumbers());
             throw new BuissnessValidationException("Booking is expired");
         }
         String paymentId;
         try {
             paymentId = paymentService.processPayment(request.getPaymentMethod(), request.getPaymentToken(), booking.getTotalFare());
         } catch (Exception e) {
             throw new BuissnessValidationException("Payment processing failed");
         }
         eventPublisher.publishEvent(new TrainBookingConfirmedEvent(booking.getId(), userId,
                 booking.getSchedule().getId()));

         return BookingConfirmResponse.builder()
                 .bookinId(booking.getId())
                 .bookingRef(booking.getBookingref())
                 .status(BookingStatus.CONFIRMED)
                 .pnr(booking.getBookingref())
                 .build();

     }

    public BookingConfirmResponse confirmBookingFallback(UUID userId, BookingConfirmRequest request, Exception e) {
        log.error("Payment service fallback triggered for booking: {}", request.getBookingId(), e);
        throw new ResourceNotFoundException("Payment service not available");
    }

    public List<String> allocateSeatsWithLock(Train_Schedule schedule , int requiredSeats , String Bookingref){
        List<String> availableSeats = generateSeatNumbers(schedule.getClasses().getTotalSeats());
        List<String> allocatedSeats = new ArrayList<>();
        UUID scheduleId = schedule.getId();

        for(int i=0 ; i<requiredSeats ; i++){
            String seatnumber = availableSeats.get(i);
            String lockkey = "seat:lock:"+ scheduleId+":"+seatnumber;
            int
            RLock lock = redissonClient.getLock(lockkey);
            try{
                if(lock.tryLock(1,lockDurationMinutes*60L , TimeUnit.SECONDS)){
                    if(createHoldRecord(schedule , seatnumber , Bookingref)){
                        allocatedSeats.add(seatnumber);
                    }
                }
            }catch (Exception e){
                Thread.currentThread().interrupt();
                throw new BuissnessValidationException("Unable to lock seats due to interruption");
            }

        }
        if(allocatedSeats.size()<requiredSeats){
            releaseSeats(scheduleId , allocatedSeats);
            throw new BuissnessValidationException("Could not allocate all required seats");
        }
        return allocatedSeats;
    }

    public String BookingReference()  {
        AtomicLong counter = new AtomicLong(1);

            String datePrefix = DateTimeFormatter.ofPattern("yyyyMMdd").format(LocalDate.now());
            return "TRN" + datePrefix + String.format("%03d", counter.getAndIncrement());

    }
    private BigDecimal CalculateFare(Train_Schedule schedule, List<PassengerDetails> passengers,
                                          Map<Integer, MealPreference> mealPrefs) {
        BigDecimal baseFare = schedule.getClasses().getBaseFare();
        BigDecimal total = baseFare.multiply(BigDecimal.valueOf(passengers.size()));

        // Add meal charges if applicable
        if (mealPrefs != null) {
            long vegMeals = mealPrefs.values().stream().filter(mp -> mp == MealPreference.VEG).count();
            // Add meal charges logic...
        }
        return total;

    }
    private Map<String, String> convertMealPreferences(Map<Integer, MealPreference> prefs, List<String> seats) {
        if (prefs == null) return new HashMap<>();
        Map<String, String> seatPrefs = new HashMap<>();
        for (int i = 0; i < Math.min(prefs.size(), seats.size()); i++) {
            seatPrefs.put(seats.get(i), prefs.get(i).name());
        }
        return seatPrefs;
    }
    private void releaseSeats(UUID scheduleId, List<String> seatNumbers) {
        seatNumbers.forEach(seatNumber -> {
            String lockKey = "seat:lock:" + scheduleId + ":" + seatNumber;
            RLock lock = redissonClient.getLock(lockKey);
            if (lock.isLocked()) {
                lock.forceUnlock();
            }
            seatHoldRepository.deleteByScheduleIdAndSeatNumbers(scheduleId, seatNumber);
        });

    }
    private boolean createHoldRecord(Train_Schedule schedule, String seatNumber, String bookingRef) {
        SeatHold seatLock = SeatHold.builder()
                .schedule(schedule)
                .seatNumber(seatNumber)
                .lockedBy(UUID.randomUUID()) // Will be updated with actual booking ID
                .lockedAt(Instant.now())
                .expiresAt(Instant.now().plus(lockDurationMinutes, ChronoUnit.MINUTES))
                .build();
        seatHoldRepository.save(seatLock);
        return true;
    }
        public static List<String> generateSeatNumbers(int totalSeats) {
            return IntStream.rangeClosed(1, totalSeats)
                    .mapToObj(i -> String.format("A%02d", i))
                    .collect(Collectors.toList());
        }
    

}
