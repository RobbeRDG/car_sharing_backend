package be.kul.rideservice.controller.amqp;

import be.kul.rideservice.service.RideService;
import be.kul.rideservice.utils.amqp.RabbitMQConfig;
import be.kul.rideservice.utils.json.jsonObjects.amqpMessages.billing.BillStatusUpdate;
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
        RideInitialisation rideInitialisation = objectMapper.readValue(rideInitialisationString, RideInitialisation.class);

        RideService.logger.info(rideInitialisationString);

        //Handle the ride initialisation
        rideService.addRide(rideInitialisation);
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

    @RabbitListener(queues = RabbitMQConfig.BILL_STATUS_UPDATE_QUEUE, containerFactory = "internalRabbitListenerContainerFactory")
    public void updateBillStatus(@Payload String billStatusUpdateString) throws JsonProcessingException {
        //get the stateUpdate object from the string
        BillStatusUpdate billStatusUpdate = objectMapper.readValue(billStatusUpdateString, BillStatusUpdate.class);

        //Handle the new waypoint
        rideService.updateBillStatus(billStatusUpdate);
    }
}
