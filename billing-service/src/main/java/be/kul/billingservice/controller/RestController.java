package be.kul.billingservice.controller;

import be.kul.billingservice.entity.Bill;
import be.kul.billingservice.service.BillingService;
import be.kul.billingservice.utils.json.jsonViews.Views;
import be.kul.billingservice.utils.json.rest.ClientSecret;
import be.kul.billingservice.utils.json.rest.PaymentMethodConfirmation;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.stripe.exception.StripeException;
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

    @GetMapping("/user-payment-method/setup")
    public @ResponseBody
    ClientSecret initialiseNewUserPaymentMethod(
            @AuthenticationPrincipal Jwt principal
    ) throws StripeException {
        String userId = principal.getClaimAsString("sub");
        return billingService.initialiseNewUserPaymentMethod(userId);
    }

    @GetMapping("/user-payment-method/check")
    public @ResponseBody
    PaymentMethodConfirmation checkUserPaymentMethod(
            @AuthenticationPrincipal Jwt principal
    ) {
        String userId = principal.getClaimAsString("sub");
        return billingService.checkUserPaymentMethod(userId);
    }

    @GetMapping("/bills/{billId}")
    @JsonView(Views.BillView.Basic.class)
    public @ResponseBody
    Bill getBill(
            @AuthenticationPrincipal Jwt principal,
            @PathVariable long billId
    ) {
        String userId = principal.getClaimAsString("sub");
        return billingService.getBill(userId, billId);
    }

    @GetMapping("/bills/detail/{billId}")
    @JsonView(Views.BillView.Detail.class)
    public @ResponseBody
    Bill getBillDetail(
            @AuthenticationPrincipal Jwt principal,
            @PathVariable long billId
    ) {
        String userId = principal.getClaimAsString("sub");
        return billingService.getBill(userId, billId);
    }
}
