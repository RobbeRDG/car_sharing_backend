package be.kul.carservice.reservations.service;

import be.kul.carservice.cars.service.CarService;
import be.kul.carservice.reservations.entity.Reservation;
import be.kul.carservice.reservations.repository.ReservationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

@Service
public class ReservationService {
    Logger logger = LoggerFactory.getLogger(ReservationService.class);

    @Autowired
    ReservationRepository reservationRepository;

    public Reservation registerReservation(Reservation reservation) {
        return reservationRepository.save(reservation);
    }


    public boolean isReserved(long carId) {
        Reservation mostRecent =  reservationRepository.getMostRecentReservationForCar(carId).orElse(null);

        //If there are no reservations on the car
        if (mostRecent==null) return false;

        //Check if the most recent reservation has already expired
        Timestamp currentTime = new Timestamp(System.currentTimeMillis());
        Timestamp validUntil = mostRecent.getValidUntil();
        if(validUntil.before(currentTime)) return false;
        else return true;
    }

    public boolean isUserOnCooldown(String userId, int reservationCooldownInMinutes) {
        Reservation mostRecent =  reservationRepository.getMostRecentReservationFromUser(userId).orElse(null);
        if (mostRecent==null) return false;

        Timestamp currentTimeMinusCooldown = new Timestamp(System.currentTimeMillis() - reservationCooldownInMinutes*60*1000);
        if (mostRecent.getCreatedOn().before(currentTimeMinusCooldown)) return false;
        return true;
    }
}
