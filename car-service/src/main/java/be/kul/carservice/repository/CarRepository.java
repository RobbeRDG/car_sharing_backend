package be.kul.carservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import be.kul.carservice.entity.Car;

import javax.persistence.LockModeType;
import java.util.*;

@Repository
public interface CarRepository extends JpaRepository<Car, Long> {
    @Lock(LockModeType.OPTIMISTIC)
    Optional<Car> findById(Long id);

    @Query(value = "SELECT * FROM car AS c WHERE ST_Distance(c.location, ST_SRID(POINT(:userLongitude, :userLatitude), 4326)) < (:radiusInKM * 1000) ORDER BY ST_Distance(c.location, ST_SRID(POINT( :userLongitude, :userLatitude ), 4326))"
            , nativeQuery = true)
    List<Car> findAllCarsWithinRadius(@Param("userLongitude") double userLongitude, @Param("userLatitude") double userLatitude, @Param("radiusInKM") double radiusInKM);

    boolean existsByNumberPlate(String numberPlate);

    @Query(value = "SELECT car.car_id, car.location, car.manufacturer, car.model, car.number_of_seats, car.number_plate, car.remaining_fuel_in_kilometers, car.year_of_manufacture " +
            "FROM car AS c " +
            "WHERE ST_Distance(c.location, ST_SRID(POINT(:userLongitude, :userLatitude), 4326)) < (:radiusInKM * 1000) and in_maintenance=false and c.ride_id is null and c.reservation_id is null " +
            "ORDER BY ST_Distance(c.location, ST_SRID(POINT( :userLongitude, :userLatitude ), 4326))"
            , nativeQuery = true)
    List<Car> findAllAvailableCarsWithinRadius(@Param("userLongitude") double userLongitude, @Param("userLatitude") double userLatitude, @Param("radiusInKM") double radiusInKM);

    @Query(value = "SELECT * FROM car AS c WHERE ST_Distance(c.location, ST_SRID(POINT(:userLongitude, :userLatitude), 4326)) < (:radiusInKM * 1000) and in_maintenance=true ORDER BY ST_Distance(c.location, ST_SRID(POINT( :userLongitude, :userLatitude ), 4326))"
            , nativeQuery = true)
    List<Car> findAllMaintenanceRequiringCarsWithinRadius(@Param("userLongitude") double userLongitude, @Param("userLatitude") double userLatitude, @Param("radiusInKM") double radiusInKM);
}
