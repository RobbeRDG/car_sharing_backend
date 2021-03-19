package be.kul.carservice.repository;

import be.kul.carservice.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    @Query(value = "SELECT * "
            + "FROM reservation "
            + "WHERE car_id=:car_id "
            + "ORDER BY valid_until DESC "
            + "LIMIT 1"
            , nativeQuery = true)
    Optional<Reservation> getMostRecentReservationForCar(@Param("car_id") long carId);

    @Query(value = "SELECT * "
            + "FROM reservation "
            + "WHERE user_id=:user_id "
            + "ORDER BY created_on DESC "
            + "LIMIT 1"
            , nativeQuery = true)
    Optional<Reservation> getMostRecentReservationFromUser(@Param("user_id") String userId);
}
