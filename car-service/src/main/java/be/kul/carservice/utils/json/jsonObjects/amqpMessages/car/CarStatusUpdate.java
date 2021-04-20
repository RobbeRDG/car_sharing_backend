package be.kul.carservice.utils.json.jsonObjects.amqpMessages.car;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.locationtech.jts.geom.Point;
import org.n52.jackson.datatype.jts.GeometryDeserializer;
import org.n52.jackson.datatype.jts.GeometrySerializer;

import java.io.Serializable;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CarStatusUpdate implements Serializable {
    @JsonIgnore
    private long carId;
    private LocalDateTime createdOn;
    private boolean online;
    private int remainingFuelInKilometers;
    @JsonSerialize( using = GeometrySerializer.class)
    @JsonDeserialize( contentUsing = GeometryDeserializer.class)
    private Point location;

    public int getRemainingFuelInKilometers() {
        return remainingFuelInKilometers;
    }

    public Point getLocation() {
        return location;
    }

    public void setCarId(long carId) {
        this.carId=carId;
    }

    public long getCarId() {
        return carId;
    }

    public LocalDateTime getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(LocalDateTime createdOn) {
        this.createdOn = createdOn;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public void setRemainingFuelInKilometers(int remainingFuelInKilometers) {
        this.remainingFuelInKilometers = remainingFuelInKilometers;
    }

    public void setLocation(Point location) {
        this.location = location;
    }
}
