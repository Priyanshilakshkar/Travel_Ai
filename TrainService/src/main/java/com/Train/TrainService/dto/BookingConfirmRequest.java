package com.Train.TrainService.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class BookingConfirmRequest {
    @NotNull
    private UUID bookingId;

    @NotBlank
    private String paymentMethod; // RAZORPAY, STRIPE

    @NotBlank
    private String paymentToken;
}
