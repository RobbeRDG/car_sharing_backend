package be.kul.carservice.cars.controller;

import be.kul.carservice.cars.entity.Car;
import be.kul.carservice.cars.service.CarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;
import java.util.UUID;

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
    List<Car> getAllCarsWithinRadius(@RequestParam double longitude, @RequestParam double latitude, @RequestParam double radiusInKM) {
        return carService.findAllCarsWithinRadius(longitude, latitude, radiusInKM);
    }

}
