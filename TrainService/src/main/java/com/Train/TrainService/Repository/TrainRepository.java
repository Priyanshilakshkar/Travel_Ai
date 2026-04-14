package com.Train.TrainService.Repository;

import com.Train.TrainService.Entities.Train;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TrainRepository extends JpaRepository<Train , UUID> {

    @Query("Select t From Train t Where t.source_id = :source And t.destination_id = : destination")
    Optional<List<Train>> finfBySourceAndDestination(UUID source , UUID destination);
}
