package be.kul.rideservice.utils.json.jsonObjects.amqpMessages.payment;

import be.kul.rideservice.entity.Ride;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class BillInitialisation {
    private long rideId;
    private long carId;
    private String userId;
    private Timestamp startedOn;
    private Timestamp finishedOn;

    public BillInitialisation(Ride ride) {
        this.rideId = ride.getRideId();
        this.carId = ride.getCarId();
        this.userId = ride.getUserId();
        this.startedOn = ride.getStartedOn();
        this.finishedOn = ride.getFinishedOn();
    }

}
