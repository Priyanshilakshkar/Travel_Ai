package com.Train.TrainService.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Coaches {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "train_id")
    private Train train;

    private String coach_no;

    private String coach_type;

    private Integer seat_count;

    @OneToMany(mappedBy = "coaches")
    private List<Seat> seats = new ArrayList<>();


}
