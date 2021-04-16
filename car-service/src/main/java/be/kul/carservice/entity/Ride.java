package be.kul.carservice.entity;

import be.kul.carservice.utils.helperObjects.RideStateEnum;
import be.kul.carservice.utils.json.jsonViews.Views;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class Ride {
    @Id
    @GeneratedValue(generator = "ride-sequence-generator")
    @GenericGenerator(
            name = "ride-sequence-generator",
            strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {
                    @Parameter(name = "sequence_name", value = "ride_sequence"),
                    @Parameter(name = "initial_value", value = "1"),
                    @Parameter(name = "increment_size", value = "1")
            }
    )
    @JsonView(Views.CarView.Ride.class)
    private long rideId;
    @NotNull
    @JsonView(Views.CarView.Ride.class)
    private String userId;
    @NotNull
    @JsonIgnore
    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "car_id")
    private Car car;

    @NotNull
    @JsonView(Views.CarView.Ride.class)
    private Timestamp createdOn;
    @JsonView(Views.CarView.Ride.class)
    private Timestamp startedOn;
    @JsonView(Views.CarView.Ride.class)
    private Timestamp finishedOn;
    @NotNull
    @JsonView(Views.CarView.Ride.class)
    private Timestamp lastStateUpdate;
    @NotNull
    @Enumerated(EnumType.STRING)
    @JsonView(Views.CarView.Ride.class)
    private RideStateEnum currentState;


    public Ride(@NotNull String userId, @NotNull Car car) {
        this.userId = userId;
        this.car = car;
        Timestamp now = new Timestamp(System.currentTimeMillis());
        this.createdOn = now;
        this.lastStateUpdate= now;
        this.currentState = RideStateEnum.WAITING_FOR_CAR_ACKNOWLEDGEMENT;
    }

    public long getRideId() {
        return rideId;
    }

    public String getUserId() {
        return userId;
    }

    public Car getCar() {
        return car;
    }

    public Timestamp getCreatedOn() {
        return createdOn;
    }

    public RideStateEnum getCurrentState() {
        return currentState;
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

    public void start() {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        this.startedOn = now;
        this.currentState = RideStateEnum.IN_PROGRESS;
        this.lastStateUpdate= now;
    }

    public void end() {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        this.finishedOn = now;
        this.currentState = RideStateEnum.FINISHED;
        this.lastStateUpdate= now;
    }

    public void cancel() {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        this.currentState = RideStateEnum.CANCELED;
        this.lastStateUpdate= now;
    }
}
