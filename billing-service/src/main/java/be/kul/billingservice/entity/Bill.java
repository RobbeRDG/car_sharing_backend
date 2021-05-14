package be.kul.billingservice.entity;

import be.kul.billingservice.utils.helperObjects.BillStatusEnum;
import be.kul.billingservice.utils.json.jsonObjects.amqpMessages.billing.BillInitialisation;
import be.kul.billingservice.utils.json.jsonViews.Views;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.Duration;
import java.time.LocalDateTime;

@Slf4j
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class Bill {
    @Id
    @GeneratedValue(generator = "bill-sequence-generator")
    @GenericGenerator(
            name = "bill-sequence-generator",
            strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {
                    @Parameter(name = "sequence_name", value = "bill_sequence"),
                    @Parameter(name = "initial_value", value = "1"),
                    @Parameter(name = "increment_size", value = "1")
            }
    )
    @JsonView(Views.BillView.Basic.class)
    private long billId;
    @NotNull
    @JsonView(Views.BillView.Basic.class)
    private long rideId;
    @NotNull
    @JsonView(Views.BillView.Detail.class)
    private long carId;
    @NotNull
    @JsonView(Views.BillView.Detail.class)
    private String userId;
    @NotNull
    @JsonView(Views.BillView.Detail.class)
    private LocalDateTime createdOn;
    @NotNull
    @JsonView(Views.BillView.Detail.class)
    private double ratePerMinuteInEur;
    @NotNull
    @JsonView(Views.BillView.Detail.class)
    private double startupRateInEur;
    @NotNull
    @JsonView(Views.BillView.Basic.class)
    private double billTotalAmountInEur;
    @NotNull
    @Enumerated(EnumType.STRING)
    @JsonView(Views.BillView.Basic.class)
    private BillStatusEnum billStatus;
    @NotNull
    @JsonView(Views.BillView.Admin.class)
    private LocalDateTime lastStateUpdate;



    private Bill(BillInitialisation billInitialisation) {
        this.createdOn = LocalDateTime.now();
        this.rideId = billInitialisation.getRideId();
        this.carId = billInitialisation.getCarId();
        this.userId = billInitialisation.getUserId();
    }

    public static Bill calculateBill(BillInitialisation billInitialisation) {
        //Generate the starter bill object
        Bill bill = new Bill(billInitialisation);

        //Set the bill's startup and rate per minute according to the carId
        long carId = bill.getCarId();
        bill.setStartupAndPerMinuteRate(carId);

        //Get the total duration of the ride
        LocalDateTime rideStart = billInitialisation.getStartedOn();
        LocalDateTime rideStop = billInitialisation.getFinishedOn();
        Duration rideDuration = Duration.between(rideStart, rideStop);


        //Calculate the billTotalAmount
        double billTotalInEur = 0;
        billTotalInEur += bill.getStartupRateInEur();
        billTotalInEur += bill.getRatePerMinuteInEur() * rideDuration.toMinutes();


        //Set the bill total amount
        bill.setBillTotalAmountInEur(billTotalInEur);


        //Set the bill status
        bill.setBillStatus(BillStatusEnum.INITIALISED);
        bill.lastStateUpdate = LocalDateTime.now();

        return bill;
    }

    private void setStartupAndPerMinuteRate(long carId) {
        startupRateInEur = 5.00;
        ratePerMinuteInEur = 0.20;
    }
}
