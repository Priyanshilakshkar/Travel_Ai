package com.Train.TrainService.Events;


import java.util.List;
import java.util.UUID;

public class TrainSeatLockedEvent {

    private final UUID bookingId;
    private final UUID userId;
    private final List<String> seatNumbers;

    public TrainSeatLockedEvent(UUID bookingId, UUID userId, List<String> seatNumbers) {
        this.bookingId = bookingId;
        this.userId = userId;
        this.seatNumbers = seatNumbers;
    }

    public UUID getBookingId() {
        return bookingId;
    }

    public UUID getUserId() {
        return userId;
    }

    public List<String> getSeatNumbers() {
        return seatNumbers;
    }
}

