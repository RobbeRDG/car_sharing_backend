package be.kul.rideservice.entity;

import be.kul.rideservice.utils.json.jsonObjects.amqpMessages.ride.RideWaypoint;
import be.kul.rideservice.utils.json.jsonViews.Views;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.locationtech.jts.geom.Point;
import org.n52.jackson.datatype.jts.GeometryDeserializer;
import org.n52.jackson.datatype.jts.GeometrySerializer;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class WayPoint {
    @Id
    @GeneratedValue(generator = "waypoint-sequence-generator")
    @GenericGenerator(
            name = "waypoint-sequence-generator",
            strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {
                    @org.hibernate.annotations.Parameter(name = "sequence_name", value = "waypoint_sequence"),
                    @org.hibernate.annotations.Parameter(name = "initial_value", value = "1"),
                    @org.hibernate.annotations.Parameter(name = "increment_size", value = "1")
            }
    )
    @JsonView(Views.RideView.Full.class)
    private long wayPointId;
    @NotNull
    @JsonView(Views.RideView.Full.class)
    private LocalDateTime time;
    @NotNull
    @JsonSerialize( using = GeometrySerializer.class)
    @JsonDeserialize( contentUsing = GeometryDeserializer.class)
    @JsonView(Views.RideView.Full.class)
    private Point location;

    @JsonIgnore
    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name="ride_id")
    private Ride ride;

    public WayPoint(RideWaypoint rideWaypoint, Ride ride) {
        this.time = rideWaypoint.getTime();
        this.location = rideWaypoint.getLocation();
        this.ride = ride;
    }
}
