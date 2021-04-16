package be.kul.rideservice.entity;

import be.kul.rideservice.utils.helperObjects.RideStateEnum;
import be.kul.rideservice.utils.json.jsonObjects.amqpMessages.ride.RideEnd;
import be.kul.rideservice.utils.json.jsonObjects.amqpMessages.ride.RideInitialisation;
import be.kul.rideservice.utils.json.jsonViews.Views;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.messaging.handler.annotation.SendTo;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
public class Ride {
    @Id
    @JsonView(Views.RideView.Basic.class)
    private long rideId;
    @NotNull
    @JsonView(Views.RideView.Basic.class)
    private long carId;
    @NotNull
    @JsonView(Views.RideView.Basic.class)
    private String userId;

    @NotNull
    @JsonView(Views.RideView.Admin.class)
    private LocalDateTime createdOn;
    @JsonView(Views.RideView.Basic.class)
    private LocalDateTime startedOn;
    @JsonView(Views.RideView.Basic.class)
    private LocalDateTime finishedOn;
    @NotNull
    @JsonView(Views.RideView.Admin.class)
    private LocalDateTime lastStateUpdate;
    @NotNull
    @Enumerated(EnumType.STRING)
    @JsonView(Views.RideView.Basic.class)
    private RideStateEnum currentState;

    @OneToMany(mappedBy = "ride")
    @JsonView(Views.RideView.Full.class)
    private Set<WayPoint> wayPoints;


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

    public LocalDateTime getCreatedOn() {
        return createdOn;
    }

    public LocalDateTime getStartedOn() {
        return startedOn;
    }

    public LocalDateTime getFinishedOn() {
        return finishedOn;
    }

    public LocalDateTime getLastStateUpdate() {
        return lastStateUpdate;
    }

    public RideStateEnum getCurrentState() {
        return currentState;
    }

    public Set<WayPoint> getWayPoints() {
        return wayPoints;
    }


    public void addWaypoint(WayPoint wayPoint) {
        if ((currentState== RideStateEnum.FINISHED || currentState== RideStateEnum.PAID) && wayPoint.getTime().isAfter(finishedOn)){
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
