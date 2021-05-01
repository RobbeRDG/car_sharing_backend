package be.kul.rideservice.entity;

import be.kul.rideservice.utils.helperObjects.BillStatusEnum;
import be.kul.rideservice.utils.helperObjects.RideStatusEnum;
import be.kul.rideservice.utils.json.jsonObjects.amqpMessages.billing.BillStatusUpdate;
import be.kul.rideservice.utils.json.jsonObjects.amqpMessages.ride.RideEnd;
import be.kul.rideservice.utils.json.jsonObjects.amqpMessages.ride.RideInitialisation;
import be.kul.rideservice.utils.json.jsonViews.Views;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
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
    private LocalDateTime lastRideStatusUpdate;
    @NotNull
    @Enumerated(EnumType.STRING)
    @JsonView(Views.RideView.Basic.class)
    private RideStatusEnum rideStatus;
    @NotNull
    @JsonView(Views.RideView.Admin.class)
    private LocalDateTime lastBillStatusUpdate;
    @NotNull
    @Enumerated(EnumType.STRING)
    @JsonView(Views.RideView.Basic.class)
    private BillStatusEnum billStatus;

    @OneToMany(mappedBy = "ride")
    @OrderBy("time asc")
    @JsonView(Views.RideView.Full.class)
    private Set<WayPoint> wayPoints;


    public Ride(RideInitialisation rideInitialisation) {
        this.rideId = rideInitialisation.getRideId();
        this.userId = rideInitialisation.getUserId();
        this.carId = rideInitialisation.getCarId();
        this.createdOn = rideInitialisation.getCreatedOn();
        this.startedOn = rideInitialisation.getStartedOn();
        this.lastRideStatusUpdate = rideInitialisation.getLastStateUpdate();
        this.rideStatus = rideInitialisation.getCurrentState();
        this.billStatus = BillStatusEnum.UNDEFINED;
        this.lastBillStatusUpdate = LocalDateTime.now();
        wayPoints = new HashSet<WayPoint>();
    }


    public void endRide(RideEnd rideEnd) {
        this.rideStatus = rideEnd.getCurrentStatus();
        this.finishedOn = rideEnd.getFinishedOn();
        this.lastRideStatusUpdate = rideEnd.getCreatedOn();
    }

    public void updateBillStatus(BillStatusUpdate billStatusUpdate) {
        //Only set the new status if it is more recent than the current one
        if (billStatusUpdate.getCreatedOn().isAfter(lastBillStatusUpdate)) {
            this.billStatus = billStatusUpdate.getBillStatus();
            this.lastBillStatusUpdate = billStatusUpdate.getCreatedOn();
        }
    }

    public boolean allowsNewDamageReport() {
        return (!rideStatus.equals(RideStatusEnum.FINISHED));
    }
}
