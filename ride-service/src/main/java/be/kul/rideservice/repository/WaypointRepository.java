package be.kul.rideservice.repository;

import be.kul.rideservice.entity.Ride;
import be.kul.rideservice.entity.WayPoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WaypointRepository extends JpaRepository<WayPoint, Long> {
}
