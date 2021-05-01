package be.kul.rideservice.repository;

import be.kul.rideservice.entity.Ride;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface RideRepository extends JpaRepository<Ride, Long> {
    @Query(value = "SELECT * " +
            "FROM rides.ride as r " +
            "WHERE r.started_on between :startDate and :stopDate "
            , nativeQuery = true)
    List<Ride> adminGetAllRidesWithinTimeFrame(@Param("startDate") LocalDate startDate, @Param("stopDate") LocalDate stopDate);

    @Query(value = "SELECT * " +
            "FROM rides.ride as r " +
            "WHERE r.started_on between :startDate and :stopDate and r.user_id = :userId "
            , nativeQuery = true)
    List<Ride> getAllRidesWithinTimeFrame(@Param("userId") String userId, @Param("startDate") LocalDate startDate, @Param("stopDate") LocalDate stopDate);


    @Query(value = "SELECT * " +
            "FROM rides.ride as r " +
            "WHERE r.user_id = :userId "
            , nativeQuery = true)
    Optional<Ride> findCurrentRideOfUser(String userId);
}
