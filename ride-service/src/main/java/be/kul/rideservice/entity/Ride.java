package be.kul.rideservice.entity;

import be.kul.rideservice.utils.helperObjects.RideState;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

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
    @NotNull
    private Timestamp startedOn;
    @NotNull
    private Timestamp finishedOn;
    @NotNull
    private Timestamp lastStateUpdate;
    @NotNull
    @Enumerated(EnumType.STRING)
    private RideState currentState;

    @OneToMany
    @JoinColumn(name = "ride_id")
    private WayPoint wayPoint;
    @Transient
    private double distanceTraveledInKm;


}
