package be.kul.rideservice.utils.json.jsonObjects;

import be.kul.rideservice.utils.helperObjects.RideState;

import java.sql.Timestamp;

public class RideCreation {
    private long rideId;
    private long carId;
    private String userId;
    private Timestamp createdOn;
    private RideState currentState;
}
