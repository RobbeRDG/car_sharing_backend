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
    public static final String CAR_SERVICE_DLQ = "carServiceDLQ";
    public static final String CAR_SERVICE_DLQ_BINDING_KEY = "carService.dlq";
    public static final String RIDE_SERVICE_DLQ_BINDING_KEY = "rideService.dlq";
    public static final String CAR_STATE_QUEUE = "carState";
    public static final String CAR_STATE_BINDING_KEY = "carService.cars.state.*";
    public static final String RIDE_INITIALISATION_QUEUE = "rideInitialisation";
    public static final String RIDE_INITIALISATION_BINDING_KEY = "rideService.rides.new.*";
    public static final String RIDE_WAYPOINT_QUEUE = "rideWaypoint";
    public static final String RIDE_WAYPOINT_BINDING_KEY = "rideService.rides.waypoint.*";
    public static final String RIDE_END_QUEUE = "rideEnd";
    public static final String RIDE_END_BINDING_KEY = "rideService.rides.end.*";
    public static final String PAYMENT_METHOD_STATUS_QUEUE = "paymentMethodStatus";
    public static final String PAYMENT_METHOD_STATUS_BINDING_KEY = "rideService.users.paymentMethodStatus.*";

    @Resource(name="externalRabbitAdmin")
    private RabbitAdmin externalRabbitAdmin;

    @Resource(name="internalRabbitAdmin")
    private RabbitAdmin internalRabbitAdmin;


    @PostConstruct
    public void RabbitInit() {
        TopicExchange serverToServer = new TopicExchange(SERVER_TO_SERVER_EXCHANGE);
        TopicExchange serverToCars = new TopicExchange(SERVER_TO_CARS_EXCHANGE);
        TopicExchange carsToServer = new TopicExchange(CARS_TO_SERVER_EXCHANGE);
        //DLQ
        internalRabbitAdmin.declareExchange(serverToServer);
        internalRabbitAdmin.declareQueue(carServiceDLQ());
        internalRabbitAdmin.declareBinding(
                BindingBuilder.bind(carServiceDLQ())
                        .to(serverToServer)
                        .with(CAR_SERVICE_DLQ_BINDING_KEY));

        //Car state update
        externalRabbitAdmin.declareExchange(carsToServer);
        externalRabbitAdmin.declareQueue(carStateQueue());
        externalRabbitAdmin.declareBinding(
                BindingBuilder.bind(carStateQueue())
                        .to(carsToServer)
                        .with(CAR_STATE_BINDING_KEY));

        //Ride initialisation
        internalRabbitAdmin.declareExchange(serverToServer);
        internalRabbitAdmin.declareQueue(rideInitialisationQueue());
        internalRabbitAdmin.declareBinding(
                BindingBuilder.bind(rideInitialisationQueue())
                        .to(serverToServer)
                        .with(RIDE_INITIALISATION_BINDING_KEY));

        //Ride waypoint
        internalRabbitAdmin.declareExchange(serverToServer);
        internalRabbitAdmin.declareQueue(rideWaypointQueue());
        internalRabbitAdmin.declareBinding(
                BindingBuilder.bind(rideWaypointQueue())
                        .to(serverToServer)
                        .with(RIDE_WAYPOINT_BINDING_KEY));

        //Ride end
        internalRabbitAdmin.declareExchange(serverToServer);
        internalRabbitAdmin.declareQueue(rideEndQueue());
        internalRabbitAdmin.declareBinding(
                BindingBuilder.bind(rideEndQueue())
                        .to(serverToServer)
                        .with(RIDE_END_BINDING_KEY));

        //Payment method update
        internalRabbitAdmin.declareExchange(serverToServer);
        internalRabbitAdmin.declareQueue(paymentMethodStatusQueue());
        internalRabbitAdmin.declareBinding(
                BindingBuilder.bind(paymentMethodStatusQueue())
                        .to(serverToServer)
                        .with(PAYMENT_METHOD_STATUS_BINDING_KEY));
    }

    @Bean
    public AmqpConsumerController amqpController() {
        return new AmqpConsumerController();
    }

    @Bean
    Queue carServiceDLQ() {
        return QueueBuilder
                .durable(CAR_SERVICE_DLQ)
                .deadLetterExchange(SERVER_TO_SERVER_EXCHANGE)
                .build();
    }

    @Bean
    public Queue carStateQueue() {
        return QueueBuilder
                .durable(CAR_STATE_QUEUE)
                .deadLetterExchange(SERVER_TO_SERVER_EXCHANGE)
                .deadLetterRoutingKey(CAR_SERVICE_DLQ_BINDING_KEY)
                .build();
    }

    @Bean
    public Queue rideInitialisationQueue() {
        return QueueBuilder
                .durable(RIDE_INITIALISATION_QUEUE)
                .deadLetterExchange(SERVER_TO_SERVER_EXCHANGE)
                .deadLetterRoutingKey(RIDE_SERVICE_DLQ_BINDING_KEY)
                .build();
    }

    @Bean
    public Queue rideWaypointQueue() {
        return QueueBuilder
                .durable(RIDE_WAYPOINT_QUEUE)
                .deadLetterExchange(SERVER_TO_SERVER_EXCHANGE)
                .deadLetterRoutingKey(RIDE_SERVICE_DLQ_BINDING_KEY)
                .build();
    }

    @Bean
    public Queue rideEndQueue() {
        return QueueBuilder
                .durable(RIDE_END_QUEUE)
                .deadLetterExchange(SERVER_TO_SERVER_EXCHANGE)
                .deadLetterRoutingKey(RIDE_SERVICE_DLQ_BINDING_KEY)
                .build();
    }

    @Bean
    public Queue paymentMethodStatusQueue() {
        return QueueBuilder
                .durable(PAYMENT_METHOD_STATUS_QUEUE)
                .deadLetterExchange(SERVER_TO_SERVER_EXCHANGE)
                .deadLetterRoutingKey(CAR_SERVICE_DLQ_BINDING_KEY)
                .build();
    }
}
