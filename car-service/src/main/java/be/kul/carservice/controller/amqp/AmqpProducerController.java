package be.kul.carservice.controller.amqp;

import be.kul.carservice.service.CarService;
import be.kul.carservice.utils.amqp.RabbitMQConfig;
import be.kul.carservice.utils.exceptions.CarOfflineException;
import be.kul.carservice.utils.json.jsonObjects.amqpMessages.car.CarAcknowledgement;
import be.kul.carservice.utils.json.jsonObjects.amqpMessages.car.CarRequest;
import be.kul.carservice.utils.json.jsonObjects.amqpMessages.ride.RideEnd;
import be.kul.carservice.utils.json.jsonObjects.amqpMessages.ride.RideInitialisation;
import be.kul.carservice.utils.json.jsonObjects.amqpMessages.ride.RideWaypoint;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Slf4j
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

    @Value("${spring.configurations.carRequest.expirationTimeInMilliseconds}")
    private int expirationTimeInMilliseconds;

    private void generateCarQueue(String queueName, String bindingKey) {
        Map<String, Object> args = new HashMap<String, Object>();
        args.put("x-message-ttl", expirationTimeInMilliseconds);

        externalRabbitAdmin.declareExchange(new TopicExchange(RabbitMQConfig.EXTERNAL_EXCHANGE, true, false));
        externalRabbitAdmin.declareQueue(new Queue(queueName, true, false, false, args));
        externalRabbitAdmin.declareBinding(
                BindingBuilder.bind(new Queue(queueName))
                        .to(new TopicExchange(RabbitMQConfig.EXTERNAL_EXCHANGE))
                        .with(bindingKey));
    }

    public <T extends CarRequest> CarAcknowledgement sendBlockingCarRequest(T request) throws JsonProcessingException, CarOfflineException {
        //Dynamicaly generate the new queue for the selected car
        String carIdString = String.valueOf(request.getCarId());
        String queueName = request.getRequestType() + "." + carIdString;
        String bindingKey = "external.cars." + request.getRequestType() + "." + carIdString;
        generateCarQueue(queueName, bindingKey);

        //Convert request to json
        String carRequestString = objectMapper.writeValueAsString(request);

        //Send the request and wait for response
        String response;
        try {
            response = (String) externalRabbitTemplate.convertSendAndReceive(
                    RabbitMQConfig.EXTERNAL_EXCHANGE,
                    bindingKey,
                    carRequestString
            );
            if (response==null) throw new NullPointerException();
        } catch (NullPointerException e) {
            throw new CarOfflineException("Can't lock/unlock the car: The car with id '" + carIdString + "' seems to be offline");
        }

        //Convert json to CarAcknowledgement
        CarAcknowledgement ack = objectMapper.readValue(response, CarAcknowledgement.class);
        return ack;
    }

    public void sendRideInitialisation(RideInitialisation rideInitialisation) throws JsonProcessingException {
        //Convert RideInitialisation to json
        String rideInitialisationString = objectMapper.writeValueAsString(rideInitialisation);

        //Send the RideInitialisationString to the ride service
        internalRabbitTemplate.convertAndSend(
                RabbitMQConfig.INTERNAL_EXCHANGE,
                RabbitMQConfig.RIDE_INITIALISATION_BINDING_KEY,
                rideInitialisationString
        );

    }

    public void sendRideWaypoint(RideWaypoint rideWaypoint) throws JsonProcessingException {
        //Convert CarStateUpdate to json
        String rideWaypointString = objectMapper.writeValueAsString(rideWaypoint);

        //Send the RideInitialisationString to the ride service
        internalRabbitTemplate.convertAndSend(
                RabbitMQConfig.INTERNAL_EXCHANGE,
                RabbitMQConfig.RIDE_WAYPOINT_BINDING_KEY,
                rideWaypointString
        );
    }

    public void sendRideEnd(RideEnd rideEnd) throws JsonProcessingException {
        //Convert RideEnd to json
        String rideEndString = objectMapper.writeValueAsString(rideEnd);

        //Send the RideInitialisationString to the ride service
        internalRabbitTemplate.convertAndSend(
                RabbitMQConfig.INTERNAL_EXCHANGE,
                RabbitMQConfig.RIDE_END_BINDING_KEY,
                rideEndString
        );
    }


}
