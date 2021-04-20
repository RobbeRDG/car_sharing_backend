package be.kul.billingservice.utils.json.jsonObjects.amqpMessages.billing;

import be.kul.billingservice.entity.Bill;
import be.kul.billingservice.utils.helperObjects.BillStatusEnum;
import be.kul.billingservice.utils.json.jsonObjects.amqpMessages.AmqpMessage;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
public class BillStatusUpdate extends AmqpMessage {
    private long billId;
    private long rideId;
    private BillStatusEnum billStatus;
    private LocalDateTime createdOn;

    public BillStatusUpdate(Bill bill) {
        super();
        this.billId = bill.getBillId();
        this.rideId = bill.getRideId();
        this.billStatus = bill.getBillStatus();
        this.createdOn = bill.getLastStateUpdate();
    }
}
