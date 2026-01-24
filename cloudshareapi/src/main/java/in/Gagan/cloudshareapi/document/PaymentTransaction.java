package in.Gagan.cloudshareapi.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "payment_transactions")
public class PaymentTransaction {

    private String id;
    private String clerkId;

    // PayPal’s unique order ID for the transaction
    private String orderId;

    // PayPal’s capture ID or transaction ID
    private String captureId; // was paymentId

    private String planId;

    // Amount as a decimal for PayPal support (could be double, BigDecimal is more
    // precise)
    private BigDecimal amount;

    private String currency;
    private int creditsAdded;
    private String status;
    private LocalDateTime transactionDate;

    private String userEmail;
    private String userName;

    // Optional: add payerId if you want to record PayPal's user reference
    // private String payerId;
}
