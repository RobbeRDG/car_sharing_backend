package be.kul.rideservice.utils.amqp;

import be.kul.rideservice.controller.amqp.AmqpConsumerController;
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
    public static final String RIDE_SERVICE_DLQ = "rideServiceDLQ";
    public static final String RIDE_SERVICE_DLQ_BINDING_KEY = "rideService.dlq";
    public static final String BILLING_SERVICE_DLQ_BINDING_KEY = "billingService.dlq";
    public static final String RIDE_INITIALISATION_QUEUE = "rideInitialisation";
    public static final String RIDE_INITIALISATION_BINDING_KEY = "rideService.rides.new.*";
    public static final String RIDE_WAYPOINT_QUEUE = "rideWaypoint";
    public static final String RIDE_WAYPOINT_BINDING_KEY = "rideService.rides.waypoint.*";
    public static final String RIDE_END_QUEUE = "rideEnd";
    public static final String RIDE_END_BINDING_KEY = "rideService.rides.end.*";
    public static final String BILL_INITIALISATION_QUEUE = "billInitialisation";
    public static final String BILL_INITIALISATION_BINDING_KEY = "billingService.billRequest.new.*";
    public static final String BILL_STATUS_UPDATE_QUEUE = "billStatusUpdateQueue";
    public static final String BILL_STATUS_UPDATE_BINDING_KEY = "rideService.billStatus.update.*";


    @Resource(name="internalRabbitAdmin")
    private RabbitAdmin internalRabbitAdmin;


    @PostConstruct
    public void RabbitInit() {
        //DLQ
        internalRabbitAdmin.declareExchange(internalExchange());
        internalRabbitAdmin.declareQueue(rideServiceDLQ());
        internalRabbitAdmin.declareBinding(
                BindingBuilder.bind(rideServiceDLQ())
                        .to(internalExchange())
                        .with(RIDE_SERVICE_DLQ_BINDING_KEY));


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

        //Payment initialisation
        internalRabbitAdmin.declareExchange(internalExchange());
        internalRabbitAdmin.declareQueue(billInitialisationQueue());
        internalRabbitAdmin.declareBinding(
                BindingBuilder.bind(billInitialisationQueue())
                        .to(internalExchange())
                        .with(BILL_INITIALISATION_BINDING_KEY));

        //Bill Status update queue
        internalRabbitAdmin.declareExchange(internalExchange());
        internalRabbitAdmin.declareQueue(billStatusUpdateQueue());
        internalRabbitAdmin.declareBinding(
                BindingBuilder.bind(billStatusUpdateQueue())
                        .to(internalExchange())
                        .with(BILL_STATUS_UPDATE_BINDING_KEY));
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
    Queue rideServiceDLQ() {
        return QueueBuilder
                .durable(RIDE_SERVICE_DLQ)
                .deadLetterExchange(INTERNAL_EXCHANGE)
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
    public Queue billInitialisationQueue() {
        return QueueBuilder
                .durable(BILL_INITIALISATION_QUEUE)
                .deadLetterExchange(INTERNAL_EXCHANGE)
                .deadLetterRoutingKey(BILLING_SERVICE_DLQ_BINDING_KEY)
                .build();
    }

    @Bean
    public Queue billStatusUpdateQueue() {
        return QueueBuilder
                .durable(BILL_STATUS_UPDATE_QUEUE)
                .deadLetterExchange(INTERNAL_EXCHANGE)
                .deadLetterRoutingKey(RIDE_SERVICE_DLQ_BINDING_KEY)
                .build();
    }

}
