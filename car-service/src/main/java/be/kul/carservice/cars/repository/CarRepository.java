package be.kul.carservice.cars.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import be.kul.carservice.cars.entity.Car;

import java.util.*;
import java.util.UUID;

@Repository
public interface CarRepository extends JpaRepository<Car, Long> {

    @Query(value = "SELECT * FROM car AS c WHERE ST_Distance(c.location, ST_SRID(POINT(:userLongitude, :userLatitude), 4326)) < (:radiusInKM * 1000) ORDER BY ST_Distance(c.location, ST_SRID(POINT( :userLongitude, :userLatitude ), 4326))"
            , nativeQuery = true)
    List<Car> findAllCarsWithinRadius(@Param("userLongitude") double userLongitude, @Param("userLatitude") double userLatitude, @Param("radiusInKM") double radiusInKM);

    boolean existsByNumberPlate(String numberPlate);

    @Query(value = "SELECT * FROM car AS c WHERE ST_Distance(c.location, ST_SRID(POINT(:userLongitude, :userLatitude), 4326)) < (:radiusInKM * 1000) and available=true ORDER BY ST_Distance(c.location, ST_SRID(POINT( :userLongitude, :userLatitude ), 4326))"
            , nativeQuery = true)
    List<Car> findAllAvailableCarsWithinRadius(@Param("userLongitude") double userLongitude, @Param("userLatitude") double userLatitude, @Param("radiusInKM") double radiusInKM);

    @Query(value = "SELECT * FROM car AS c WHERE ST_Distance(c.location, ST_SRID(POINT(:userLongitude, :userLatitude), 4326)) < (:radiusInKM * 1000) and needs_maintenance=true ORDER BY ST_Distance(c.location, ST_SRID(POINT( :userLongitude, :userLatitude ), 4326))"
            , nativeQuery = true)
    List<Car> findAllMaintenanceRequiringCarsWithinRadius(@Param("userLongitude") double userLongitude, @Param("userLatitude") double userLatitude, @Param("radiusInKM") double radiusInKM);
}
