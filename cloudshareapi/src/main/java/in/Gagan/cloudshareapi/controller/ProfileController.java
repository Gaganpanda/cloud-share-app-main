package in.Gagan.cloudshareapi.controller;

import in.Gagan.cloudshareapi.dto.ProfileDTO;
import in.Gagan.cloudshareapi.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @PostMapping("/register")
    public ResponseEntity<ProfileDTO> registerProfile(@RequestBody ProfileDTO profileDTO) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // 🔐 Always trust Clerk JWT, never request body
        String clerkId = auth.getName();
        profileDTO.setClerkId(clerkId);

        ProfileDTO savedProfile = profileService.createProfile(profileDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedProfile);
    }
}
