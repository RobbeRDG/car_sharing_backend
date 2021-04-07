package be.kul.carservice.utils.json.jsonObjects;

import be.kul.carservice.entity.Ride;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.bouncycastle.util.Times;
import org.springframework.beans.factory.annotation.Value;

import java.sql.Timestamp;

public class CarRideRequest {
    @Value("${spring.configurations.carRideRequest.expirationTimeInMilliseconds}")
    private int expirationTimeInMilliseconds;
    private long rideId;
    private long carId;
    private Timestamp createdOn;

    public CarRideRequest(Ride ride) {
        this.rideId = ride.getRideId();
        this.carId = ride.getCar().getCarId();
        this.createdOn = ride.getCreatedOn();
    }

    public String getCarIdString() {
        return String.valueOf(carId);
    }

    public long getCarId() {
        return carId;
    }

    public int getExpirationTimeInMilliseconds() {
        return expirationTimeInMilliseconds;
    }

    public long getRideId() {
        return rideId;
    }

    public Timestamp getCreatedOn() {
        return createdOn;
    }
}
