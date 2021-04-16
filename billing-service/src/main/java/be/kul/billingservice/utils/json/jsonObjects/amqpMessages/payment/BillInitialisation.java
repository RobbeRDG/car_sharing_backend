package be.kul.billingservice.utils.json.jsonObjects.amqpMessages.payment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BillInitialisation {
    private long rideId;
    private long carId;
    private String userId;
    private Timestamp startedOn;
    private Timestamp finishedOn;

    public long getRideId() {
        return rideId;
    }

    public long getCarId() {
        return carId;
    }

    public String getUserId() {
        return userId;
    }

    public Timestamp getStartedOn() {
        return startedOn;
    }

    public Timestamp getFinishedOn() {
        return finishedOn;
    }
}
