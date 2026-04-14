package com.Train.TrainService.dto;

import com.Train.TrainService.Enum.ClassType;
import com.Train.TrainService.Enum.ScheduleStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class ClassAvailability {
    private ClassType classType;
    private Integer availableSeats;
    private Integer fare;
    private ScheduleStatus status;
}
