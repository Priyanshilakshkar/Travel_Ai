package com.Train.TrainService.Entities;

import com.Train.TrainService.Enum.ScheduleStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.security.PrivateKey;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Train_Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne
    @JoinColumn(name= "route_id")
    private Train_routes routes;

    @ManyToOne
    @JoinColumn(name = "class_id")
    private TrainClass classes;

    private LocalDate journeyDate;
    private Integer availableSeats;

    @Enumerated(EnumType.STRING)
    private ScheduleStatus status;

    @Version
    private Long version;

    @OneToMany(mappedBy = "schedule")
    private List<SeatHold> holds;

    @OneToMany(mappedBy = "schedule")
    private List<TrainBooking> bookings;


}
