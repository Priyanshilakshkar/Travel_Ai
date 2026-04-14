package com.Train.TrainService.Service;

import com.Train.TrainService.Entities.TrainClass;
import com.Train.TrainService.Entities.Train_Schedule;
import com.Train.TrainService.Entities.Train_routes;
import com.Train.TrainService.Exceptions.ResourceNotFoundException;
import com.Train.TrainService.Repository.TrainRouteRepository;
import com.Train.TrainService.Repository.TrainScheduleRepository;
import com.Train.TrainService.dto.ClassAvailability;
import com.Train.TrainService.dto.TrainSearchRequest;
import com.Train.TrainService.dto.TrainSearchResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class TrainSearchService {
    @Autowired
    private TrainRouteRepository trainRouteRepository;

    @Autowired
    private TrainScheduleRepository trainScheduleRepository;

    public List<TrainSearchResponse> TrainSearch(TrainSearchRequest request){
        List<Train_routes> routes = trainRouteRepository.findBySourceStationAndDestinationStation(request.getSource() , request.getDestination())
                .orElseThrow(()->new ResourceNotFoundException("Train not found"));

        List<TrainSearchResponse> results = new ArrayList<>();
        for(Train_routes route : routes){
            String dayOfWeek = request.getJourneyDate().getDayOfWeek().toString();
            if(!route.getOperatsOn().contains(dayOfWeek)){
                continue;
            }
            List<ClassAvailability> classAvailabilities = new ArrayList<>();

            for(TrainClass classes : route.getCLasses()){
                Train_Schedule schedule = trainScheduleRepository.findByRoutesAndClassesAndJourneyDate(
                        route.getId(),
                        classes.getId(),
                        request.getJourneyDate()
                ).orElseGet(() -> createScheduleForDate(route, classes , request.getJourneyDate()));
                classAvailabilities.add(ClassAvailability.builder()
                        .classType(classes.getType())
                        .availableSeats(schedule.getAvailableSeats())
                        .fare(classes.getBaseFare())
                        .status(schedule.getStatus())
                        .build());
            }
            results.add(TrainSearchResponse.builder()
                    .trainNumber(route.getTrain().getTrainNumber())
                    .trainName(route.getTrain().getTrainName())
                    .trainType(route.getTrain().getType())
                    .source(route.getSourceStation())
                    .destination(route.getDestinationStation())
                    .departureTime(route.getDeparture())
                    .arrivalTime(route.getArrival())
                    .duration(route.getDuration())
                    .classAvailabilities(classAvailabilities)
                    .build());
        }
          return results;
        }
    public Train_Schedule createScheduleForDate(Train_routes route , TrainClass trainClass , LocalDate date ){
        Train_Schedule schedule = new Train_Schedule();
        schedule.setClasses(trainClass);
        schedule.setJourneyDate(date);
        schedule.setRoutes(route);
        return schedule;
    }


    }


