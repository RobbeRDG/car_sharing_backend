package be.kul.carservice.utils.amqp;

import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class RabbitMQConnectionConfig {
    @Value("${spring.rabbitmq.external.host}")
    private String RABBITMQ_EXTERNAL_CONNECTION_HOST;

    @Value("${spring.rabbitmq.external.username}")
    private String RABBITMQ_EXTERNAL_CONNECTION_USERNAME;

    @Value("${spring.rabbitmq.external.password}")
    private String RABBITMQ_EXTERNAL_CONNECTION_PASSWORD;

    @Value("${spring.rabbitmq.internal.host}")
    private String RABBITMQ_INTERNAL_CONNECTION_HOST;

    @Value("${spring.rabbitmq.internal.username}")
    private String RABBITMQ_INTERNAL_CONNECTION_USERNAME;

    @Value("${spring.rabbitmq.internal.password}")
    private String RABBITMQ_INTERNAL_CONNECTION_PASSWORD;


    @Bean(name = "externalConnectionFactory")
    public CachingConnectionFactory externalConnectionFactory() {
        CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory(RABBITMQ_EXTERNAL_CONNECTION_HOST);
        cachingConnectionFactory.setUsername(RABBITMQ_EXTERNAL_CONNECTION_USERNAME);
        cachingConnectionFactory.setVirtualHost(RABBITMQ_EXTERNAL_CONNECTION_USERNAME);
        cachingConnectionFactory.setPassword(RABBITMQ_EXTERNAL_CONNECTION_PASSWORD);
        cachingConnectionFactory.setRequestedHeartBeat(30);
        cachingConnectionFactory.setConnectionTimeout(30000);
        return cachingConnectionFactory;
    }

    @Bean(name = "externalRabbitListenerContainerFactory")
    public SimpleRabbitListenerContainerFactory externalRabbitListenerContainerFactory() {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(externalConnectionFactory());
        return factory;
    }

    @Bean(name = "externalRabbitTemplate")
    public RabbitTemplate externalRabbitTemplate() {
        return new RabbitTemplate(externalConnectionFactory());
    }

    @Bean(name = "externalRabbitAdmin")
    public RabbitAdmin externalRabbitAdmin() {
        return new RabbitAdmin(externalConnectionFactory());
    }

    @Bean(name = "internalConnectionFactory")
    @Primary
    public CachingConnectionFactory internalConnectionFactory() {
        CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory(RABBITMQ_INTERNAL_CONNECTION_HOST);
        cachingConnectionFactory.setUsername(RABBITMQ_INTERNAL_CONNECTION_USERNAME);
        cachingConnectionFactory.setVirtualHost(RABBITMQ_INTERNAL_CONNECTION_USERNAME);
        cachingConnectionFactory.setPassword(RABBITMQ_INTERNAL_CONNECTION_PASSWORD);
        cachingConnectionFactory.setRequestedHeartBeat(30);
        cachingConnectionFactory.setConnectionTimeout(30000);
        return cachingConnectionFactory;
    }

    @Bean(name = "internalRabbitListenerContainerFactory")
    public SimpleRabbitListenerContainerFactory internalRabbitListenerContainerFactory() {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(internalConnectionFactory());
        return factory;
    }

    @Bean(name = "internalRabbitTemplate")
    @Primary
    public RabbitTemplate internalRabbitTemplate() {
        return new RabbitTemplate(internalConnectionFactory());
    }

    @Bean(name = "internalRabbitAdmin")
    public RabbitAdmin internalRabbitAdmin() {
        return new RabbitAdmin(internalConnectionFactory());
    }

    @Bean(name = "jsonMessageConverter")
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
