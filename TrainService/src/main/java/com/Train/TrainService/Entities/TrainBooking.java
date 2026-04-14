package com.Train.TrainService.Entities;

import com.Train.TrainService.Enum.BookingStatus;
import com.Train.TrainService.dto.PassengerDetails;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TrainBooking {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(unique = true , nullable = false)
    private String Bookingref;

    private UUID userId;

    @ManyToOne
    @JoinColumn(name = "schedule_id")
    private Train_Schedule schedule;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb" , nullable = false)
    private List<PassengerDetails> details;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb" , nullable = false)
    private List<String> seatNumbers;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb" )
    private Map<String , String> mealPreferences ;

    private BigDecimal TotalFare;

    @Enumerated(EnumType.STRING)
    private BookingStatus status;

    private String paymentId;
    private Instant lockExpiresAt;
    private Instant bookedAt;
    private Instant confirmedAt;
}
