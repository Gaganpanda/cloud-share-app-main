package in.Gagan.cloudshareapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentDTO {
    private String planId;
    private BigDecimal amount; // for decimal currency support, can use Double too
    private String currency;
    private Integer credits;
    private Boolean success;
    private String message;
    private String orderId; // PayPal order id
    private String captureId; // PayPal capture id (optional, for payment confirmation)
}
