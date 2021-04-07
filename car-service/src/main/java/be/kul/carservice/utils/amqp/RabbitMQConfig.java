package be.kul.carservice.utils.amqp;

import be.kul.carservice.controller.amqp.AmqpConsumerController;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@Configuration
public class RabbitMQConfig {
    public static final String CARS_TO_SERVER_EXCHANGE = "carsToServer";
    public static final String SERVER_TO_CARS_EXCHANGE = "serverToCars";
    public static final String SERVER_TO_SERVER_EXCHANGE = "serverToServer";
    public static final String CAR_STATE_QUEUE = "carState";
    public static final String CAR_STATE_BINDING_KEY = "carService.cars.state.*";
    public static final String RIDE_INITIALISATION_QUEUE = "rideInitialisation";
    public static final String RIDE_INITIALISATION_BINDING_KEY = "rideService.rides.new.*";
    public static final String RIDE_WAYPOINT_QUEUE = "rideWaypoint";
    public static final String RIDE_WAYPOINT_BINDING_KEY = "rideService.rides.waypoint.*";

    @Resource(name="externalRabbitAdmin")
    private RabbitAdmin externalRabbitAdmin;

    @Resource(name="internalRabbitAdmin")
    private RabbitAdmin internalRabbitAdmin;


    @PostConstruct
    public void RabbitInit() {
        //Car state update
        externalRabbitAdmin.declareExchange(new TopicExchange(CARS_TO_SERVER_EXCHANGE));
        externalRabbitAdmin.declareQueue(new Queue(CAR_STATE_QUEUE));
        externalRabbitAdmin.declareBinding(
                BindingBuilder.bind(new Queue(CAR_STATE_QUEUE))
                        .to(new TopicExchange(CARS_TO_SERVER_EXCHANGE))
                        .with(CAR_STATE_BINDING_KEY));

        //Ride initialisation
        internalRabbitAdmin.declareExchange(new TopicExchange(SERVER_TO_SERVER_EXCHANGE));
        internalRabbitAdmin.declareQueue(new Queue(RIDE_INITIALISATION_QUEUE));
        internalRabbitAdmin.declareBinding(
                BindingBuilder.bind(new Queue(RIDE_INITIALISATION_QUEUE))
                        .to(new TopicExchange(SERVER_TO_SERVER_EXCHANGE))
                        .with(RIDE_INITIALISATION_BINDING_KEY));
    }

    @Bean
    public AmqpConsumerController amqpController() {
        return new AmqpConsumerController();
    }

}
