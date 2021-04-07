package be.kul.carservice.utils.json.jsonObjects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CarLockRequest {
    private long rideId;
    private long carId;
    private Timestamp createdOn;
    private boolean lockCar;

    public CarLockRequest(long rideId, long carId, boolean lockCar) {
        this.rideId = rideId;
        this.carId = carId;
        this.lockCar = lockCar;
    }

    public String getCarIdString() {
        return String.valueOf(carId);
    }

    public long getCarId() {
        return carId;
    }
}
