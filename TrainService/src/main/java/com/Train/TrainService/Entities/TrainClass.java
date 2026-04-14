package com.Train.TrainService.Entities;

import com.Train.TrainService.Enum.ClassType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.internal.build.AllowReflection;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TrainClass {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "route_id")
    private Train_routes route;

    @Enumerated(EnumType.STRING)
    private ClassType type;

    private Integer totalSeats;
    private BigDecimal BaseFare;

    @OneToMany(mappedBy = "classes")
    private List<Train_Schedule> schedules;

}
