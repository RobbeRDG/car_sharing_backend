package be.kul.rideservice.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
public class Ride {
    @Id
    @GeneratedValue
    private long rideId;
    @NotNull
    private long carId;
    @NotNull
    private long userId;

    @OneToMany
    @JoinColumn(name = "ride_id")
    private WayPoint wayPoint;

}
