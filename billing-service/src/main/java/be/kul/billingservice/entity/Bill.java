package be.kul.billingservice.entity;

import be.kul.billingservice.utils.json.jsonObjects.amqpMessages.billing.BillInitialisation;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.Duration;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class Bill {
    @Id
    private long billId;
    private long rideId;
    private long carId;
    private String userId;
    private LocalDateTime createdOn;
    @Value("${spring.configurations.billing.ratePerMinuteInEur}")
    private double ratePerMinuteInEur;
    @Value("${spring.configurations.billing.startupRateInEur}")
    private double startupRateInEur;
    private double billTotalAmountInEur;

    public Bill(BillInitialisation billInitialisation) {
        this.rideId = billInitialisation.getRideId();
        this.carId = billInitialisation.getCarId();
        this.userId = billInitialisation.getUserId();
    }

    public static Bill calculateBill(BillInitialisation billInitialisation) {
        //Get the total duration of the ride
        LocalDateTime rideStart = billInitialisation.getStartedOn();
        LocalDateTime rideStop = billInitialisation.getFinishedOn();
        Duration rideDuration = Duration.between(rideStart, rideStop);

        //Generate the starter bill object
        Bill bill = new Bill(billInitialisation);

        //Calculate the billTotalAmount
        double billTotalInEur = 0;
        billTotalInEur += bill.getStartupRateInEur();
        billTotalInEur += bill.getRatePerMinuteInEur() * rideDuration.toMinutes();

        //Set the bill total amount
        bill.setBillTotalAmountInEur(billTotalInEur);

        return bill;
    }
}
