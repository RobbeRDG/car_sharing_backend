package be.kul.carservice.controller;

import be.kul.carservice.entity.Car;
import be.kul.carservice.service.CarService;
import be.kul.carservice.utils.jsonObjects.CarStateUpdate;
import be.kul.carservice.utils.jsonViews.Views;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path="/car-service")
public class CarController {
    @Autowired
    private CarService carService;

    @PostMapping("/cars")
    public @ResponseBody Car registerCar(@RequestBody Car car) {
        return carService.registerCar(car);
    }

    @PostMapping("/cars/batch")
    public @ResponseBody List<Car> registerCar(@RequestBody List<Car> carList) {
        return carService.registerCars(carList);
    }

    @GetMapping("/cars")
    public @ResponseBody
    @JsonView(Views.CarView.Full.class )
    List<Car> getAllCarsWithinRadius(@RequestParam double longitude, @RequestParam double latitude, @RequestParam double radiusInKM) {
        return carService.findAllCarsWithinRadius(longitude, latitude, radiusInKM);
    }

    @GetMapping("/cars/available")
    @JsonView(Views.CarView.Basic.class)
    public @ResponseBody
    List<Car> getAllAvailableCarsWithinRadius(@RequestParam double longitude, @RequestParam double latitude, @RequestParam double radiusInKM) {
        return carService.findAllAvailableCarsWithinRadius(longitude, latitude, radiusInKM);
    }

    @PutMapping("/cars/state/{id}")
    @JsonView(Views.CarView.Full.class)
    public @ResponseBody Car updateCarState(
            @RequestBody CarStateUpdate stateUpdate,
            @PathVariable long id
    ) {
        return carService.updateCarState(id, stateUpdate);
    }

    @PutMapping("/cars/reservation/{id}")
    @JsonView(Views.CarView.Reserved.class)
    public @ResponseBody Car reserveCar(
            @AuthenticationPrincipal Jwt principal,
            @PathVariable long id
    ) {
        String userId = principal.getClaimAsString("sub");
        return carService.reserveCar(userId, id);
    }


}
