package in.Gagan.cloudshareapi.controller;

import in.Gagan.cloudshareapi.document.FileMetadataDocument;
import in.Gagan.cloudshareapi.repository.FileMetadataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
public class FileController {

    private final FileMetadataRepository fileRepository;

    // ======================================================
    // ✅ UPLOAD FILES
    // ======================================================
    @PostMapping("/upload")
    public ResponseEntity<?> uploadFiles(
            @RequestParam("files") List<MultipartFile> files,
            Authentication authentication
    ) {

        try {

            String clerkId = authentication.getName();

            String uploadDir =
                    System.getProperty("user.dir") + File.separator + "uploads";

            File directory = new File(uploadDir);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            for (MultipartFile file : files) {

                String fileId = UUID.randomUUID().toString();
                String fileName = fileId + "_" + file.getOriginalFilename();

                Path filePath = Paths.get(uploadDir, fileName);

                file.transferTo(filePath.toFile());

                FileMetadataDocument metadata =
                        FileMetadataDocument.builder()
                                .id(fileId)
                                .name(file.getOriginalFilename())
                                .type(file.getContentType())
                                .size(file.getSize())
                                .clerkId(clerkId)
                                .publicStatus(false)   // ✅ CLEAN FIX
                                .fileLocation(filePath.toString())
                                .uploadedAt(LocalDateTime.now())
                                .build();

                fileRepository.save(metadata);
            }

            return ResponseEntity.ok("Files uploaded successfully");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body("Upload failed: " + e.getMessage());
        }
    }

    // ======================================================
    // ✅ FETCH MY FILES
    // ======================================================
    @GetMapping("/my")
    public ResponseEntity<List<FileMetadataDocument>> getMyFiles(
            Authentication auth
    ) {
        return ResponseEntity.ok(
                fileRepository.findByClerkIdOrderByUploadedAtDesc(auth.getName())
        );
    }

    // ======================================================
    // ✅ TOGGLE PUBLIC
    // ======================================================
    @PatchMapping("/{id}/toggle-public")
    public ResponseEntity<Void> togglePublic(@PathVariable String id) {

        FileMetadataDocument file =
                fileRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException("File not found"));

        file.setPublic(!file.isPublic());  // ✅ works clean

        fileRepository.save(file);

        return ResponseEntity.ok().build();
    }

    // ======================================================
    // ✅ DOWNLOAD FILE
    // ======================================================
    @GetMapping("/download/{id}")
    public ResponseEntity<byte[]> download(@PathVariable String id) {

        try {

            FileMetadataDocument file =
                    fileRepository.findById(id)
                            .orElseThrow(() ->
                                    new RuntimeException("File not found"));

            Path path = Paths.get(file.getFileLocation());
            byte[] fileBytes = java.nio.file.Files.readAllBytes(path);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + file.getName() + "\"")
                    .body(fileBytes);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    // ======================================================
    // ✅ DELETE FILE
    // ======================================================
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {

        FileMetadataDocument file =
                fileRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException("File not found"));

        try {
            java.nio.file.Files.deleteIfExists(
                    Paths.get(file.getFileLocation()));
        } catch (Exception ignored) {}

        fileRepository.deleteById(id);

        return ResponseEntity.noContent().build();
    }

    // ======================================================
    // ✅ PUBLIC FILE VIEW
    // ======================================================
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
