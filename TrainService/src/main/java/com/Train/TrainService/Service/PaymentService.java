package com.Train.TrainService.Service;

import com.Train.TrainService.Exceptions.BuissnessValidationException;
import com.stripe.Stripe;
import com.stripe.model.Charge;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentService {

    @Value("${payment.razorpay.key-id}")
    private String razorpayKeyId;

    @Value("${payment.razorpay.key-secret}")
    private String razorpayKeySecret;

    @Value("${payment.stripe.api-key}")
    private String stripeApiKey;

    public String processPayment(String method, String token, BigDecimal amount) {
        return switch (method) {
            case "RAZORPAY" -> processRazorpayPayment(token, amount);
            case "STRIPE" -> processStripePayment(token, amount);
            default -> throw new BuissnessValidationException("Unsupported payment method: " + method);
        };
    }

    private String processRazorpayPayment(String paymentToken, BigDecimal amount) {
        try {
            RazorpayClient razorpay = new RazorpayClient(razorpayKeyId, razorpayKeySecret);
            JSONObject options = new JSONObject();
            options.put("amount", amount.multiply(BigDecimal.valueOf(100)).intValue());
            options.put("currency", "INR");

            Payment payment = razorpay.Payment.capture(paymentToken, options);
            if ("captured".equals(payment.get("status"))) {
                return payment.get("id");
            }
            throw new BuissnessValidationException("Razorpay payment failed");
        } catch (Exception e) {
            log.error("Razorpay payment failed", e);
            throw new BuissnessValidationException("Payment processing failed");
        }
    }

    private String processStripePayment(String paymentToken, BigDecimal amount) {
        Stripe.apiKey = stripeApiKey;
        Map<String, Object> params = Map.of(
                "amount", amount.multiply(BigDecimal.valueOf(100)).longValue(),
                "currency", "usd",
                "source", paymentToken,
                "description", "Train booking payment"
        );

        try {
            Charge charge = Charge.create(params);
            if ("succeeded".equals(charge.getStatus())) {
                return charge.getId();
            }
            throw new BuissnessValidationException("Stripe payment failed");
        } catch (Exception e) {
            log.error("Stripe payment failed", e);
            throw new BuissnessValidationException("Payment processing failed");
        }
    }
}

