package com.Train.TrainService.Entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeatHold {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "schedule_id")
    private Train_Schedule schedule;

    private String seatNumber ;
    private UUID lockedBy;
    private Instant lockedAt;
    private Instant expiresAt;
}
