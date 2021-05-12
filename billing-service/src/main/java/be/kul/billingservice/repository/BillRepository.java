package be.kul.billingservice.repository;

import be.kul.billingservice.entity.Bill;
import be.kul.billingservice.entity.UserPaymentInformation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.ArrayList;

public interface BillRepository extends JpaRepository<Bill, Long> {
    @Query(value = "SELECT * " +
            "FROM bill " +
            "WHERE bill.user_id=:userId and bill.bill_status='FAILED'"
            , nativeQuery = true)
    ArrayList<Bill> getUserFailedBills(@Param("userId") String userId);
}
