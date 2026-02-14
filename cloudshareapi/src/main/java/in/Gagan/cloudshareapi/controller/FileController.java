package in.Gagan.cloudshareapi.controller;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import in.Gagan.cloudshareapi.document.FileMetadataDocument;
import in.Gagan.cloudshareapi.repository.FileMetadataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
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

            for (MultipartFile file : files) {

                String fileId = UUID.randomUUID().toString();

                Map uploadResult = cloudinary.uploader().upload(
                        file.getBytes(),
                        ObjectUtils.asMap("folder", "cloudshare")
                );

                String fileUrl = uploadResult.get("secure_url").toString();
                String publicId = uploadResult.get("public_id").toString();

                FileMetadataDocument metadata =
                        FileMetadataDocument.builder()
                                .id(fileId)
                                .name(file.getOriginalFilename())
                                .type(file.getContentType())
                                .size(file.getSize())
                                .clerkId(clerkId)
                                .publicStatus(false)
                                .fileLocation(fileUrl)
                                .cloudinaryPublicId(publicId)
                                .uploadedAt(LocalDateTime.now())
                                .build();

                fileRepository.save(metadata);
            }

            return ResponseEntity.ok("Uploaded successfully");

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Upload failed: " + e.getMessage());
        }
    }

    @GetMapping("/my")
    public ResponseEntity<List<FileMetadataDocument>> getMyFiles(Authentication auth) {
        return ResponseEntity.ok(
                fileRepository.findByClerkIdOrderByUploadedAtDesc(auth.getName())
        );
    }

    @PatchMapping("/{id}/toggle-public")
    public ResponseEntity<Void> togglePublic(
            @PathVariable String id,
            Authentication authentication
    ) {

        FileMetadataDocument file =
                fileRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException("File not found"));

        if (!file.getClerkId().equals(authentication.getName())) {
            return ResponseEntity.status(403).build();
        }

        file.setPublic(!file.isPublic());
        fileRepository.save(file);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable String id,
            Authentication authentication
    ) {

        FileMetadataDocument file =
                fileRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException("File not found"));

        if (!file.getClerkId().equals(authentication.getName())) {
            return ResponseEntity.status(403).build();
        }

        try {
            cloudinary.uploader().destroy(
                    file.getCloudinaryPublicId(),
                    ObjectUtils.emptyMap()
            );
        } catch (Exception ignored) {}

        fileRepository.deleteById(id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<?> download(
            @PathVariable String id,
            Authentication authentication
    ) {

        FileMetadataDocument file =
                fileRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException("File not found"));

        if (!file.getClerkId().equals(authentication.getName()) && !file.isPublic()) {
            return ResponseEntity.status(403).build();
        }

        return ResponseEntity
                .status(302)
                .header("Location", file.getFileLocation())
                .build();
    }

    @GetMapping("/public/{id}")
    public ResponseEntity<FileMetadataDocument> getPublicFile(
            @PathVariable String id
    ) {

        FileMetadataDocument file =
                fileRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException("File not found"));

        if (!file.isPublic()) {
            return ResponseEntity.status(403).build();
        }

        return ResponseEntity.ok(file);
    }
}
