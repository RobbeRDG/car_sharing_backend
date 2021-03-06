package be.kul.carservice.cars.service;

import be.kul.carservice.cars.entity.Car;
import be.kul.carservice.cars.repository.CarRepository;
import be.kul.carservice.reservations.entity.Reservation;
import be.kul.carservice.reservations.service.ReservationService;
import be.kul.carservice.utils.exceptions.AlreadyExistsException;
import be.kul.carservice.utils.exceptions.DoesntExistException;
import be.kul.carservice.utils.exceptions.NotAvailableException;
import be.kul.carservice.utils.exceptions.ReservationCooldownException;
import be.kul.carservice.utils.jsonObjects.CarStateUpdate;
import com.netflix.discovery.converters.Auto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.transaction.Transactional;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

@Service
public class CarService {
    Logger logger = LoggerFactory.getLogger(CarService.class);

    private static final int reservationCooldownInMinutes = 1;

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private ReservationService reservationService;


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

        logger.info("Cars created");
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

    @Transactional
    public Car reserveCar(String userId, long id) {
        //Get the requested car
        Car car = carRepository.findById(id).orElse(null);
        if (car==null) throw new DoesntExistException("The car with id '" + id + " doesn't exist");

        //check if the car is not in use or in maintenance
        if (car.isInMaintenance() || car.isInUse()) throw new NotAvailableException("The car with id '" + id + "' is not available");

        //Check if there isn't already a reservation on the car
        if (car.getLatestReservation() != null && car.getLatestReservation().getValidUntil().after(new Timestamp(System.currentTimeMillis()))) {
            throw new NotAvailableException("The car with id '" + id + "' is not available");
        }

        //Check if the user can place a reservation
        if(reservationService.isUserOnCooldown(userId, reservationCooldownInMinutes)) throw new ReservationCooldownException("The car can't be reserved: user is still on cooldown");

        //Create a new reservation
        Reservation reservation = new Reservation(userId, car);
        reservationService.registerReservation(reservation);

        //Set the reservation state of the car
        car.setLatestReservation(reservation);

        //Save the new state
        return carRepository.save(car);
    }
}
