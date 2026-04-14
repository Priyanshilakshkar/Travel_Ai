package com.Train.TrainService.Repository;

import com.Train.TrainService.Entities.Train_Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.UUID;

public interface ScheduleRepository extends JpaRepository<Train_Schedule , UUID> {
    boolean existsByTrainIdAndTravelDate(UUID train_id , LocalDate date);
}
