package in.Gagan.cloudshareapi.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api/v1.0/paypal")
@CrossOrigin(origins = "http://localhost:5173")
public class PaypalConfigController {
    @Value("${paypal.client.id}")
    private String clientId;

    @GetMapping("/client-id")
    public Map<String, String> getClientId() {
        return Collections.singletonMap("clientId", clientId);
    }
}
