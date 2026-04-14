package com.Train.TrainService.Service;

import com.Train.TrainService.Entities.Station;
import com.Train.TrainService.Entities.Train;
import com.Train.TrainService.Exceptions.ResourceNotFoundException;
import com.Train.TrainService.Repository.ScheduleRepository;
import com.Train.TrainService.Repository.TrainRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class TrainService {

    @Autowired
    private TrainRepository trainRepository;

    @Autowired
    private StationRepository stationRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    public List<Train> searchTrains(String source , String destination , LocalDate date){
        Station source1 = stationRepository.findByName(source)
                .orElseThrow(()->new ResourceNotFoundException("Station not found"));

        Station destination1 = stationRepository.findByName(destination)
                .orElseThrow(()->new ResourceNotFoundException("Station not found"));


        List<Train> trains = trainRepository.finfBySourceAndDestination(source1.getId() , destination1.getId())
                .orElseThrow(()->new ResourceNotFoundException("No train found"));

        return trains.stream()
                .filter(t->
                        scheduleRepository.existsByTrainIdAndTravelDate(t.getTrain_id(), date)
                        ).toList();



    }

}
