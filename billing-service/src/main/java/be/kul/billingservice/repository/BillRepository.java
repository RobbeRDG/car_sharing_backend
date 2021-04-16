package be.kul.billingservice.repository;

import be.kul.billingservice.entity.Bill;
import be.kul.billingservice.entity.UserPaymentInformation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BillRepository extends JpaRepository<Bill, Long> {
}
