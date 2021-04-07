package be.kul.rideservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@org.springframework.web.bind.annotation.RestController
public class RestController {
    @Autowired
    private RideService rideService;


}
