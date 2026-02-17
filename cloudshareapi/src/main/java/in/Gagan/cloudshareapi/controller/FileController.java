package in.Gagan.cloudshareapi.controller;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import in.Gagan.cloudshareapi.document.FileMetadataDocument;
import in.Gagan.cloudshareapi.repository.FileMetadataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
// ✅ NO @CrossOrigin - handled globally by SecurityConfig
public class FileController {

    private final FileMetadataRepository fileRepository;
    private final Cloudinary cloudinary;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFiles(
            @RequestParam("files") List<MultipartFile> files,
            Authentication authentication
    ) {
        try {
            String clerkId = authentication.getName();
            log.info("Uploading {} files for user: {}", files.size(), clerkId);

            for (MultipartFile file : files) {
                String fileId = UUID.randomUUID().toString();

                Map uploadResult = cloudinary.uploader().upload(
                        file.getBytes(),
                        ObjectUtils.asMap(
                                "folder", "cloudshare",
                                "resource_type", "auto"
                        )
                );

                FileMetadataDocument metadata = FileMetadataDocument.builder()
                        .id(fileId)
                        .name(file.getOriginalFilename())
                        .type(file.getContentType())
                        .size(file.getSize())
                        .clerkId(clerkId)
                        .publicStatus(false)
                        .fileLocation(uploadResult.get("secure_url").toString())
                        .cloudinaryPublicId(uploadResult.get("public_id").toString())
                        .uploadedAt(LocalDateTime.now())
                        .build();

                fileRepository.save(metadata);
                log.info("✅ File saved: {}", fileId);
            }

            return ResponseEntity.ok(Map.of("message", "Uploaded successfully"));

        } catch (Exception e) {
            log.error("Upload failed", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Upload failed: " + e.getMessage()));
        }
    }

    @GetMapping("/my")
    public ResponseEntity<?> getMyFiles(Authentication auth) {
        try {
            String clerkId = auth.getName();
            List<FileMetadataDocument> files =
                    fileRepository.findByClerkIdOrderByUploadedAtDesc(clerkId);
            return ResponseEntity.ok(files);
        } catch (Exception e) {
            log.error("Failed to fetch files", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Failed to fetch files"));
        }
    }

    @PatchMapping("/{id}/toggle-public")
    public ResponseEntity<?> togglePublic(
            @PathVariable String id,
            Authentication authentication
    ) {
        try {
            FileMetadataDocument file = fileRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("File not found"));

            if (!file.getClerkId().equals(authentication.getName())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Unauthorized"));
            }

            file.setPublic(!file.isPublic());
            fileRepository.save(file);

            return ResponseEntity.ok(Map.of(
                    "message", "Updated successfully",
                    "publicStatus", file.isPublic()
            ));
        } catch (Exception e) {
            log.error("Toggle failed", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(
            @PathVariable String id,
            Authentication authentication
    ) {
        try {
            FileMetadataDocument file = fileRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("File not found"));

            if (!file.getClerkId().equals(authentication.getName())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Unauthorized"));
            }

            try {
                cloudinary.uploader().destroy(
                        file.getCloudinaryPublicId(),
                        ObjectUtils.asMap("resource_type", "auto")
                );
            } catch (Exception e) {
                log.warn("Cloudinary delete failed: {}", e.getMessage());
            }

            fileRepository.deleteById(id);
            return ResponseEntity.noContent().build();

        } catch (Exception e) {
            log.error("Delete failed", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<?> download(
            @PathVariable String id,
            Authentication authentication
    ) {
        try {
            log.info("Download: file={}, user={}", id, authentication.getName());

            FileMetadataDocument file = fileRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("File not found"));

            if (!file.getClerkId().equals(authentication.getName())) {
                log.warn("❌ Unauthorized - owner:{} requester:{}",
                        file.getClerkId(), authentication.getName());
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Unauthorized"));
            }

            log.info("✅ Download URL sent for: {}", id);
            return ResponseEntity.ok(Map.of(
                    "url", file.getFileLocation(),
                    "name", file.getName()
            ));

        } catch (Exception e) {
            log.error("Download failed: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Download failed: " + e.getMessage()));
        }
    }

    @GetMapping("/public/{id}")
    public ResponseEntity<?> getPublicFile(@PathVariable String id) {
        try {
            FileMetadataDocument file = fileRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("File not found"));

            if (!file.isPublic()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "This file is not public"));
            }

            return ResponseEntity.ok(file);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/public/download/{id}")
    public ResponseEntity<?> publicDownload(@PathVariable String id) {
        try {
            FileMetadataDocument file = fileRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("File not found"));

            if (!file.isPublic()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "This file is not public"));
            }

            return ResponseEntity.ok(Map.of(
                    "url", file.getFileLocation(),
                    "name", file.getName()
            ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Download failed: " + e.getMessage()));
        }
    }
}
