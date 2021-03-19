package be.kul.carservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class Reservation {
    private static final int reservationIntervalInSeconds = 900;

    @Id
    @GeneratedValue
    private long reservationId;

    @NotNull
    private String userId;
    @NotNull
    private Timestamp createdOn;
    @NotNull
    private Timestamp validUntil;
    @JsonIgnore
    @ManyToOne()
    @JoinColumn(name = "car_id")
    private Car car;


    public Reservation(@NotNull String userId, @NotNull Car car) {
        this.userId = userId;
        this.car = car;

        long currentTime = System.currentTimeMillis();
        this.createdOn = new Timestamp(currentTime);
        this.validUntil = new Timestamp(currentTime + reservationIntervalInSeconds*1000); // *1000 because milliseconds
    }

    public Timestamp getValidUntil() {
        return validUntil;
    }

    public Timestamp getCreatedOn() {
        return createdOn;
    }
}
