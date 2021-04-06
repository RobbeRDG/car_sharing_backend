package be.kul.carservice.entity;

import be.kul.carservice.utils.json.jsonViews.Views;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

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
    @GeneratedValue(generator = "reservation-sequence-generator")
    @GenericGenerator(
            name = "reservation-sequence-generator",
            strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {
                    @Parameter(name = "sequence_name", value = "reservation_sequence"),
                    @Parameter(name = "initial_value", value = "1"),
                    @Parameter(name = "increment_size", value = "1")
            }
    )
    @JsonView(Views.CarView.Reserved.class)
    private long reservationId;

    @NotNull
    @JsonView(Views.CarView.Reserved.class)
    private String userId;
    @NotNull
    @JsonView(Views.CarView.Reserved.class)
    private Timestamp createdOn;
    @NotNull
    @JsonView(Views.CarView.Reserved.class)
    private Timestamp validUntil;
    @JsonIgnore
    @ManyToOne(cascade = CascadeType.PERSIST)
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
