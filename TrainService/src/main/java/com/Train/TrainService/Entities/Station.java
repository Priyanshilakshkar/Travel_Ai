package com.Train.TrainService.Entities;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Station {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private String Name;
    private String Code;
    private String city;
    private String location;

    @OneToMany(mappedBy = "sourceStation")
    private List<Train> trainStarting = new ArrayList<>();

    @OneToMany(mappedBy = "destinationStation")
    private List<Train> trainEnding = new ArrayList<>();

    @OneToMany(mappedBy = "station")
    private List<Train_routes> routes = new ArrayList<>();
}
