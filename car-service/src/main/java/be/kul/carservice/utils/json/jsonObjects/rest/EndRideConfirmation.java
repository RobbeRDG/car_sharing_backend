package be.kul.carservice.utils.json.jsonObjects.rest;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EndRideConfirmation {
    private boolean successfullyEnded;
    private String message;
}
