package com.Train.TrainService.Repository;

import com.Train.TrainService.Entities.Train_Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TrainScheduleRepository extends JpaRepository<Train_Schedule , UUID> {
    Optional<Train_Schedule> findByRoutesAndClassesAndJourneyDate(UUID route_id , UUID Trainclass_id , LocalDate date);
}
