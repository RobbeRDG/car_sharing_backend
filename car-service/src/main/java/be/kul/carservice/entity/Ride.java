package be.kul.carservice.entity;

import be.kul.carservice.utils.helperObjects.RideStatusEnum;
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
import java.time.LocalDateTime;

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
    private LocalDateTime createdOn;
    @JsonView(Views.CarView.Ride.class)
    private LocalDateTime startedOn;
    @JsonView(Views.CarView.Ride.class)
    private LocalDateTime finishedOn;
    @NotNull
    @JsonView(Views.CarView.Ride.class)
    private LocalDateTime lastStatusUpdate;
    @NotNull
    @Enumerated(EnumType.STRING)
    @JsonView(Views.CarView.Ride.class)
    private RideStatusEnum currentStatus;


    public Ride(@NotNull String userId, @NotNull Car car) {
        this.userId = userId;
        this.car = car;
        LocalDateTime now = LocalDateTime.now();
        this.createdOn = now;
        this.lastStatusUpdate = now;
        this.currentStatus = RideStatusEnum.WAITING_FOR_CAR_ACKNOWLEDGEMENT;
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

    public LocalDateTime getCreatedOn() {
        return createdOn;
    }

    public RideStatusEnum getCurrentStatus() {
        return currentStatus;
    }

    public LocalDateTime getStartedOn() {
        return startedOn;
    }

    public LocalDateTime getFinishedOn() {
        return finishedOn;
    }

    public LocalDateTime getLastStatusUpdate() {
        return lastStatusUpdate;
    }

    public void start() {
        LocalDateTime now = LocalDateTime.now();
        this.startedOn = now;
        this.currentStatus = RideStatusEnum.IN_PROGRESS;
        this.lastStatusUpdate = now;
    }

    public void end() {
        LocalDateTime now = LocalDateTime.now();
        this.finishedOn = now;
        this.currentStatus = RideStatusEnum.FINISHED;
        this.lastStatusUpdate = now;
    }

    public void cancel() {
        LocalDateTime now = LocalDateTime.now();
        this.currentStatus = RideStatusEnum.CANCELED;
        this.lastStatusUpdate = now;
    }
}
