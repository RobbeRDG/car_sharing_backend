package be.kul.rideservice.controller;

import be.kul.rideservice.service.RideService;
import be.kul.rideservice.utils.amqp.RabbitMQConfig;
import be.kul.rideservice.utils.json.jsonObjects.RideStateUpdate;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;

public class AmqpController {
    @Autowired
    RideService rideService;

    @Autowired
    private ObjectMapper objectMapper;

    @RabbitListener(queues = RabbitMQConfig.RIDE_STATE_QUEUE, containerFactory = "internalRabbitListenerContainerFactory")
    public void createRide(@Payload String rideString, @Header(AmqpHeaders.RECEIVED_ROUTING_KEY) String routingKey) throws JsonProcessingException {
        //get the rideCreation object from the string
        RideCreation rideCreation = objectMapper.readValue(rideString, RideCreation.class);

        //Get the rideId from the routing key
        //Ex routing key: rideService.rides.new.{rideId}
        String rideIdString = routingKey.split("\\.")[3];
        long rideId = Integer.parseInt(rideIdString);

        //Set the carId for the car state update
        rideCreation.setRideId(rideId);

        rideService.createRide(rideCreation);
    }

    @RabbitListener(queues = RabbitMQConfig.RIDE_STATE_QUEUE, containerFactory = "internalRabbitListenerContainerFactory")
    public void updateRideState(@Payload String stateUpdateString, @Header(AmqpHeaders.RECEIVED_ROUTING_KEY) String routingKey) throws JsonProcessingException {
        //get the stateUpdate object from the string
        RideStateUpdate stateUpdate = objectMapper.readValue(stateUpdateString, RideStateUpdate.class);

        //Get the rideId from the routing key
        //Ex routing key: rideService.rides.state.{rideId}
        String rideIdString = routingKey.split("\\.")[3];
        long rideId = Integer.parseInt(rideIdString);

        //Set the carId for the car state update
        stateUpdate.setRideId(rideId);

        rideService.updateRideState(stateUpdate);
    }
}
