package be.kul.billingservice.repository;

import be.kul.billingservice.entity.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.ArrayList;
import java.util.Optional;

public interface BillRepository extends JpaRepository<Bill, Long> {
    @Query(value = "SELECT * " +
            "FROM bill " +
            "WHERE bill.user_id=:userId and bill.bill_status='FAILED'"
            , nativeQuery = true)
    ArrayList<Bill> getUserFailedBills(@Param("userId") String userId);

    @Query(value = "SELECT * " +
            "FROM bill " +
            "WHERE bill.ride_id=:rideId"
            , nativeQuery = true)
    Optional<Bill> findByRideId(@Param("rideId") long rideId);
}
