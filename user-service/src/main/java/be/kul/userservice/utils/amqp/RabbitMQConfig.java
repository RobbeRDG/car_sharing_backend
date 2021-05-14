package be.kul.userservice.utils.amqp;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@Configuration
public class RabbitMQConfig {
    public static final String INTERNAL_EXCHANGE = "internalExchange";


    @Resource(name="internalRabbitAdmin")
    private RabbitAdmin internalRabbitAdmin;


    @PostConstruct
    public void RabbitInit() {

    }

    @Bean
    public TopicExchange internalExchange() {
        return new TopicExchange(INTERNAL_EXCHANGE);
    }

}
