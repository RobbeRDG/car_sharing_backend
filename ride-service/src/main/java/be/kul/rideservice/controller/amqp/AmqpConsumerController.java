package be.kul.rideservice.controller.amqp;

import be.kul.rideservice.service.RideService;
import be.kul.rideservice.utils.amqp.RabbitMQConfig;
import be.kul.rideservice.utils.json.jsonObjects.amqpMessages.ride.RideEnd;
import be.kul.rideservice.utils.json.jsonObjects.amqpMessages.ride.RideInitialisation;
import be.kul.rideservice.utils.json.jsonObjects.amqpMessages.ride.RideWaypoint;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;

public class AmqpConsumerController {
    @Autowired
    RideService rideService;

    @Autowired
    private ObjectMapper objectMapper;

    @RabbitListener(queues = RabbitMQConfig.RIDE_INITIALISATION_QUEUE, containerFactory = "internalRabbitListenerContainerFactory")
    public void addRide(@Payload String rideInitialisationString) throws JsonProcessingException {
        //get the rideCreation object from the string
        RideInitialisation rideCreation = objectMapper.readValue(rideInitialisationString, RideInitialisation.class);

        //Handle the ride initialisation
        rideService.addRide(rideCreation);
    }

    @RabbitListener(queues = RabbitMQConfig.RIDE_WAYPOINT_QUEUE, containerFactory = "internalRabbitListenerContainerFactory")
    public void addWaypoint(@Payload String rideWaypointString) throws JsonProcessingException {
        //get the stateUpdate object from the string
        RideWaypoint rideWaypoint = objectMapper.readValue(rideWaypointString, RideWaypoint.class);

        //Handle the new waypoint
        rideService.addWaypoint(rideWaypoint);
    }

    @RabbitListener(queues = RabbitMQConfig.RIDE_END_QUEUE, containerFactory = "internalRabbitListenerContainerFactory")
    public void endRide(@Payload String rideEndString) throws JsonProcessingException {
        //get the stateUpdate object from the string
        RideEnd rideEnd = objectMapper.readValue(rideEndString, RideEnd.class);

        //Handle the new waypoint
        rideService.endRide(rideEnd);
    }
}
