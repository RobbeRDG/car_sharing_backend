package be.kul.carservice.utils.amqp;

import be.kul.carservice.controller.AmqpController;
import com.rabbitmq.client.ConnectionFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.handler.annotation.support.DefaultMessageHandlerMethodFactory;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@Configuration
public class RabbitMQConfig {
    public final static String CAR_STATE_UPDATE_QUEUE_NAME = "carStateUpdatesToServer";
    public final static String CAR_STATE_EXCHANGE_NAME = "carStateExchangeToServer";
    public final static String STATE_UPDATE_BINDING_KEY = "stateUpdate";

    @Resource(name="externalRabbitAdmin")
    private RabbitAdmin externalRabbitAdmin;

    @Resource(name="internalRabbitAdmin")
    private RabbitAdmin internalRabbitAdmin;


    @PostConstruct
    public void RabbitInit() {
        externalRabbitAdmin.declareExchange(new DirectExchange(CAR_STATE_EXCHANGE_NAME));
        externalRabbitAdmin.declareQueue(new Queue(CAR_STATE_UPDATE_QUEUE_NAME));
        externalRabbitAdmin.declareBinding(
                BindingBuilder.bind(new Queue(CAR_STATE_UPDATE_QUEUE_NAME))
                        .to(new DirectExchange(CAR_STATE_EXCHANGE_NAME))
                        .with(STATE_UPDATE_BINDING_KEY));
    }

    @Bean
    public AmqpController amqpController() {
        return new AmqpController();
    }

}
