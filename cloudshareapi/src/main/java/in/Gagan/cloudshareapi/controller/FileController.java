package in.Gagan.cloudshareapi.controller;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import in.Gagan.cloudshareapi.document.FileMetadataDocument;
import in.Gagan.cloudshareapi.repository.FileRepository;
import in.Gagan.cloudshareapi.service.FileMetadataService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
public class FileController {

    private final FileMetadataService service;

    @PostMapping("/upload")
    public ResponseEntity<FileMetadataDocument> upload(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal String clerkId
    ) throws Exception {

        return ResponseEntity.ok(
                service.uploadFile(file, clerkId)
        );
    }

    @GetMapping("/my")
    public ResponseEntity<List<FileMetadataDocument>> myFiles(
            @AuthenticationPrincipal String clerkId
    ) {
        return ResponseEntity.ok(
                service.getFilesByUser(clerkId)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<String> download(
            @PathVariable String id,
            @AuthenticationPrincipal String clerkId
    ) {

        FileMetadataDocument file = service.getFile(id);

        if (!file.getClerkId().equals(clerkId)) {
            return ResponseEntity.status(403).build();
        }

        return ResponseEntity.ok(file.getSecureUrl());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(
            @PathVariable String id,
            @AuthenticationPrincipal String clerkId
    ) throws Exception {

        FileMetadataDocument file = service.getFile(id);

        if (!file.getClerkId().equals(clerkId)) {
            return ResponseEntity.status(403).build();
        }

        service.deleteFile(id);

        return ResponseEntity.ok("Deleted successfully");
    }
}
