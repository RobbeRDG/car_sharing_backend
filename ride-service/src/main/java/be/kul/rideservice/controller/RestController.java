package be.kul.rideservice.controller;

import be.kul.rideservice.entity.Ride;
import be.kul.rideservice.service.RideService;
import be.kul.rideservice.utils.exceptions.DoesntExistException;
import be.kul.rideservice.utils.json.jsonViews.Views;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@org.springframework.web.bind.annotation.RestController
@RequestMapping(path="/ride-service")
public class RestController {
    @Autowired
    private RideService rideService;

    @GetMapping("/admin/rides")
    public @ResponseBody
    @JsonView(Views.RideView.Basic.class )
    List<Ride> adminGetAllRidesWithinTimeFrame(
            @RequestParam("startTime") @DateTimeFormat(pattern = "dd.MM.yyyy") LocalDate startTime,
            @RequestParam("stopTime") @DateTimeFormat(pattern = "dd.MM.yyyy") LocalDate stopTime
    ) {
        RideService.logger.info("test");
        return rideService.adminGetAllRidesWithinTimeFrame(startTime, stopTime);
    }

    @GetMapping("/admin/rides/{rideId}")
    public @ResponseBody
    @JsonView(Views.RideView.Full.class )
    Ride adminGetRide(@PathVariable long rideId) {
        return rideService.adminGetRide(rideId);
    }

    @GetMapping("/rides/{rideId}")
    public @ResponseBody
    @JsonView(Views.RideView.Full.class )
    Ride getRide(@PathVariable long rideId, @AuthenticationPrincipal Jwt principal ) {
        String userId = principal.getClaimAsString("sub");
        return rideService.getRide(userId, rideId);
    }

    @GetMapping("/rides")
    public @ResponseBody
    @JsonView(Views.RideView.Basic.class )
    List<Ride> getAllRidesWithinTimeFrame(
            @RequestParam("startTime") @DateTimeFormat(pattern = "dd.MM.yyyy") LocalDate startTime,
            @RequestParam("stopTime") @DateTimeFormat(pattern = "dd.MM.yyyy") LocalDate stopTime,
            @AuthenticationPrincipal Jwt principal
    ){
        String userId = principal.getClaimAsString("sub");
        return rideService.getAllRidesWithinTimeFrame(userId, startTime, stopTime);
    }

    @PostMapping("/rides/{rideId}/damage-report")
    public @ResponseBody
    ResponseEntity<String> uploadDamageReport(
            @PathVariable long rideId,
            @RequestParam("imageFile") MultipartFile imageFile,
            @RequestParam("reportDescription") String description,
            @AuthenticationPrincipal Jwt principal
    ) throws IOException {
        if (imageFile.isEmpty()) throw new DoesntExistException("Couldn't upload damage report: no image file has been provided");
        String userId = principal.getClaimAsString("sub");
        return rideService.uploadDamageReport(userId, rideId, imageFile, description);
    }


}
