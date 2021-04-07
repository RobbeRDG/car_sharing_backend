package be.kul.carservice.utils.json.jsonObjects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.locationtech.jts.geom.Point;
import org.n52.jackson.datatype.jts.GeometryDeserializer;
import org.n52.jackson.datatype.jts.GeometrySerializer;

import java.sql.Timestamp;

public class RideWaypoint {
    private long rideId;
    private long carId;
    private Timestamp createdOn;
    @JsonSerialize( using = GeometrySerializer.class)
    @JsonDeserialize( contentUsing = GeometryDeserializer.class)
    private Point location;

    public RideWaypoint(CarStateUpdate carStateUpdate, long rideId) {
        this.rideId=rideId;
        carId=carStateUpdate.getCarId();
        createdOn=carStateUpdate.getCreatedOn();
        location=carStateUpdate.getLocation();
    }
}
