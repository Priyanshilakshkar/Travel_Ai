package com.Train.TrainService.Entities;

import com.Train.TrainService.Enum.TrainType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.AnyDiscriminatorImplicitValues;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Train {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private String trainNumber;
    private String trainName;

    @Enumerated
    private TrainType type;

    @OneToMany(mappedBy = "train")
    private List<Train_routes> routes = new ArrayList<>();
}
