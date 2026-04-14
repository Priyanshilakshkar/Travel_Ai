package com.Train.TrainService.Repository;

import com.Train.TrainService.Entities.TrainBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TrainBookinRepository extends JpaRepository<TrainBooking , UUID> {
}
