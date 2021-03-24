package be.kul.rideservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RideController {
    @Autowired
    private RideService rideService;


}
