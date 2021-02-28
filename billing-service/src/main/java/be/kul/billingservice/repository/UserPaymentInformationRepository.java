package be.kul.billingservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserPaymentInformationRepository extends JpaRepository<UserPaymentInformation, String>{

}
