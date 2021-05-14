package be.kul.carservice.utils.json.jsonObjects.amqpMessages.car;

import be.kul.carservice.utils.json.jsonObjects.amqpMessages.AmqpMessage;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

@NoArgsConstructor
@Getter
@Setter
@Component
public class CarRequest extends AmqpMessage {
    private String requestType;
    private long carId;
    private int expirationTimeInMilliseconds;

    public CarRequest(String requestType, long carId, int expirationTimeInMilliseconds) {
        super();
        this.requestType = requestType;
        this.carId = carId;
        this.expirationTimeInMilliseconds = expirationTimeInMilliseconds;
    }


    public String getRequestType() {
        return requestType;
    }


    public long getCarId() {
        return carId;
    }
}
