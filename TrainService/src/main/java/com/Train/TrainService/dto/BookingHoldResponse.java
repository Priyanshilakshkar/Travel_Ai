package com.Train.TrainService.dto;

import com.Train.TrainService.Enum.BookingStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
public class BookingHoldResponse {
    private UUID bookingId;
    private String bookingRef;
    private List<String> seatNumbers;
    private BigDecimal totalFare;
    private Instant lockExpiresAt;
    private Integer remainingSeconds;
    private BookingStatus status;
}
