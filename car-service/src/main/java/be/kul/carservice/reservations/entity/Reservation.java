package be.kul.carservice.reservations.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.UUID;

@Entity
public class Reservation {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private UUID carId;
}
