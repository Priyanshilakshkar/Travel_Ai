package com.Train.TrainService.dto;

import com.Train.TrainService.Enum.ClassType;
import com.Train.TrainService.Enum.MealPreference;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@Builder
public class BookingHoldRequest {
    @NotNull
    private UUID scheduleId;

    @NotEmpty
    @Size(min = 1, max = 6)
    private List<PassengerDetails> passengers;

    @NotNull
    private ClassType classType;

    private Map<Integer, MealPreference> mealPreferences;

}
