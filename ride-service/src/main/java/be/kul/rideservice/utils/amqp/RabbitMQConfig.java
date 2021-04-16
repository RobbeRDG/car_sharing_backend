package be.kul.rideservice.utils.amqp;

import be.kul.rideservice.controller.amqp.AmqpConsumerController;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@Configuration
public class RabbitMQConfig {
    public static final String SERVER_TO_SERVER_EXCHANGE = "serverToServer";
    public static final String RIDE_INITIALISATION_QUEUE = "rideInitialisation";
    public static final String RIDE_INITIALISATION_BINDING_KEY = "rideService.rides.new.*";
    public static final String RIDE_WAYPOINT_QUEUE = "rideWaypoint";
    public static final String RIDE_WAYPOINT_BINDING_KEY = "rideService.rides.waypoint.*";
    public static final String RIDE_END_QUEUE = "rideEnd";
    public static final String RIDE_END_BINDING_KEY = "rideService.rides.end.*";
    public static final String BILL_INITIALISATION_QUEUE = "billInitialisation";
    public static final String BILL_INITIALISATION_BINDING_KEY = "paymentService.bill.new.*";


    @Resource(name="internalRabbitAdmin")
    private RabbitAdmin internalRabbitAdmin;


    @PostConstruct
    public void RabbitInit() {
        //Ride initialisation
        internalRabbitAdmin.declareExchange(new TopicExchange(SERVER_TO_SERVER_EXCHANGE));
        internalRabbitAdmin.declareQueue(new Queue(RIDE_INITIALISATION_QUEUE));
        internalRabbitAdmin.declareBinding(
                BindingBuilder.bind(new Queue(RIDE_INITIALISATION_QUEUE))
                        .to(new TopicExchange(SERVER_TO_SERVER_EXCHANGE))
                        .with(RIDE_INITIALISATION_BINDING_KEY));

        //Ride waypoint
        internalRabbitAdmin.declareExchange(new TopicExchange(SERVER_TO_SERVER_EXCHANGE));
        internalRabbitAdmin.declareQueue(new Queue(RIDE_WAYPOINT_QUEUE));
        internalRabbitAdmin.declareBinding(
                BindingBuilder.bind(new Queue(RIDE_WAYPOINT_QUEUE))
                        .to(new TopicExchange(SERVER_TO_SERVER_EXCHANGE))
                        .with(RIDE_WAYPOINT_BINDING_KEY));

        //Ride end
        internalRabbitAdmin.declareExchange(new TopicExchange(SERVER_TO_SERVER_EXCHANGE));
        internalRabbitAdmin.declareQueue(new Queue(RIDE_END_QUEUE));
        internalRabbitAdmin.declareBinding(
                BindingBuilder.bind(new Queue(RIDE_END_QUEUE))
                        .to(new TopicExchange(SERVER_TO_SERVER_EXCHANGE))
                        .with(RIDE_END_BINDING_KEY));

        //Payment initialisation
        internalRabbitAdmin.declareExchange(new TopicExchange(SERVER_TO_SERVER_EXCHANGE));
        internalRabbitAdmin.declareQueue(new Queue(BILL_INITIALISATION_QUEUE));
        internalRabbitAdmin.declareBinding(
                BindingBuilder.bind(new Queue(BILL_INITIALISATION_QUEUE))
                        .to(new TopicExchange(SERVER_TO_SERVER_EXCHANGE))
                        .with(BILL_INITIALISATION_BINDING_KEY));
    }

    @Bean
    public AmqpConsumerController amqpController() {
        return new AmqpConsumerController();
    }

}
