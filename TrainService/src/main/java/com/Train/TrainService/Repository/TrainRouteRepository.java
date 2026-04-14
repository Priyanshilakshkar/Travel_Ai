package com.Train.TrainService.Repository;

import com.Train.TrainService.Entities.Train_routes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TrainRouteRepository extends JpaRepository<Train_routes , UUID> {
    Optional<List<Train_routes>> findBySourceStationAndDestinationStation(String source , String destination);
}
