package com.Train.TrainService.Repository;

import com.Train.TrainService.Entities.SeatHold;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SeatHoldRepository extends JpaRepository<SeatHold , UUID> {

    @Query("Delete From SeatHold sl Where sl.schedule_id = :scheduleId And sl.seatNumber = : seatNumber")
    void deleteByScheduleIdAndSeatNumbers(UUID scheduleId , String seatNumber);
}
