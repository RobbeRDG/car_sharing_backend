package be.kul.carservice.controller.amqp;

import be.kul.carservice.service.CarService;
import be.kul.carservice.utils.amqp.RabbitMQConfig;
import be.kul.carservice.utils.json.jsonObjects.amqpMessages.car.CarStatusUpdate;
import be.kul.carservice.utils.json.jsonObjects.amqpMessages.payment.UserPaymentMethodStatusUpdate;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;

public class AmqpConsumerController {
    @Autowired
    CarService carService;

    @Autowired
    private ObjectMapper objectMapper;


    @RabbitListener(queues = RabbitMQConfig.CAR_STATUS_QUEUE, containerFactory = "externalRabbitListenerContainerFactory")
    public void updateCarState(@Payload String stateUpdateString, @Header(AmqpHeaders.RECEIVED_ROUTING_KEY) String routingKey) throws JsonProcessingException {
        //get the stateUpdate object from the string
        CarStatusUpdate stateUpdate = objectMapper.readValue(stateUpdateString, CarStatusUpdate.class);

        //Get the car id from the routing key
        //Ex routing key: carService.cars.state.{carId}
        String carIdString = routingKey.split("\\.")[3];
        long carId = Integer.parseInt(carIdString);

        //Set the carId for the car state update
        stateUpdate.setCarId(carId);

        //Update the car state
        carService.updateCarStatus(stateUpdate);
    }

    @RabbitListener(queues = RabbitMQConfig.USER_PAYMENT_METHOD_UPDATE_QUEUE, containerFactory = "internalRabbitListenerContainerFactory")
    public void updatePaymentMethodStatus(@Payload String paymentMethodUpdateString) throws JsonProcessingException {
        //get the PaymentMethodStatusUpdate object from the string
        UserPaymentMethodStatusUpdate userPaymentMethodStatusUpdate = objectMapper.readValue(paymentMethodUpdateString, UserPaymentMethodStatusUpdate.class);

        //Handle the new waypoint
        carService.updateUserPaymentMethodStatus(userPaymentMethodStatusUpdate);
    }
}
