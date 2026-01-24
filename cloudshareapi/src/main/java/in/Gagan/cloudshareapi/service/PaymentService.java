package in.Gagan.cloudshareapi.service;

import in.Gagan.cloudshareapi.document.PaymentTransaction;
import in.Gagan.cloudshareapi.document.ProfileDocument;
import in.Gagan.cloudshareapi.dto.PaymentDTO;
import in.Gagan.cloudshareapi.dto.PaymentVerificationDTO;
import in.Gagan.cloudshareapi.repository.PaymentTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final ProfileService profileService;
    private final UserCreditsService userCreditsService;
    private final PaymentTransactionRepository paymentTransactionRepository;

    @Value("${paypal.client.id}")
    private String paypalClientId;
    @Value("${paypal.client.secret}")
    private String paypalClientSecret;
    @Value("${paypal.mode}")
    private String paypalMode;

    public PaymentDTO createOrder(PaymentDTO paymentDTO) {
        try {
            ProfileDocument currentProfile = profileService.getCurrentProfile();
            String clerkId = currentProfile.getClerkId();

            // TODO: Implement PayPal order creation using PayPal SDK or REST API
            String orderId = "PAYPAL_ORDER_ID_FROM_API";

            // create pending transaction record
            PaymentTransaction transaction = PaymentTransaction.builder()
                    .clerkId(clerkId)
                    .orderId(orderId)
                    .planId(paymentDTO.getPlanId())
                    .amount(paymentDTO.getAmount())
                    .currency(paymentDTO.getCurrency())
                    .status("PENDING")
                    .transactionDate(LocalDateTime.now())
                    .userEmail(currentProfile.getEmail())
                    .userName(currentProfile.getFirstName() + " " + currentProfile.getLastName())
                    .build();

            paymentTransactionRepository.save(transaction);

            return PaymentDTO.builder()
                    .orderId(orderId)
                    .success(true)
                    .message("PayPal order created successfully")
                    .build();

        } catch (Exception e) {
            return PaymentDTO.builder()
                    .success(false)
                    .message("Error creating order: " + e.getMessage())
                    .build();
        }
    }

    public PaymentDTO verifyPayment(PaymentVerificationDTO request) {
        try {
            ProfileDocument currentProfile = profileService.getCurrentProfile();
            String clerkId = currentProfile.getClerkId();

            // TODO: Verify and capture payment via PayPal API using request.getOrderId()
            // and request.getCaptureId()
            boolean paymentSuccess = true; // Set by actual PayPal payment status
            String captureId = request.getCaptureId(); // Set from PayPal response

            if (!paymentSuccess) {
                updateTransactionStatus(request.getOrderId(), "FAILED", captureId, null);
                return PaymentDTO.builder()
                        .success(false)
                        .message("PayPal payment verification failed")
                        .build();
            }

            // Add credits based on plan
            int creditsToAdd = 0;
            String plan = "BASIC";

            switch (request.getPlanId()) {
                case "premium":
                    creditsToAdd = 500;
                    plan = "PREMIUM";
                    break;
                case "ultimate":
                    creditsToAdd = 5000;
                    plan = "ULTIMATE";
                    break;
            }

            if (creditsToAdd > 0) {
                userCreditsService.addCredits(clerkId, creditsToAdd, plan);
                updateTransactionStatus(request.getOrderId(), "SUCCESS", captureId, creditsToAdd);
                return PaymentDTO.builder()
                        .success(true)
                        .message("Payment verified and credits added successfully")
                        .credits(userCreditsService.getUserCredits(clerkId).getCredits())
                        .build();
            } else {
                updateTransactionStatus(request.getOrderId(), "FAILED", captureId, null);
                return PaymentDTO.builder()
                        .success(false)
                        .message("Invalid plan selected")
                        .build();
            }
        } catch (Exception e) {
            updateTransactionStatus(request.getOrderId(), "ERROR", null, null);
            return PaymentDTO.builder()
                    .success(false)
                    .message("Error verifying payment:" + e.getMessage())
                    .build();
        }
    }

    private void updateTransactionStatus(String orderId, String status, String captureId, Integer creditsToAdd) {
        paymentTransactionRepository.findAll().stream()
                .filter(t -> t.getOrderId() != null && t.getOrderId().equals(orderId))
                .findFirst()
                .map(transaction -> {
                    transaction.setStatus(status);
                    transaction.setCaptureId(captureId);// PayPal capture/transaction ID
                    if (creditsToAdd != null) {
                        transaction.setCreditsAdded(creditsToAdd);
                    }
                    return paymentTransactionRepository.save(transaction);
                })
                .orElse(null);
    }
}
