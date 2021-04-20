package be.kul.billingservice.utils.amqp;

import be.kul.billingservice.controller.amqp.AmqpConsumerController;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@Configuration
public class RabbitMQConfig {
    public static final String INTERNAL_EXCHANGE = "internalExchange";
    public static final String BILLING_SERVICE_DLQ = "billingServiceDLQ";
    public static final String BILLING_SERVICE_DLQ_BINDING_KEY = "billingService.dlq";
    public static final String CAR_SERVICE_DLQ_BINDING_KEY = "carService.dlq";
    public static final String RIDE_SERVICE_DLQ_BINDING_KEY = "rideService.dlq";
    public static final String BILL_INITIALISATION_QUEUE = "billInitialisation";
    public static final String BILL_INITIALISATION_BINDING_KEY = "billingService.billRequest.new.*";
    public static final String USER_PAYMENT_METHOD_UPDATE_QUEUE = "userPaymentMethodUpdate";
    public static final String USER_PAYMENT_METHOD_UPDATE_BINDING_KEY = "carService.userPaymentMethod.update.*";
    public static final String BILL_PROCESSING_QUEUE = "billProcessingQueue";
    public static final String BILL_PROCESSING_BINDING_KEY = "billingService.billProcessing.new.*";
    public static final String BILL_STATUS_UPDATE_QUEUE = "billStatusUpdateQueue";
    public static final String BILL_STATUS_UPDATE_BINDING_KEY = "rideService.billStatus.update.*";


    @Resource(name="internalRabbitAdmin")
    private RabbitAdmin internalRabbitAdmin;


    @PostConstruct
    public void RabbitInit() {
        //DLQ
        internalRabbitAdmin.declareExchange(internalExchange());
        internalRabbitAdmin.declareQueue(billingServiceDLQ());
        internalRabbitAdmin.declareBinding(
                BindingBuilder.bind(billingServiceDLQ())
                        .to(internalExchange())
                        .with(BILLING_SERVICE_DLQ_BINDING_KEY));

        //Payment initialisation
        internalRabbitAdmin.declareExchange(internalExchange());
        internalRabbitAdmin.declareQueue(billInitialisationQueue());
        internalRabbitAdmin.declareBinding(
                BindingBuilder.bind(billInitialisationQueue())
                        .to(internalExchange())
                        .with(BILL_INITIALISATION_BINDING_KEY));

        //User payment method update
        internalRabbitAdmin.declareExchange(internalExchange());
        internalRabbitAdmin.declareQueue(userPaymentMethodUpdateQueue());
        internalRabbitAdmin.declareBinding(
                BindingBuilder.bind(userPaymentMethodUpdateQueue())
                        .to(internalExchange())
                        .with(USER_PAYMENT_METHOD_UPDATE_BINDING_KEY));

        //Payment processing queue
        internalRabbitAdmin.declareExchange(internalExchange());
        internalRabbitAdmin.declareQueue(paymentProcessingQueue());
        internalRabbitAdmin.declareBinding(
                BindingBuilder.bind(paymentProcessingQueue())
                        .to(internalExchange())
                        .with(BILL_PROCESSING_BINDING_KEY));

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
    Queue billingServiceDLQ() {
        return QueueBuilder
                .durable(BILLING_SERVICE_DLQ)
                .deadLetterExchange(INTERNAL_EXCHANGE)
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
    public Queue userPaymentMethodUpdateQueue() {
        return QueueBuilder
                .durable(USER_PAYMENT_METHOD_UPDATE_QUEUE)
                .deadLetterExchange(INTERNAL_EXCHANGE)
                .deadLetterRoutingKey(CAR_SERVICE_DLQ_BINDING_KEY)
                .build();
    }

    @Bean
    public Queue paymentProcessingQueue() {
        return QueueBuilder
                .durable(BILL_PROCESSING_QUEUE)
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
