package be.kul.rideservice.entity;

import be.kul.rideservice.utils.helperObjects.RideStateEnum;
import be.kul.rideservice.utils.json.jsonObjects.amqpMessages.ride.RideEnd;
import be.kul.rideservice.utils.json.jsonObjects.amqpMessages.ride.RideInitialisation;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Ride {
    @Id
    private long rideId;
    @NotNull
    private long carId;
    @NotNull
    private String userId;

    @NotNull
    private Timestamp createdOn;
    private Timestamp startedOn;
    private Timestamp finishedOn;
    @NotNull
    private Timestamp lastStateUpdate;
    @NotNull
    @Enumerated(EnumType.STRING)
    private RideStateEnum currentState;

    @OneToMany
    @JoinColumn(name = "ride_id")
    private Set<WayPoint> wayPoints;
    @Transient
    private double distanceTraveledInKm;

    public Ride(RideInitialisation rideInitialisation) {
        this.rideId = rideInitialisation.getRideId();
        this.userId = rideInitialisation.getUserId();
        this.carId = rideInitialisation.getCarId();
        this.createdOn = rideInitialisation.getCreatedOn();
        this.startedOn = rideInitialisation.getStartedOn();
        this.finishedOn = rideInitialisation.getFinishedOn();
        this.lastStateUpdate = rideInitialisation.getLastStateUpdate();
        this.currentState = rideInitialisation.getCurrentState();
        wayPoints = new HashSet<WayPoint>();


    }

    public long getRideId() {
        return rideId;
    }

    public long getCarId() {
        return carId;
    }

    public String getUserId() {
        return userId;
    }

    public Timestamp getCreatedOn() {
        return createdOn;
    }

    public Timestamp getStartedOn() {
        return startedOn;
    }

    public Timestamp getFinishedOn() {
        return finishedOn;
    }

    public Timestamp getLastStateUpdate() {
        return lastStateUpdate;
    }

    public RideStateEnum getCurrentState() {
        return currentState;
    }

    public Set<WayPoint> getWayPoints() {
        return wayPoints;
    }

    public double getDistanceTraveledInKm() {
        return distanceTraveledInKm;
    }

    public void addWaypoint(WayPoint wayPoint) {
        if ((currentState== RideStateEnum.FINISHED || currentState== RideStateEnum.PAID) && wayPoint.getTime().after(finishedOn)){
            throw new IllegalArgumentException("Couldn't add waypoint: Waypoint created after ride had ended");
        }
        wayPoints.add(wayPoint);
    }

    public void updateState(RideEnd rideEnd) {
        this.currentState = rideEnd.getCurrentState();
        this.finishedOn = rideEnd.getFinishedOn();
        this.lastStateUpdate = rideEnd.getLastStateUpdate();
    }
}
