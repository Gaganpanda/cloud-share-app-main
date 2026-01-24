package in.Gagan.cloudshareapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentVerificationDTO {

    private String orderId; // PayPal order ID
    private String captureId; // PayPal capture/transaction ID (optional, depending on your flow)
    private String planId;
}
