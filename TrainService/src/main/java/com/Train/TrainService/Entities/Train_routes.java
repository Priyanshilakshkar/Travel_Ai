package com.Train.TrainService.Entities;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;
import org.hibernate.type.SqlTypes;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Train_routes {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "train_id")
    private Train train;

    private String sourceStation;
    private String destinationStation;
    private LocalTime departure;
    private LocalTime arrival;
    private Duration duration;
    private Integer distanceKm;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private List<String> operatsOn = new ArrayList<>();

    @OneToMany(mappedBy = "routes")
    List<TrainClass> cLasses = new ArrayList<>();

    @OneToMany(mappedBy = "routes")
    private List<Train_Schedule> schedules;
}
