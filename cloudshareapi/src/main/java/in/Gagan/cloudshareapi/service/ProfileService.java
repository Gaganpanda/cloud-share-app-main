package in.Gagan.cloudshareapi.service;

import in.Gagan.cloudshareapi.document.ProfileDocument;
import in.Gagan.cloudshareapi.dto.ProfileDTO;
import in.Gagan.cloudshareapi.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final ProfileRepository profileRepository;

    // Get current authenticated user's profile (auto-create if not exists)
    public ProfileDocument getCurrentProfile() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            throw new UsernameNotFoundException("User not authenticated");
        }

        String clerkId = auth.getName();

        return profileRepository.findByClerkId(clerkId)
                .orElseGet(() -> profileRepository.save(
                        ProfileDocument.builder()
                                .clerkId(clerkId)
                                .createdAt(Instant.now())
                                .build()
                ));
    }

    // Create or update profile (used by Clerk webhook + /me endpoint)
    public ProfileDTO createOrUpdate(ProfileDTO dto) {

        ProfileDocument profile = profileRepository
                .findByClerkId(dto.getClerkId())
                .orElse(ProfileDocument.builder()
                        .clerkId(dto.getClerkId())
                        .createdAt(Instant.now())
                        .build()
                );

        if (dto.getEmail() != null) profile.setEmail(dto.getEmail());
        if (dto.getFirstName() != null) profile.setFirstName(dto.getFirstName());
        if (dto.getLastName() != null) profile.setLastName(dto.getLastName());
        if (dto.getPhotoUrl() != null) profile.setPhotoUrl(dto.getPhotoUrl());

        profile = profileRepository.save(profile);

        return ProfileDTO.builder()
                .id(profile.getId())
                .clerkId(profile.getClerkId())
                .email(profile.getEmail())
                .firstName(profile.getFirstName())
                .lastName(profile.getLastName())
                .photoUrl(profile.getPhotoUrl())
                .createdAt(profile.getCreatedAt())
                .build();
    }

    // Delete profile (used by Clerk user.deleted webhook)
    public void deleteProfile(String clerkId) {
        profileRepository.findByClerkId(clerkId)
                .ifPresent(profileRepository::delete);
    }
}
