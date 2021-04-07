package be.kul.rideservice.utils.amqp;

import be.kul.rideservice.controller.AmqpController;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@Configuration
public class RabbitMQConfig {
    public final static String SERVER_TO_SERVER_EXCHANGE = "serverToServer";
    public final static String RIDE_STATE_QUEUE = "rideState";
    public final static String RIDE_STATE_BINDING_KEY = "rideService.ride.state.*";
    public final static String RIDE_CREATION_QUEUE = "rideCreation";
    public final static String RIDE_CREATION_BINDING_KEY = "rideService.ride.creation.*";

    @Resource(name="externalRabbitAdmin")
    private RabbitAdmin externalRabbitAdmin;

    @Resource(name="internalRabbitAdmin")
    private RabbitAdmin internalRabbitAdmin;


    @PostConstruct
    public void RabbitInit() {
        externalRabbitAdmin.declareExchange(new DirectExchange(SERVER_TO_SERVER_EXCHANGE));

        //Ride creation
        externalRabbitAdmin.declareQueue(new Queue(RIDE_CREATION_QUEUE));
        externalRabbitAdmin.declareBinding(
                BindingBuilder.bind(new Queue(RIDE_CREATION_QUEUE))
                        .to(new DirectExchange(SERVER_TO_SERVER_EXCHANGE))
                        .with(RIDE_CREATION_BINDING_KEY));

        //Ride state update
        externalRabbitAdmin.declareQueue(new Queue(RIDE_STATE_QUEUE));
        externalRabbitAdmin.declareBinding(
                BindingBuilder.bind(new Queue(RIDE_STATE_QUEUE))
                        .to(new DirectExchange(SERVER_TO_SERVER_EXCHANGE))
                        .with(RIDE_STATE_BINDING_KEY));
    }

    @Bean
    public AmqpController amqpController() {
        return new AmqpController();
    }

}
