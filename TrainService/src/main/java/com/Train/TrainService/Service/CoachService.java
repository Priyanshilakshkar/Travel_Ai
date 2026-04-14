package com.Train.TrainService.Service;

import com.Train.TrainService.Entities.Coaches;
import com.Train.TrainService.Exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CoachService {

    @Autowired
    private CoachRepository coachRepository;

    public List<Coaches> getCoaches(UUID train_id){
        List<Coaches> coaches =coachRepository.findByTrainId(train_id)
                .orElseThrow(()->new ResourceNotFoundException("Coaches not found"));

        return coaches;
    }

    public int checkAvailability(UUID train_id , String coach_type){
        return coachRepository.availability(train_id,coach_type);
    }
}
