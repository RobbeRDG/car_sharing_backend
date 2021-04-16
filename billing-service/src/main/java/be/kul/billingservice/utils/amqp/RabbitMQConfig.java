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
    public static final String SERVER_TO_SERVER_EXCHANGE = "serverToServer";
    public static final String BILLING_SERVICE_DLQ = "billingServiceDLQ";
    public static final String BILLING_SERVICE_DLQ_BINDING_KEY = "billingService.dlq";
    public static final String BILL_INITIALISATION_QUEUE = "billInitialisation";
    public static final String BILL_INITIALISATION_BINDING_KEY = "paymentService.bill.new.*";
    public static final String USER_PAYMENT_METHOD_UPDATE_QUEUE = "userPaymentMethodUpdate";
    public static final String USER_PAYMENT_METHOD_UPDATE_BINDING_KEY = "carService.userPaymentMethod.update.*";
    public static final String BILL_PROCESSING_QUEUE = "billProcessingQueue";
    public static final String BILL_PROCESSING_BINDING_KEY = "carService.billProcessing.new.*";


    @Resource(name="internalRabbitAdmin")
    private RabbitAdmin internalRabbitAdmin;


    @PostConstruct
    public void RabbitInit() {
        TopicExchange serverToServer = new TopicExchange(SERVER_TO_SERVER_EXCHANGE);
        //DLQ
        internalRabbitAdmin.declareExchange(serverToServer);
        internalRabbitAdmin.declareQueue(billingServiceDLQ());
        internalRabbitAdmin.declareBinding(
                BindingBuilder.bind(billingServiceDLQ())
                        .to(serverToServer)
                        .with(BILLING_SERVICE_DLQ_BINDING_KEY));

        //Payment initialisation
        internalRabbitAdmin.declareExchange(serverToServer);
        internalRabbitAdmin.declareQueue(billInitialisationQueue());
        internalRabbitAdmin.declareBinding(
                BindingBuilder.bind(billInitialisationQueue())
                        .to(serverToServer)
                        .with(BILL_INITIALISATION_BINDING_KEY));

        //User payment method update
        internalRabbitAdmin.declareExchange(serverToServer);
        internalRabbitAdmin.declareQueue(userPaymentMethodUpdateQueue());
        internalRabbitAdmin.declareBinding(
                BindingBuilder.bind(userPaymentMethodUpdateQueue())
                        .to(serverToServer)
                        .with(USER_PAYMENT_METHOD_UPDATE_BINDING_KEY));

        //Payment processing queue
        internalRabbitAdmin.declareExchange(serverToServer);
        internalRabbitAdmin.declareQueue(paymentProcessingQueue());
        internalRabbitAdmin.declareBinding(
                BindingBuilder.bind(paymentProcessingQueue())
                        .to(serverToServer)
                        .with(BILL_PROCESSING_BINDING_KEY));
    }

    @Bean
    public AmqpConsumerController amqpController() {
        return new AmqpConsumerController();
    }

    @Bean
    Queue billingServiceDLQ() {
        return QueueBuilder
                .durable(BILLING_SERVICE_DLQ)
                .deadLetterExchange(SERVER_TO_SERVER_EXCHANGE)
                .build();
    }

    @Bean
    public Queue billInitialisationQueue() {
        return QueueBuilder
                .durable(BILL_INITIALISATION_QUEUE)
                .deadLetterExchange(SERVER_TO_SERVER_EXCHANGE)
                .deadLetterRoutingKey(BILLING_SERVICE_DLQ_BINDING_KEY)
                .build();
    }

    @Bean
    public Queue userPaymentMethodUpdateQueue() {
        return QueueBuilder
                .durable(USER_PAYMENT_METHOD_UPDATE_QUEUE)
                .deadLetterExchange(SERVER_TO_SERVER_EXCHANGE)
                .deadLetterRoutingKey(BILLING_SERVICE_DLQ_BINDING_KEY)
                .build();
    }

    @Bean
    public Queue paymentProcessingQueue() {
        return QueueBuilder
                .durable(BILL_PROCESSING_QUEUE)
                .deadLetterExchange(SERVER_TO_SERVER_EXCHANGE)
                .deadLetterRoutingKey(BILLING_SERVICE_DLQ_BINDING_KEY)
                .build();
    }

}
