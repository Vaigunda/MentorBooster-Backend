package com.example.flutter.controller;

import com.example.flutter.request.PaymentRequest;
import com.example.flutter.response.PaymentResponse;
import com.stripe.Stripe;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    @Value("${stripe.apikey}")
    private String stripeApiKey;

    // Initialize Stripe API key after loading the class
    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeApiKey;
    }

    @PostMapping("/createPaymentIntent")
    public PaymentResponse createPaymentIntent(@RequestBody PaymentRequest request) throws Exception {
        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(request.getAmount())  // Amount in cents
                .setCurrency(request.getCurrency())  // "cad" for Canada
                .addPaymentMethodType("card")
                .build();

        PaymentIntent paymentIntent = PaymentIntent.create(params);

        return new PaymentResponse(
                paymentIntent.getId(),
                paymentIntent.getStatus(),
                paymentIntent.getClientSecret()
        );
    }


}