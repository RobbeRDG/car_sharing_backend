package be.kul.rideservice.utils.json.jsonObjects.amqpMessages.payment;

import be.kul.rideservice.entity.Ride;

import java.sql.Timestamp;

public class PaymentInitialisation {
    private long rideId;
    private long carId;
    private String userId;
    private Timestamp startedOn;
    private Timestamp finishedOn;

    public PaymentInitialisation(Ride ride) {
        this.rideId = ride.getRideId();
        this.carId = ride.getCarId();
        this.userId = ride.getUserId();
        this.startedOn = ride.getStartedOn();
        this.finishedOn = ride.getFinishedOn();
    }

}
