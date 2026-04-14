package com.Train.TrainService.dto;

import com.Train.TrainService.Enum.BookingStatus;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Data
@Getter
@Setter
@Builder
public class BookingConfirmResponse {
    private UUID bookinId;
    private String bookingRef;
    private BookingStatus status;
    private String pnr;
}
