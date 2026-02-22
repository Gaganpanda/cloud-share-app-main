package in.Gagan.cloudshareapi.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import in.Gagan.cloudshareapi.dto.ProfileDTO;
import in.Gagan.cloudshareapi.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/webhooks/clerk")
@RequiredArgsConstructor
public class ClerkWebhookController {

    private final ProfileService profileService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping
    public ResponseEntity<Void> handleClerkWebhook(@RequestBody String payload) {

        try {
            JsonNode root = objectMapper.readTree(payload);

            String eventType = root.path("type").asText();
            JsonNode data = root.path("data");

            if (data.isMissingNode()) {
                return ResponseEntity.badRequest().build();
            }

            String clerkId = data.path("id").asText(null);

            String email = null;
            if (data.path("email_addresses").isArray()
                    && data.path("email_addresses").size() > 0) {
                email = data.path("email_addresses")
                        .get(0)
                        .path("email_address")
                        .asText(null);
            }

            ProfileDTO profileDTO = ProfileDTO.builder()
                    .clerkId(clerkId)
                    .email(email)
                    .firstName(data.path("first_name").asText(null))
                    .lastName(data.path("last_name").asText(null))
                    .photoUrl(data.path("image_url").asText(null))
                    .build();

            switch (eventType) {
                case "user.created":
                case "user.updated":
                    profileService.createOrUpdate(profileDTO);
                    break;

                case "user.deleted":
                    profileService.deleteProfile(clerkId);
                    break;

                default:
                    // Ignore unhandled events
                    break;
            }

            return ResponseEntity.ok().build();

        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}