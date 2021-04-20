package be.kul.carservice.utils.json.jsonObjects.amqpMessages.car;

import be.kul.carservice.utils.json.jsonObjects.amqpMessages.AmqpMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CarRequest extends AmqpMessage {
    private String requestType;
    private long carId;
    @Value("${spring.configurations.carRequest.expirationTimeInMilliseconds}")
    private int expirationTimeInMilliseconds;

    public CarRequest(String requestType, long carId) {
        super();
        this.requestType = requestType;
        this.carId = carId;
    }

    public String getRequestType() {
        return requestType;
    }


    public long getCarId() {
        return carId;
    }

    public int getExpirationTimeInMilliseconds() {
        return expirationTimeInMilliseconds;
    }

    public String getCarIdString() {
        return String.valueOf(carId);
    }
}
