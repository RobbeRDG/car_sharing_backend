package be.kul.carservice.cars.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.UniqueElements;
import org.locationtech.jts.geom.Point;
import org.n52.jackson.datatype.jts.GeometryDeserializer;
import org.n52.jackson.datatype.jts.GeometrySerializer;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Car {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private UUID carId;

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
    private boolean available;
    @NotNull
    private boolean reserved;
    @NotNull
    private boolean inUse;
    @NotNull
    private boolean damaged;
    @NotNull
    private int remainingFuelInKilometers;
    @NotNull
    @JsonSerialize( using = GeometrySerializer.class)
    @JsonDeserialize( contentUsing = GeometryDeserializer.class)
    private Point location;

    public String getNumberPlate() {
        return numberPlate;
    }
}
