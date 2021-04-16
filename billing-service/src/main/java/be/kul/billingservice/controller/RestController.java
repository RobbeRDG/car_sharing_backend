package be.kul.billingservice.controller;

import be.kul.billingservice.service.BillingService;
import be.kul.billingservice.utils.json.jsonViews.Views;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.util.List;

@org.springframework.web.bind.annotation.RestController
@RequestMapping(path="/billing-service")
public class RestController {
    @Autowired
    private BillingService billingService;

    @PutMapping("/users/payment-method")
    public @ResponseBody
    ResponseEntity<String> configureNewPaymentMethod(
            @AuthenticationPrincipal Jwt principal
    ){
        String userId = principal.getClaimAsString("sub");
        return billingService.configureNewPaymentMethod(userId);
    }
}
