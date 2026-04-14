package com.Train.TrainService.dto;

import com.Train.TrainService.Enum.TrainType;
import lombok.Builder;

import java.time.Duration;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Builder
public class TrainSearchResponse {
    private UUID scheduleId;
    private String trainNumber;
    private String trainName;
    private TrainType trainType;
    private String source;
    private String destination;
    private LocalTime departureTime;
    private LocalTime arrivalTime;
    private Duration duration;
    private List<ClassAvailability> classAvailabilities;
}
