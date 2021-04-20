package be.kul.carservice.repository;

import be.kul.carservice.entity.UserPaymentMethodStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserPaymentMethodStatusRepository extends JpaRepository<UserPaymentMethodStatus, String> {
}
