package be.kul.rideservice.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class DamageReport {
    @Id
    @GeneratedValue(generator = "carImage-sequence-generator")
    @GenericGenerator(
            name = "carImage-sequence-generator",
            strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {
                    @org.hibernate.annotations.Parameter(name = "sequence_name", value = "carImage_sequence"),
                    @org.hibernate.annotations.Parameter(name = "initial_value", value = "1"),
                    @org.hibernate.annotations.Parameter(name = "increment_size", value = "1")
            }
    )
    private long reportId;
    @NotNull
    private String imageType;
    @NotNull
    @Column(columnDefinition = "LONGBLOB")
    private byte[] imageBytes;
    private String description;
    @NotNull
    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name="ride_id")
    private Ride ride;
    @NotNull
    private LocalDateTime createdOn;

    public DamageReport(Ride ride, String imageType, byte[] imageBytes, String description) {
        this.ride = ride;
        this.imageType = imageType;
        this.imageBytes = imageBytes;
        this.description = description;
        this.createdOn = LocalDateTime.now();
    }
}
