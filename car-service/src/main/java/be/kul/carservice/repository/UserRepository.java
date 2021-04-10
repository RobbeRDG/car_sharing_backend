package be.kul.carservice.repository;

import be.kul.carservice.entity.Ride;
import be.kul.carservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
}
