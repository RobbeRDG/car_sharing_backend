package be.kul.carservice.utils.amqp;

import be.kul.carservice.controller.amqp.AmqpConsumerController;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@Configuration
public class RabbitMQConfig {
    public static final String INTERNAL_EXCHANGE = "internalExchange";
    public static final String EXTERNAL_EXCHANGE = "externalExchange";
    public static final String CAR_SERVICE_DLQ = "carServiceDLQ";
    public static final String CAR_SERVICE_DLQ_BINDING_KEY = "carService.dlq";
    public static final String RIDE_SERVICE_DLQ_BINDING_KEY = "rideService.dlq";
    public static final String BILLING_SERVICE_DLQ_BINDING_KEY = "billingService.dlq";
    public static final String CAR_STATUS_QUEUE = "carStatus";
    public static final String CAR_STATUS_BINDING_KEY = "carService.cars.status.*";
    public static final String RIDE_INITIALISATION_QUEUE = "rideInitialisation";
    public static final String RIDE_INITIALISATION_BINDING_KEY = "rideService.rides.new.*";
    public static final String RIDE_WAYPOINT_QUEUE = "rideWaypoint";
    public static final String RIDE_WAYPOINT_BINDING_KEY = "rideService.rides.waypoint.*";
    public static final String RIDE_END_QUEUE = "rideEnd";
    public static final String RIDE_END_BINDING_KEY = "rideService.rides.end.*";
    public static final String USER_PAYMENT_METHOD_UPDATE_QUEUE = "userPaymentMethodUpdate";
    public static final String USER_PAYMENT_METHOD_UPDATE_BINDING_KEY = "carService.userPaymentMethod.update.*";

    @Resource(name="externalRabbitAdmin")
    private RabbitAdmin externalRabbitAdmin;

    @Resource(name="internalRabbitAdmin")
    private RabbitAdmin internalRabbitAdmin;


    @PostConstruct
    public void RabbitInit() {
        //DLQ
        internalRabbitAdmin.declareExchange(internalExchange());
        internalRabbitAdmin.declareQueue(carServiceDLQ());
        internalRabbitAdmin.declareBinding(
                BindingBuilder.bind(carServiceDLQ())
                        .to(internalExchange())
                        .with(CAR_SERVICE_DLQ_BINDING_KEY));

        //Car state update
        externalRabbitAdmin.declareExchange(externalExchange());
        externalRabbitAdmin.declareQueue(carStatusQueue());
        externalRabbitAdmin.declareBinding(
                BindingBuilder.bind(carStatusQueue())
                        .to(externalExchange())
                        .with(CAR_STATUS_BINDING_KEY));

        //Ride initialisation
        internalRabbitAdmin.declareExchange(internalExchange());
        internalRabbitAdmin.declareQueue(rideInitialisationQueue());
        internalRabbitAdmin.declareBinding(
                BindingBuilder.bind(rideInitialisationQueue())
                        .to(internalExchange())
                        .with(RIDE_INITIALISATION_BINDING_KEY));

        //Ride waypoint
        internalRabbitAdmin.declareExchange(internalExchange());
        internalRabbitAdmin.declareQueue(rideWaypointQueue());
        internalRabbitAdmin.declareBinding(
                BindingBuilder.bind(rideWaypointQueue())
                        .to(internalExchange())
                        .with(RIDE_WAYPOINT_BINDING_KEY));

        //Ride end
        internalRabbitAdmin.declareExchange(internalExchange());
        internalRabbitAdmin.declareQueue(rideEndQueue());
        internalRabbitAdmin.declareBinding(
                BindingBuilder.bind(rideEndQueue())
                        .to(internalExchange())
                        .with(RIDE_END_BINDING_KEY));

        //Payment method update
        internalRabbitAdmin.declareExchange(internalExchange());
        internalRabbitAdmin.declareQueue(userPaymentMethodStatusQueue());
        internalRabbitAdmin.declareBinding(
                BindingBuilder.bind(userPaymentMethodStatusQueue())
                        .to(internalExchange())
                        .with(USER_PAYMENT_METHOD_UPDATE_BINDING_KEY));
    }

    @Bean
    public AmqpConsumerController amqpController() {
        return new AmqpConsumerController();
    }

    @Bean
    public TopicExchange internalExchange() {
        return new TopicExchange(INTERNAL_EXCHANGE);
    }

    @Bean
    public TopicExchange externalExchange() {
        return new TopicExchange(EXTERNAL_EXCHANGE);
    }

    @Bean
    Queue carServiceDLQ() {
        return QueueBuilder
                .durable(CAR_SERVICE_DLQ)
                .deadLetterExchange(INTERNAL_EXCHANGE)
                .build();
    }

    @Bean
    public Queue carStatusQueue() {
        return QueueBuilder
                .durable(CAR_STATUS_QUEUE)
                .deadLetterExchange(INTERNAL_EXCHANGE)
                .deadLetterRoutingKey(CAR_SERVICE_DLQ_BINDING_KEY)
                .build();
    }

    @Bean
    public Queue rideInitialisationQueue() {
        return QueueBuilder
                .durable(RIDE_INITIALISATION_QUEUE)
                .deadLetterExchange(INTERNAL_EXCHANGE)
                .deadLetterRoutingKey(RIDE_SERVICE_DLQ_BINDING_KEY)
                .build();
    }

    @Bean
    public Queue rideWaypointQueue() {
        return QueueBuilder
                .durable(RIDE_WAYPOINT_QUEUE)
                .deadLetterExchange(INTERNAL_EXCHANGE)
                .deadLetterRoutingKey(RIDE_SERVICE_DLQ_BINDING_KEY)
                .build();
    }

    @Bean
    public Queue rideEndQueue() {
        return QueueBuilder
                .durable(RIDE_END_QUEUE)
                .deadLetterExchange(INTERNAL_EXCHANGE)
                .deadLetterRoutingKey(RIDE_SERVICE_DLQ_BINDING_KEY)
                .build();
    }

    @Bean
    public Queue userPaymentMethodStatusQueue() {
        return QueueBuilder
                .durable(USER_PAYMENT_METHOD_UPDATE_QUEUE)
                .deadLetterExchange(INTERNAL_EXCHANGE)
                .deadLetterRoutingKey(CAR_SERVICE_DLQ_BINDING_KEY)
                .build();
    }
}
