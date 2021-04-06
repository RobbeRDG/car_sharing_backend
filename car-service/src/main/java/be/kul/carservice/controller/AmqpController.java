package be.kul.carservice.controller;

import be.kul.carservice.entity.Car;
import be.kul.carservice.service.CarService;
import be.kul.carservice.utils.amqp.RabbitMQConfig;
import be.kul.carservice.utils.json.jsonObjects.CarStateUpdate;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;

public class AmqpController {
    @Autowired
    CarService carService;

    @Autowired
    private ObjectMapper objectMapper;


    Logger logger = LoggerFactory.getLogger(AmqpController.class);

    @RabbitListener(queues = RabbitMQConfig.CAR_STATE_UPDATE_QUEUE_NAME)
    public void updateCarState(@Payload String stateUpdateString) throws JsonProcessingException {
        //get the stateUpdate object from the string
        CarStateUpdate stateUpdate = objectMapper.readValue(stateUpdateString, CarStateUpdate.class);

        //Update the car state
        carService.updateCarState(stateUpdate);
    }
}
