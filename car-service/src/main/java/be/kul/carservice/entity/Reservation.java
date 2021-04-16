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
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

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
    private LocalDateTime createdOn;
    @NotNull
    @JsonView(Views.CarView.Reserved.class)
    private LocalDateTime validUntil;
    @JsonIgnore
    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "car_id")
    private Car car;


    public Reservation(@NotNull String userId, @NotNull Car car) {
        this.userId = userId;
        this.car = car;

        LocalDateTime now = LocalDateTime.now();
        this.createdOn = now;
        this.validUntil = now.plus(reservationIntervalInSeconds, ChronoUnit.SECONDS);
    }

    public LocalDateTime getValidUntil() {
        return validUntil;
    }

    public LocalDateTime getCreatedOn() {
        return createdOn;
    }

    public String getUserId() {
        return userId;
    }
}
