package be.kul.carservice.entity;

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
    private long carId;
    @Version
    private long version;

    //Car information
    @NotNull
    private int numberOfSeats;
    @NotNull
    private String color;
    @NotNull
    private String manufacturer;
    @NotNull
    private String model;
    @NotNull
    private String yearOfManufacture;
    @NotNull
    @Column(unique=true)
    private String numberPlate;

    //Car state
    @NotNull
    private boolean inMaintenance;
    @NotNull
    private int remainingFuelInKilometers;
    @NotNull
    @JsonSerialize( using = GeometrySerializer.class)
    @JsonDeserialize( contentUsing = GeometryDeserializer.class)
    private Point location;
    @OneToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "reservation_id")
    private Reservation currentReservation;
    @OneToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "ride_id")
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

    public boolean isFree() {
        if (
                inMaintenance
                || currentRide != null
                || currentReservation != null
        ) return false;
        else return true;
    }
}
