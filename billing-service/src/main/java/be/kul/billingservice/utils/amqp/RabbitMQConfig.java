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
    public static final String BILL_INITIALISATION_QUEUE = "billInitialisation";
    public static final String BILL_INITIALISATION_BINDING_KEY = "paymentService.bill.new.*";
    public static final String USER_ADD_QUEUE = "addUser";
    public static final String USER_ADD_BINDING_KEY = "paymentService.user.new.*";


    @Resource(name="internalRabbitAdmin")
    private RabbitAdmin internalRabbitAdmin;


    @PostConstruct
    public void RabbitInit() {
        //Payment initialisation
        internalRabbitAdmin.declareExchange(new TopicExchange(SERVER_TO_SERVER_EXCHANGE));
        internalRabbitAdmin.declareQueue(new Queue(BILL_INITIALISATION_QUEUE));
        internalRabbitAdmin.declareBinding(
                BindingBuilder.bind(new Queue(BILL_INITIALISATION_QUEUE))
                        .to(new TopicExchange(SERVER_TO_SERVER_EXCHANGE))
                        .with(BILL_INITIALISATION_BINDING_KEY));

        //Add new user
        internalRabbitAdmin.declareExchange(new TopicExchange(SERVER_TO_SERVER_EXCHANGE));
        internalRabbitAdmin.declareQueue(new Queue(USER_ADD_QUEUE));
        internalRabbitAdmin.declareBinding(
                BindingBuilder.bind(new Queue(USER_ADD_QUEUE))
                        .to(new TopicExchange(SERVER_TO_SERVER_EXCHANGE))
                        .with(USER_ADD_BINDING_KEY));
    }

    @Bean
    public AmqpConsumerController amqpController() {
        return new AmqpConsumerController();
    }

}
