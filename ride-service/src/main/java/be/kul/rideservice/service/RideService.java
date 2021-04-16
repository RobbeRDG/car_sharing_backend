package be.kul.rideservice.service;

import be.kul.rideservice.controller.amqp.AmqpProducerController;
import be.kul.rideservice.entity.Ride;
import be.kul.rideservice.entity.WayPoint;
import be.kul.rideservice.repository.RideRepository;
import be.kul.rideservice.repository.WaypointRepository;
import be.kul.rideservice.utils.exceptions.DoesntExistException;
import be.kul.rideservice.utils.exceptions.NotAllowedException;
import be.kul.rideservice.utils.json.jsonObjects.amqpMessages.payment.BillInitialisation;
import be.kul.rideservice.utils.json.jsonObjects.amqpMessages.ride.RideEnd;
import be.kul.rideservice.utils.json.jsonObjects.amqpMessages.ride.RideInitialisation;
import be.kul.rideservice.utils.json.jsonObjects.amqpMessages.ride.RideWaypoint;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

@Service
public class RideService {
    public static final Logger logger = LoggerFactory.getLogger(RideService.class);

    @Autowired
    private RideRepository rideRepository;

    @Autowired
    private WaypointRepository waypointRepository;

    @Autowired
    AmqpProducerController amqpProducerController;


    public List<Ride> adminGetAllRidesWithinTimeFrame(LocalDate startTime, LocalDate stopTime) {
        return rideRepository.adminGetAllRidesWithinTimeFrame(startTime, stopTime);
    }

    public Ride adminGetRide(long rideId) {
        //Get the requested ride
        Ride ride = rideRepository.findById(rideId).orElse(null);
        if (ride==null) throw new DoesntExistException("Couldn't get the requested ride: The ride with id '" + rideId + "' doesn't exist");

        return ride;
    }

    public Ride getRide(String userId, long rideId) {
        //Get the requested ride
        Ride ride = rideRepository.findById(rideId).orElse(null);
        if (ride==null) throw new DoesntExistException("Couldn't get the requested ride: The ride with id '" + rideId + "' doesn't exist");

        //Check if this ride belongs to the requesting user
        if (ride.getUserId().equals(userId)) throw new NotAllowedException(
                "Couldn't get the requested ride: this ride does not belong to the requesting user"
        );

        return ride;
    }

    public List<Ride> getAllRidesWithinTimeFrame(String userId, Date startTime, Date stopTime) {
        return rideRepository.getAllRidesWithinTimeFrame(userId, startTime, stopTime);
    }

    public void addRide(RideInitialisation rideCreation) {
        //Create a new ride object
        Ride ride = new Ride(rideCreation);

        logger.info(String.valueOf(ride.getRideId()));
        //Save the ride object
        rideRepository.save(ride);
    }

    @Transactional(dontRollbackOn = {DoesntExistException.class, IllegalArgumentException.class})
    public void addWaypoint(RideWaypoint rideWaypoint) {
        //Get the associated ride of the waypoint
        long rideId = rideWaypoint.getRideId();
        Ride ride = rideRepository.findById(rideId).orElse(null);
        if (ride==null) throw new DoesntExistException("Couldn't add waypoint: The ride with id '" + rideId + "' doesn't exist");

        //Save the new waypoint
        waypointRepository.save(new WayPoint(rideWaypoint, ride));
    }

    public void endRide(RideEnd rideEnd) throws JsonProcessingException {
        //Get the associated ride to end
        long rideId = rideEnd.getRideId();
        Ride ride = rideRepository.findById(rideId).orElse(null);
        if (ride==null) throw new DoesntExistException("Couldn't end the requested ride: The ride with id '" + rideId + "' doesn't exist");

        //Update the ride state with the new end ride request
        ride.updateState(rideEnd);

        //Send a payment request to the payment service
        BillInitialisation billInitialisation = new BillInitialisation(ride);
        amqpProducerController.sendPaymentInitialisation(billInitialisation);

        //Save the new ride state
        rideRepository.save(ride);

    }
}
