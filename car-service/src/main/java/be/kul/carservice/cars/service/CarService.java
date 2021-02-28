package be.kul.carservice.cars.service;

import be.kul.carservice.cars.entity.Car;
import be.kul.carservice.cars.repository.CarRepository;
import be.kul.carservice.utils.exceptions.AlreadyExistsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.List;
import java.util.Set;

@Service
public class CarService {
    Logger logger = LoggerFactory.getLogger(CarService.class);

    @Autowired
    private CarRepository carRepository;

    public Car registerCar(Car car) {
        //First check if the car doesn't already exist
        if (carRepository.existsByNumberPlate(car.getNumberPlate())) {
            logger.error("Car with numberplate '" + car.getNumberPlate() + "' already exists");
            throw new AlreadyExistsException(
                    "Car with numberplate '" + car.getNumberPlate() + "' already exists"
            );
        }

        logger.info("Car with numberplate '" +car.getNumberPlate() + "' created");
        return carRepository.save(car);
    }

    public List<Car> findAllCarsWithinRadius(Double longitude, double latitude, double radiusInKM) {
        return carRepository.findAllCarsWithinRadius(longitude, latitude, radiusInKM);
    }

    public List<Car> registerCars(List<Car> carList) {
        //First check if any of the cars don't already exist
        for(Car car: carList) {
            if (carRepository.existsByNumberPlate(car.getNumberPlate())) {
                logger.error("Car with numberplate '" + car.getNumberPlate() + "' already exists");
                throw new AlreadyExistsException(
                        "Car with numberplate '" + car.getNumberPlate() + "' already exists"
                );
            }
        }

        return carRepository.saveAll(carList);
    }
}
