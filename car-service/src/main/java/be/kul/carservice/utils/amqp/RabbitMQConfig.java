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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.handler.annotation.support.DefaultMessageHandlerMethodFactory;

@Configuration
public class RabbitMQConfig {
    private final static String RABBITMQ_CONNECTION_HOST = "rat.rmq2.cloudamqp.com";
    private final static String RABBITMQ_CONNECTION_PASSWORD = "ntVEoqTWlN38mXwCrhgdHUmDaOwB86N1";
    private final static String RABBITMQ_CONNECTION_USERNAME = "iyopjwwd";
    public final static String CAR_STATE_UPDATE_QUEUE_NAME = "carStateUpdatesToServer";
    public final static String CAR_STATE_EXCHANGE_NAME = "carStateExchangeToServer";
    public final static String STATE_UPDATE_BINDING_KEY = "stateUpdate";

    @Bean
    public CachingConnectionFactory connectionFactory() {
		CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory(RABBITMQ_CONNECTION_HOST);
		cachingConnectionFactory.setUsername(RABBITMQ_CONNECTION_USERNAME);
        cachingConnectionFactory.setVirtualHost(RABBITMQ_CONNECTION_USERNAME);
		cachingConnectionFactory.setPassword(RABBITMQ_CONNECTION_PASSWORD);
        cachingConnectionFactory.setRequestedHeartBeat(30);
        cachingConnectionFactory.setConnectionTimeout(30000);
		return cachingConnectionFactory;
	}

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory() {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory());
        return factory;
    }

    @Bean
    public RabbitTemplate rabbitTemplate() {
        return new RabbitTemplate(connectionFactory());
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }


    @Bean
    public Queue carStateUpdateQueue() {
        return new Queue(CAR_STATE_UPDATE_QUEUE_NAME);
    }

    @Bean
    public DirectExchange carStateExchange() {
        return new DirectExchange(CAR_STATE_EXCHANGE_NAME);
    }


    @Bean
    public Binding carStateUpdateBinding(DirectExchange exchange,
                                         Queue queue) {
        return BindingBuilder.bind(queue)
                .to(exchange)
                .with(STATE_UPDATE_BINDING_KEY);
    }

    @Bean
    public AmqpController amqpController() {
        return new AmqpController();
    }

}
