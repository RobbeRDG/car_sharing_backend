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
    public static final String RIDE_SERVICE_DLQ = "rideServiceDLQ";
    public static final String RIDE_SERVICE_DLQ_BINDING_KEY = "rideService.dlq";
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
        TopicExchange serverToServer = new TopicExchange(SERVER_TO_SERVER_EXCHANGE);
        //DLQ
        internalRabbitAdmin.declareExchange(serverToServer);
        internalRabbitAdmin.declareQueue(rideServiceDLQ());
        internalRabbitAdmin.declareBinding(
                BindingBuilder.bind(rideServiceDLQ())
                        .to(serverToServer)
                        .with(RIDE_SERVICE_DLQ_BINDING_KEY));


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

        //Payment initialisation
        internalRabbitAdmin.declareExchange(serverToServer);
        internalRabbitAdmin.declareQueue(billInitialisationQueue());
        internalRabbitAdmin.declareBinding(
                BindingBuilder.bind(billInitialisationQueue())
                        .to(serverToServer)
                        .with(BILL_INITIALISATION_BINDING_KEY));
    }

    @Bean
    public AmqpConsumerController amqpController() {
        return new AmqpConsumerController();
    }

    @Bean
    Queue rideServiceDLQ() {
        return QueueBuilder
                .durable(RIDE_SERVICE_DLQ)
                .deadLetterExchange(SERVER_TO_SERVER_EXCHANGE)
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
    public Queue billInitialisationQueue() {
        return QueueBuilder
                .durable(BILL_INITIALISATION_QUEUE)
                .build();
    }

}
