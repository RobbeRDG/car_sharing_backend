package be.kul.carservice.service;

import be.kul.carservice.entity.Car;
import be.kul.carservice.entity.Reservation;
import be.kul.carservice.repository.CarRepository;
import be.kul.carservice.repository.ReservationRepository;
import be.kul.carservice.utils.exceptions.AlreadyExistsException;
import be.kul.carservice.utils.exceptions.DoesntExistException;
import be.kul.carservice.utils.exceptions.NotAvailableException;
import be.kul.carservice.utils.exceptions.ReservationCooldownException;
import be.kul.carservice.utils.jsonObjects.CarStateUpdate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.util.List;

@Service
public class CarService {
    Logger logger = LoggerFactory.getLogger(CarService.class);

    private static final int RESERVATION_COOLDOWN_IN_MINUTES = 120;

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private ReservationRepository reservationRepository;


    public Car registerCar(Car car) {
        //First check if the car doesn't already exist
        if (carRepository.existsByNumberPlate(car.getNumberPlate())) {
            String errorMessage = "Car with numberplate '" + car.getNumberPlate() + "' already exists";
            logger.warn(errorMessage);
            throw new AlreadyExistsException(errorMessage);
        }

        logger.info("Car with numberplate '" +car.getNumberPlate() + "' created");
        return carRepository.save(car);
    }

    public List<Car> findAllCarsWithinRadius(Double longitude, double latitude, double radiusInKM) {
        return carRepository.findAllCarsWithinRadius(longitude, latitude, radiusInKM);
    }

    public List<Car> registerCars(List<Car> carList) {
        //First check if any of the cars don't already exist
        StringBuilder errorMessage = new StringBuilder("Car(s) with numberplate: ");
        boolean error = false;
        for(Car car: carList) {
            if (carRepository.existsByNumberPlate(car.getNumberPlate())) {
                error = true;
                errorMessage.append(" '" + car.getNumberPlate() + "' ");
            }
        }
        errorMessage.append("Already exist");
        if (error) {
            logger.warn(errorMessage.toString());
            throw new AlreadyExistsException(errorMessage.toString());
        }

        logger.info("Multiple cars created");
        return carRepository.saveAll(carList);
    }

    public List<Car> findAllAvailableCarsWithinRadius(double longitude, double latitude, double radiusInKM) {
        return carRepository.findAllAvailableCarsWithinRadius(longitude, latitude, radiusInKM);
    }

    public List<Car> findAllMaintenanceRequiringCarsWithinRadius(double longitude, double latitude, double radiusInKM) {
        return carRepository.findAllMaintenanceRequiringCarsWithinRadius(longitude, latitude, radiusInKM);
    }


    public Car updateCarState(long id, CarStateUpdate stateUpdate) {
        Car car = carRepository.findById(id).orElse(null);
        if (car==null) throw new DoesntExistException("The car with id '" + id + "' doesn't exist");

        //Update parameters
        logger.info("Updated car state of car with id '" + id + "'");
        car.setRemainingFuelInKilometers(stateUpdate.getRemainingFuelInKilometers());
        car.setLocation(stateUpdate.getLocation());

        return carRepository.save(car);
    }


    @Transactional(dontRollbackOn = {NotAvailableException.class, DoesntExistException.class, ReservationCooldownException.class})
    public Car reserveCar(String userId, long id) {
        //Get the requested car
        Car car = carRepository.findById(id).orElse(null);
        if (car==null) throw new DoesntExistException("The car with id '" + id + " doesn't exist");

        //check if the car is not in use or in maintenance or reserved
        if (!car.isFree()) throw new NotAvailableException("The car with id '" + id + "' is not available");

        //Check if the user can place a reservation
        if(isUserOnCooldown(userId, RESERVATION_COOLDOWN_IN_MINUTES)) throw new ReservationCooldownException("The car can't be reserved: user is still on cooldown");

        //Create a new reservation
        Reservation reservation = new Reservation(userId, car);

        //Save the reservation
        reservationRepository.save(reservation);

        //Set the reservation state of the car
        car.setCurrentReservation(reservation);

        //Log the reservation
        logger.info("Placed reservation on car with id '" + id + "'");

        //Save the new state
        return carRepository.save(car);
    }

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
