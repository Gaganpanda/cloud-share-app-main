package in.Gagan.cloudshareapi.controller;

import in.Gagan.cloudshareapi.dto.ProfileDTO;
import in.Gagan.cloudshareapi.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/profiles")
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping("/me")
    public ResponseEntity<ProfileDTO> getMyProfile() {
        String clerkId = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        ProfileDTO profile = profileService.createOrUpdate(
                ProfileDTO.builder().clerkId(clerkId).build()
        );

        return ResponseEntity.ok(profile);
    }
}
