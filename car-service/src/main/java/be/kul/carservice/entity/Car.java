package be.kul.carservice.entity;

import be.kul.carservice.utils.json.jsonViews.Views;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.locationtech.jts.geom.Point;
import org.n52.jackson.datatype.jts.GeometryDeserializer;
import org.n52.jackson.datatype.jts.GeometrySerializer;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Car {

    @Id
    @GeneratedValue(generator = "car-sequence-generator")
    @GenericGenerator(
            name = "car-sequence-generator",
            strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {
                    @Parameter(name = "sequence_name", value = "car_sequence"),
                    @Parameter(name = "initial_value", value = "1"),
                    @Parameter(name = "increment_size", value = "1")
            }
    )
    @JsonView(Views.CarView.Basic.class)
    private long carId;
    @Version
    @JsonIgnore
    private long version;

    //Car information
    @NotNull
    @JsonView(Views.CarView.Basic.class)
    private int numberOfSeats;
    @NotNull
    @JsonView(Views.CarView.Basic.class)
    private String color;
    @NotNull
    @JsonView(Views.CarView.Basic.class)
    private String manufacturer;
    @NotNull
    @JsonView(Views.CarView.Basic.class)
    private String model;
    @NotNull
    @JsonView(Views.CarView.Basic.class)
    private String yearOfManufacture;
    @NotNull
    @Column(unique=true)
    @JsonView(Views.CarView.Basic.class)
    private String numberPlate;

    //Car status
    @NotNull
    @JsonView(Views.CarView.Full.class)
    private LocalDateTime lastStateUpdate;
    @NotNull
    @JsonView(Views.CarView.Full.class)
    private boolean online; //If the car is online
    @NotNull
    @JsonView(Views.CarView.Full.class)
    private boolean active; //If the car is activated for requests
    @NotNull
    @JsonView(Views.CarView.Ride.class)
    private boolean carDoorsLocked;
    @NotNull
    @JsonView(Views.CarView.Full.class)
    private boolean inMaintenance;
    @NotNull
    @JsonView(Views.CarView.Basic.class)
    private int remainingFuelInKilometers;
    @NotNull
    @JsonSerialize( using = GeometrySerializer.class)
    @JsonDeserialize( contentUsing = GeometryDeserializer.class)
    @JsonView(Views.CarView.Basic.class)
    private Point location;
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "reservation_id")
    @JsonView(Views.CarView.Reserved.class)
    private Reservation currentReservation;
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "ride_id")
    @JsonView(Views.CarView.Ride.class)
    private Ride currentRide;


    public String getNumberPlate() {
        return numberPlate;
    }

    public void setRemainingFuelInKilometers(int remainingFuelInKilometers) {
        this.remainingFuelInKilometers = remainingFuelInKilometers;
    }

    public void setLocation(Point location) {
        this.location = location;
    }

    public boolean isInMaintenance() {
        return inMaintenance;
    }

    public Reservation getCurrentReservation() {
        return currentReservation;
    }

    public void setCurrentReservation(Reservation currentReservation) {
        this.currentReservation = currentReservation;
    }

    public boolean canBeReserved() {
        if (
                !online
                || !active
                || inMaintenance
                || currentRide != null
                || currentReservation != null
        ) return false;
        else return true;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public void setLastStateUpdate(LocalDateTime timestamp) {
        this.lastStateUpdate = timestamp;
    }

    public boolean canBeRidden(String userId) {
        if (
                canBeReserved()
                || (currentReservation!=null && currentReservation.getUserId().equals(userId))
            ) return true;
        else return false;
    }

    public void setCurrentRide(Ride currentRide) {
        this.currentRide = currentRide;
    }

    public long getCarId() {
        return carId;
    }

    public Ride getCurrentRide() {
        return currentRide;
    }

    public void setCarDoorsLocked(boolean lock) {
        new Timestamp(System.currentTimeMillis());
        carDoorsLocked=lock;
    }

    public boolean isRiddenBy(String userId) {
        return currentRide.getUserId().equals(userId);
    }

    public boolean isBeingRidden() {
        return currentRide!=null;
    }

}
