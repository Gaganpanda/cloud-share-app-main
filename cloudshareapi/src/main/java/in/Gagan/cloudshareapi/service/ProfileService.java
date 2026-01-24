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
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final ProfileRepository profileRepository;

    // ================= CURRENT USER =================

    public ProfileDocument getCurrentProfile() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            throw new UsernameNotFoundException("User not authenticated");
        }

        String clerkId = auth.getName();

        return profileRepository.findByClerkId(clerkId)
                .orElseGet(() -> {
                    ProfileDocument profile = ProfileDocument.builder()
                            .clerkId(clerkId)
                            .credits(5)
                            .createdAt(Instant.now())
                            .build();
                    return profileRepository.save(profile);
                });
    }

    // ================= CREATE =================

    public ProfileDTO createProfile(ProfileDTO dto) {

        ProfileDocument profile = profileRepository
                .findByClerkId(dto.getClerkId())
                .orElse(ProfileDocument.builder()
                        .clerkId(dto.getClerkId())
                        .credits(5)
                        .createdAt(Instant.now())
                        .build()
                );

        applyDto(profile, dto);

        profile = profileRepository.save(profile);
        return mapToDTO(profile);
    }

    // ================= UPDATE (USED BY WEBHOOK) =================

    public ProfileDTO updateProfile(ProfileDTO dto) {

        Optional<ProfileDocument> optionalProfile =
                profileRepository.findByClerkId(dto.getClerkId());

        if (optionalProfile.isEmpty()) {
            return null;
        }

        ProfileDocument profile = optionalProfile.get();
        applyDto(profile, dto);

        profile = profileRepository.save(profile);
        return mapToDTO(profile);
    }

    // ================= DELETE (USED BY WEBHOOK) =================

    public void deleteProfile(String clerkId) {
        profileRepository.findByClerkId(clerkId)
                .ifPresent(profileRepository::delete);
    }

    // ================= HELPERS =================

    private void applyDto(ProfileDocument profile, ProfileDTO dto) {
        if (dto.getEmail() != null && !dto.getEmail().isBlank()) {
            profile.setEmail(dto.getEmail());
        }
        if (dto.getFirstName() != null) {
            profile.setFirstName(dto.getFirstName());
        }
        if (dto.getLastName() != null) {
            profile.setLastName(dto.getLastName());
        }
        if (dto.getPhotoUrl() != null) {
            profile.setPhotoUrl(dto.getPhotoUrl());
        }
    }

    private ProfileDTO mapToDTO(ProfileDocument p) {
        return ProfileDTO.builder()
                .id(p.getId())
                .clerkId(p.getClerkId())
                .email(p.getEmail())
                .firstName(p.getFirstName())
                .lastName(p.getLastName())
                .photoUrl(p.getPhotoUrl())
                .credits(p.getCredits())
                .createdAt(p.getCreatedAt())
                .build();
    }
}
