package be.kul.carservice.utils.jsonObjects;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.locationtech.jts.geom.Point;
import org.n52.jackson.datatype.jts.GeometryDeserializer;
import org.n52.jackson.datatype.jts.GeometrySerializer;

@NoArgsConstructor
@AllArgsConstructor
@Setter
public class CarStateUpdate {
    private int remainingFuelInKilometers;
    @JsonSerialize( using = GeometrySerializer.class)
    @JsonDeserialize( contentUsing = GeometryDeserializer.class)
    private Point location;

    public int getRemainingFuelInKilometers() {
        return remainingFuelInKilometers;
    }

    public Point getLocation() {
        return location;
    }
}
