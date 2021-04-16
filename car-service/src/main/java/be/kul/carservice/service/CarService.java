package be.kul.carservice.service;

import be.kul.carservice.controller.amqp.AmqpProducerController;
import be.kul.carservice.entity.Car;
import be.kul.carservice.entity.Reservation;
import be.kul.carservice.entity.Ride;
import be.kul.carservice.entity.User;
import be.kul.carservice.repository.CarRepository;
import be.kul.carservice.repository.ReservationRepository;
import be.kul.carservice.repository.RideRepository;
import be.kul.carservice.repository.UserRepository;
import be.kul.carservice.utils.exceptions.*;
import be.kul.carservice.utils.helperObjects.PaymentMethodStatusEnum;
import be.kul.carservice.utils.json.jsonObjects.amqpMessages.car.*;
import be.kul.carservice.utils.json.jsonObjects.amqpMessages.payment.PaymentMethodStatusUpdate;
import be.kul.carservice.utils.json.jsonObjects.amqpMessages.ride.RideEnd;
import be.kul.carservice.utils.json.jsonObjects.amqpMessages.ride.RideInitialisation;
import be.kul.carservice.utils.json.jsonObjects.amqpMessages.ride.RideWaypoint;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.util.List;

@org.springframework.stereotype.Service
public class CarService {
    public static final Logger logger = LoggerFactory.getLogger(CarService.class);

    private static final int RESERVATION_COOLDOWN_IN_MINUTES = 120;

    @Autowired
    private AmqpProducerController amqpProducerController;

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private RideRepository rideRepository;


    public Car registerCar(Car car) {
        //First check if the car doesn't already exist
        if (carRepository.existsByNumberPlate(car.getNumberPlate())) {
            String errorMessage = "Car with numberplate '" + car.getNumberPlate() + "' already exists";
            logger.warn(errorMessage);
            throw new AlreadyExistsException(errorMessage);
        }

        //Set the latestStateUpdate to now
        car.setLastStateUpdate(new Timestamp(System.currentTimeMillis()));
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

        //Set the latestStateUpdate of each car to now
        Timestamp now = new Timestamp(System.currentTimeMillis());
        for(Car car: carList) {
            car.setLastStateUpdate(now);
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


    public Car updateCarState(CarStateUpdate stateUpdate) throws JsonProcessingException {
        long carId = stateUpdate.getCarId();
        Car car = carRepository.findById(carId).orElse(null);
        if (car==null) throw new DoesntExistException("Couldn't update car state: The car with id '" + carId + "' doesn't exist");

        //Update car parameters
        car.setRemainingFuelInKilometers(stateUpdate.getRemainingFuelInKilometers());
        car.setLocation(stateUpdate.getLocation());
        car.setOnline(stateUpdate.isOnline());
        car.setLastStateUpdate(stateUpdate.getCreatedOn());

        //If the car is in a ride send waypoint to ride service
        if (car.getCurrentRide()!=null) {
            RideWaypoint rideWaypoint = new RideWaypoint(stateUpdate, car.getCurrentRide().getRideId());
            amqpProducerController.sendRideWaypoint(rideWaypoint);
        }


        logger.info("Updated car state of car with id '" + carId + "'");
        return carRepository.save(car);
    }


    @Transactional(dontRollbackOn = {NotAvailableException.class, DoesntExistException.class, ReservationCooldownException.class})
    public Car reserveCar(String userId, long carId) {
        /*
        //Check if the user has a valid paymentMethod
        User user = userRepository.findById(userId).orElse(null);
        if (user == null || !user.getPaymentMethodStatus().equals(PaymentMethodStatusEnum.VALID)) throw new NotAllowedException(
                "Couldn't reserve car: This user doesn't have a valid payment method"
        );

         */

        //Get the requested car
        Car car = carRepository.findById(carId).orElse(null);
        if (car==null) throw new DoesntExistException("Couldn't reserve car: The car with id '" + carId + " doesn't exist");

        //check if the car is not in use or in maintenance or reserved
        if (!car.canBeReserved()) throw new NotAvailableException("Couldn't reserve car: The car with id '" + carId + "' can't be reserved now");

        //Check if the user can place a reservation
        if(isUserOnCooldown(userId, RESERVATION_COOLDOWN_IN_MINUTES)) throw new ReservationCooldownException("Couldn't reserve car: User is still on cooldown");

        //Create a new reservation
        Reservation reservation = new Reservation(userId, car);

        //Set the reservation state of the car
        car.setCurrentReservation(reservation);

        //Log the reservation
        logger.info("Placed reservation on car with id '" + carId + "'");

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

    public Car setCarToActive(long carId) {
        //Get the requested car
        Car car = carRepository.findById(carId).orElse(null);
        if (car==null) throw new DoesntExistException("Couldn't set car to active: The car with id '" + carId + " doesn't exist");

        //Set the car to active state
        car.setActive(true);

        //Save and return the car
        return carRepository.save(car);
    }

    public Car setCarToInactive(long carId) {
        //Get the requested car
        Car car = carRepository.findById(carId).orElse(null);
        if (car==null) throw new DoesntExistException("Couldn't set car to inactive: The car with id '" + carId + " doesn't exist");

        //Set the car to active state
        car.setActive(false);

        //Save and return the car
        return carRepository.save(car);
    }

    @Transactional(dontRollbackOn = {DoesntExistException.class, NotAvailableException.class, RequestDeniedException.class, CarOfflineException.class})
    public Car startRide(String userId, long carId) throws Exception {
        /*
        //Check if the user has a valid paymentMethod
        User user = userRepository.findById(userId).orElse(null);
        if (user == null || !user.getPaymentMethodStatus().equals(PaymentMethodStatusEnum.VALID)) throw new NotAllowedException(
                "Couldn't start ride: This user doesn't have a valid payment method"
        );

         */

        //Get the requested car
        Car car = carRepository.findById(carId).orElse(null);
        if (car==null) throw new DoesntExistException("Couldn't start ride: The car with id '" + carId + " doesn't exist");

        //check if the car can be ridden
        if (!car.canBeRidden(userId)) throw new NotAvailableException(
                "Couldn't start ride: The car with id '" + carId + "' can't be ridden now");

        //Create a new ride
        Ride ride = rideRepository.save(new Ride(userId, car));

        /*
        //Send a ride request to the car and wait for response
        CarRideRequest rideRequest = new CarRideRequest(ride);
        CarAcknowledgement ack;
        try {
            ack = amqpProducerController.sendBlockingCarRequest(rideRequest);
        } catch(CarOfflineException e) {
            handleCarOfflineOnStartRide(car, ride);
            throw e;
        }

        //Check if the acknowledgement confirms the ride request
        if(!ack.confirmsRequest(rideRequest)) {
            handleRequestDeniedOnStartRide(ride);
            throw new RequestDeniedException(
                    "Couldn't start ride: The car with id '" + carId + "' has denied the ride request: " + ack.getErrorMessage());
        }

         */

        //Set the new car state
        car.setCurrentRide(ride);
        car.setCarDoorsLocked(false);

        //Start the ride
        car.getCurrentRide().start();

        //Save the car state
        Car updatedCar = carRepository.save(car);

        //Send a RideInitialisation to the ride service
        RideInitialisation rideInitialisation = new RideInitialisation(ride);
        amqpProducerController.sendRideInitialisation(rideInitialisation);

        //Return the new car state
        return updatedCar;
    }

    private void handleRequestDeniedOnStartRide(Ride ride) {
        //Cancel the new ride
        ride.cancel();

        //Save the ride state
        rideRepository.save(ride);
    }

    private void handleCarOfflineOnStartRide(Car car, Ride ride) throws Exception {
        //Cancel the new ride
        ride.cancel();

        //Set the new state not-responding car
        car.setOnline(false);

        //Save the car and ride state
        carRepository.save(car);
        rideRepository.save(ride);
    }

    public Car lockCar(String userId, long carId, boolean lock) throws JsonProcessingException {
        //Get the requested car
        Car car = carRepository.findById(carId).orElse(null);
        if (car==null) throw new DoesntExistException("Couldn't lock car: The car with id '" + carId + " doesn't exist");

        //Check if the user is currently riding the requested car
        if (!car.isRiddenBy(userId)) throw new NotAllowedException(
                "Couldn't lock/unlock car: Cars can only be locked when the user is currently riding the car");

        /*
        //Create a CarLockRequest and send the request to the car
        CarLockRequest carLockRequest = new CarLockRequest(car.getCurrentRide().getRideId(), carId, true);
        CarAcknowledgement ack = amqpProducerController.sendBlockingCarRequest(carLockRequest);

        //Check if the acknowledgement confirms the lockrequest
        if(!ack.confirmsRequest(carLockRequest)) throw new RequestDeniedException(
                "Couldn't lock/unlock car: The car with id '" + carId + "' has denied the lock request: " + ack.getErrorMessage());

         */

        //Set the car door state
        car.setCarDoorsLocked(lock);

        //Return to client
        return carRepository.save(car);
    }

    @Transactional(dontRollbackOn = {DoesntExistException.class, RequestDeniedException.class, NotAllowedException.class})
    public ResponseEntity<String> endRide(String userId, long carId) throws JsonProcessingException {
        //Get the requested car
        Car car = carRepository.findById(carId).orElse(null);
        if (car==null) throw new DoesntExistException(
                "Couldn't end ride: The car with id '" + carId + " doesn't exist");

        //Check if the user is currently riding the requested car
        if (!car.isBeingRidden() || !car.isRiddenBy(userId)) throw new NotAllowedException(
                "Couldn't end ride: User is not currently riding the requested car");

        /*
        //Create a EndRideRequest and send the request to the car
        CarEndRideRequest endRideRequest = new CarEndRideRequest(car.getCurrentRide());
        CarAcknowledgement ack = amqpProducerController.sendBlockingCarRequest(endRideRequest);


        //Check if the acknowledgement confirms the EndRideRequest
        if(!ack.confirmsRequest(endRideRequest)) throw new RequestDeniedException(
                "Couldn't end ride: The car with id '" + carId + "' has denied the end ride request: " + ack.getErrorMessage());

         */

        //End the ride
        Ride ride = car.getCurrentRide();
        ride.end();

        //Save the finished ride
        rideRepository.save(ride);

        //Set the car door state to locked
        car.setCarDoorsLocked(true);

        //Save the car state
        Car updatedCar = carRepository.save(car);

        //Unlink the finished ride from the car
        carRepository.unlinkCurrentRide(ride.getRideId());
        //Unlink the current reservation
        if (updatedCar.getCurrentReservation() != null) {
            carRepository.unlinkCurrentReservation(updatedCar.getCurrentReservation().getReservationId());
        }

        //send the rideStateUpdate to the ride service
        RideEnd rideEnd = new RideEnd(ride);
        amqpProducerController.sendRideEnd(rideEnd);

        return new ResponseEntity<>(
                "Successfully ended ride",
                HttpStatus.OK);
    }

    public void updatePaymentMethodStatus(PaymentMethodStatusUpdate paymentMethodStatusUpdate) {
        String userId = paymentMethodStatusUpdate.getUserId();

        //Check if the user has a valid paymentMethod
        User user = userRepository.findById(userId).orElse(null);
        if (user==null) throw new DoesntExistException("Couldn't update payment method status:  The user with id '" + userId + " doesn't exist");

        userRepository.save(user);
    }
}
