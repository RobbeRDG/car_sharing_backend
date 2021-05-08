package be.kul.carservice.controller;

import be.kul.carservice.entity.Car;
import be.kul.carservice.service.CarService;
import be.kul.carservice.utils.json.jsonObjects.amqpMessages.car.CarStatusUpdate;
import be.kul.carservice.utils.json.jsonObjects.rest.EndRideConfirmation;
import be.kul.carservice.utils.json.jsonViews.Views;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@org.springframework.web.bind.annotation.RestController
@RequestMapping(path="/car-service")
public class RestController {
    @Autowired
    private CarService carService;

    @PostMapping("/admin/cars")
    public @ResponseBody Car registerCar(@RequestBody Car car) {
        return carService.registerCar(car);
    }

    @PostMapping("/admin/cars/batch")
    public @ResponseBody List<Car> registerCar(@RequestBody List<Car> carList) {
        return carService.registerCars(carList);
    }

    @GetMapping("/admin/cars")
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

    @PutMapping("/admin/cars/status/{carId}")
    @JsonView(Views.CarView.Full.class)
    public @ResponseBody Car updateCarStatus(
            @RequestBody CarStatusUpdate carStatusUpdate,
            @PathVariable long carId
    ) throws JsonProcessingException {
        carStatusUpdate.setCarId(carId);
        return carService.updateCarStatus(carStatusUpdate);
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

    @PutMapping("/admin/cars/active/{carId}")
    @JsonView(Views.CarView.Full.class)
    public @ResponseBody Car setCarToActive(
            @PathVariable long carId
    ) {
        return carService.setCarToActive(carId);
    }

    @PutMapping("/admin/cars/deactivate/{carId}")
    @JsonView(Views.CarView.Full.class)
    public @ResponseBody Car setCarToInactive(
            @PathVariable long carId
    ) {
        return carService.setCarToInactive(carId);
    }

    @PutMapping("/cars/start-ride/{carId}")
    @JsonView(Views.CarView.Ride.class)
    public @ResponseBody Car startRide(
            @AuthenticationPrincipal Jwt principal,
            @PathVariable long carId
    ) throws Exception {
        String userId = principal.getClaimAsString("sub");
        return carService.startRide(userId, carId);
    }

    @PutMapping("/cars/end-ride/{carId}")
    public @ResponseBody
    EndRideConfirmation endRide(
            @AuthenticationPrincipal Jwt principal,
            @PathVariable long carId
    ) throws Exception {
        String userId = principal.getClaimAsString("sub");
        return carService.endRide(userId, carId);
    }

    @PutMapping("/cars/lock/{carId}")
    @JsonView(Views.CarView.Ride.class)
    public @ResponseBody
    Car lockCar(
            @AuthenticationPrincipal Jwt principal,
            @PathVariable long carId
    ) throws Exception {
        String userId = principal.getClaimAsString("sub");
        return carService.lockCar(userId, carId, true);
    }

    @PutMapping("/cars/unlock/{carId}")
    @JsonView(Views.CarView.Ride.class)
    public @ResponseBody
    Car unlockCar(
            @AuthenticationPrincipal Jwt principal,
            @PathVariable long carId
    ) throws Exception {
        String userId = principal.getClaimAsString("sub");
        return carService.lockCar(userId, carId, false);
    }

    @GetMapping("/cars/ride")
    @JsonView(Views.CarView.Ride.class)
    public @ResponseBody
    Car getCarBeingRiddenByUser(
            @AuthenticationPrincipal Jwt principal
    ) {
        String userId = principal.getClaimAsString("sub");
        return carService.getCarBeingRiddenByUser(userId);
    }

    @GetMapping("/cars/reservation")
    @JsonView(Views.CarView.Reserved.class)
    public @ResponseBody
    Car getCarReservedByUser(
            @AuthenticationPrincipal Jwt principal
    ) {
        String userId = principal.getClaimAsString("sub");
        return carService.getCarReservedByUser(userId);
    }
}
