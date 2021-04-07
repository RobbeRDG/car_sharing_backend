package be.kul.carservice.controller.amqp;

import be.kul.carservice.service.CarService;
import be.kul.carservice.utils.amqp.RabbitMQConfig;
import be.kul.carservice.utils.exceptions.CarOfflineException;
import be.kul.carservice.utils.json.jsonObjects.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class AmqpProducerController {
    @Resource(name = "externalRabbitTemplate")
    private RabbitTemplate externalRabbitTemplate;

    @Resource(name="externalRabbitAdmin")
    private RabbitAdmin externalRabbitAdmin;

    @Resource(name = "internalRabbitTemplate")
    private RabbitTemplate internalRabbitTemplate;

    @Resource(name="internalRabbitAdmin")
    private RabbitAdmin internalRabbitAdmin;

    @Autowired
    CarService carService;

    @Autowired
    private ObjectMapper objectMapper;

    public CarAcknowledgement sendCarRideRequest(CarRideRequest carRideRequest) throws JsonProcessingException, CarOfflineException {
        //Dynamicaly generate the new queue for the selected car
        String carIdString = carRideRequest.getCarIdString();
        String queueName = "rideRequest." + carIdString;
        String bindingKey = "external.cars.rideRequest." + carIdString;
        generateCarQueue(queueName, bindingKey);

        //Convert carRideRequest to json
        String carRideRequestString = objectMapper.writeValueAsString(carRideRequest);

        //Send the request and wait for response
        String response;
        try {
            response = (String) externalRabbitTemplate.convertSendAndReceive(
                    RabbitMQConfig.SERVER_TO_CARS_EXCHANGE,
                    bindingKey,
                    carRideRequestString
            );
            if (response==null) throw new NullPointerException();
        } catch (NullPointerException e) {
            throw new CarOfflineException("Can't ride the car: The car with id '" + carIdString + "' seems to be offline");
        }

        //Convert json to CarAcknowledgement
        CarAcknowledgement ack = objectMapper.readValue(response, CarAcknowledgement.class);
        return ack;
    }

    private void generateCarQueue(String queueName, String bindingKey) {
        externalRabbitAdmin.declareExchange(new TopicExchange(RabbitMQConfig.SERVER_TO_CARS_EXCHANGE));
        externalRabbitAdmin.declareQueue(new Queue(queueName));
        externalRabbitAdmin.declareBinding(
                BindingBuilder.bind(new Queue(queueName))
                        .to(new TopicExchange(RabbitMQConfig.SERVER_TO_CARS_EXCHANGE))
                        .with(bindingKey));
        externalRabbitTemplate.setReplyTimeout(3000);
    }

    public void sendRideInitialisation(RideInitialisation rideInitialisation) throws JsonProcessingException {
        //Convert RideInitialisation to json
        String rideInitialisationString = objectMapper.writeValueAsString(rideInitialisation);

        //Send the RideInitialisationString to the ride service
        internalRabbitTemplate.convertAndSend(
                RabbitMQConfig.SERVER_TO_SERVER_EXCHANGE,
                RabbitMQConfig.RIDE_INITIALISATION_BINDING_KEY,
                rideInitialisationString
        );

    }

    public CarAcknowledgement sendCarLockRequest(CarLockRequest carLockRequest) throws JsonProcessingException {
        //Dynamicaly generate the new queue for the selected car
        String carIdString = carLockRequest.getCarIdString();
        String queueName = "lockRequest." + carIdString;
        String bindingKey = "external.cars.lockRequest." + carIdString;
        generateCarQueue(queueName, bindingKey);

        //Convert CarLockRequest to json
        String carRideRequestString = objectMapper.writeValueAsString(carLockRequest);

        //Send the request and wait for response
        String response;
        try {
            response = (String) externalRabbitTemplate.convertSendAndReceive(
                    RabbitMQConfig.SERVER_TO_CARS_EXCHANGE,
                    bindingKey,
                    carRideRequestString
            );
            if (response==null) throw new NullPointerException();
        } catch (NullPointerException e) {
            throw new CarOfflineException("Can't lock/unlock the car: The car with id '" + carIdString + "' seems to be offline");
        }

        //Convert json to CarAcknowledgement
        CarAcknowledgement ack = objectMapper.readValue(response, CarAcknowledgement.class);
        return ack;
    }

    public void sendRideWaypoint(RideWaypoint rideWaypoint) throws JsonProcessingException {
        //Convert CarStateUpdate to json
        String rideWaypointString = objectMapper.writeValueAsString(rideWaypoint);

        //Send the RideInitialisationString to the ride service
        internalRabbitTemplate.convertAndSend(
                RabbitMQConfig.SERVER_TO_SERVER_EXCHANGE,
                RabbitMQConfig.RIDE_WAYPOINT_BINDING_KEY,
                rideWaypointString
        );
    }
}
