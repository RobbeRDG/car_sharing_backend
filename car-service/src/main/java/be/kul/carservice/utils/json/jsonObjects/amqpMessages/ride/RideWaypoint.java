package be.kul.carservice.utils.json.jsonObjects.amqpMessages.ride;

import be.kul.carservice.utils.json.jsonObjects.amqpMessages.AmqpMessage;
import be.kul.carservice.utils.json.jsonObjects.amqpMessages.car.CarStatusUpdate;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.locationtech.jts.geom.Point;
import org.n52.jackson.datatype.jts.GeometryDeserializer;
import org.n52.jackson.datatype.jts.GeometrySerializer;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RideWaypoint extends AmqpMessage {
    private long rideId;
    @JsonSerialize( using = GeometrySerializer.class)
    @JsonDeserialize( contentUsing = GeometryDeserializer.class)
    private Point location;
    private LocalDateTime time;

    public RideWaypoint(CarStatusUpdate carStatusUpdate, long rideId) {
        super();
        this.rideId=rideId;
        time= carStatusUpdate.getCreatedOn();
        location= carStatusUpdate.getLocation();
    }
}
