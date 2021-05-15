package be.kul.billingservice.controller;

import be.kul.billingservice.entity.Bill;
import be.kul.billingservice.service.BillingService;
import be.kul.billingservice.utils.json.jsonObjects.rest.ClientSecret;
import be.kul.billingservice.utils.json.jsonObjects.rest.PaymentMethodConfirmation;
import be.kul.billingservice.utils.json.jsonObjects.rest.UserPaymentCardInformation;
import be.kul.billingservice.utils.json.jsonViews.Views;
import com.fasterxml.jackson.annotation.JsonView;
import com.stripe.exception.StripeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

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

    @GetMapping("/user-payment-method/current")
    public @ResponseBody
    UserPaymentCardInformation getCurrentUserPaymentMethod(
            @AuthenticationPrincipal Jwt principal
    ) throws StripeException {
        String userId = principal.getClaimAsString("sub");
        return billingService.getCurrentUserPaymentCard(userId);
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

    @GetMapping("/bills/ride/{rideId}")
    @JsonView(Views.BillView.Basic.class)
    public @ResponseBody
    Bill getBillFromRide(
            @AuthenticationPrincipal Jwt principal,
            @PathVariable long rideId
    ) {
        String userId = principal.getClaimAsString("sub");
        return billingService.getBillFromRide(userId, rideId);
    }

    @GetMapping("/bills/ride/detail/{rideId}")
    @JsonView(Views.BillView.Detail.class)
    public @ResponseBody
    Bill getBillDetailFromRide(
            @AuthenticationPrincipal Jwt principal,
            @PathVariable long rideId
    ) {
        String userId = principal.getClaimAsString("sub");
        return billingService.getBillFromRide(userId, rideId);
    }
}
