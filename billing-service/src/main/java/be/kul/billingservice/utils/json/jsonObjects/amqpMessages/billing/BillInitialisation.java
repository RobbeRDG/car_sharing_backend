package be.kul.billingservice.utils.json.jsonObjects.amqpMessages.billing;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BillInitialisation {
    private long rideId;
    private long carId;
    private String userId;
    private LocalDateTime startedOn;
    private LocalDateTime finishedOn;

    public long getRideId() {
        return rideId;
    }

    public long getCarId() {
        return carId;
    }

    public String getUserId() {
        return userId;
    }

    public LocalDateTime getStartedOn() {
        return startedOn;
    }

    public LocalDateTime getFinishedOn() {
        return finishedOn;
    }
}
