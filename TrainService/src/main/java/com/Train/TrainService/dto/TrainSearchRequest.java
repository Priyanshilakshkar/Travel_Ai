package com.Train.TrainService.dto;

import com.Train.TrainService.Enum.ClassType;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class TrainSearchRequest {
    @NotBlank
    private String source;

    @NotBlank
    private String destination;

    @NotNull
    @Future
    private LocalDate journeyDate;

    private ClassType preferredClass;
}
