package in.Gagan.cloudshareapi.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import in.Gagan.cloudshareapi.document.FileMetadataDocument;
import in.Gagan.cloudshareapi.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FileMetadataService {

    private final FileRepository repository;
    private final Cloudinary cloudinary;

    // 🔥 Upload
    public FileMetadataDocument uploadFile(
            MultipartFile file,
            String clerkId
    ) throws Exception {

        Map uploadResult = cloudinary.uploader().upload(
                file.getBytes(),
                ObjectUtils.asMap(
                        "folder", "cloudshare/" + clerkId,
                        "resource_type", "auto"
                )
        );

        FileMetadataDocument savedFile =
                FileMetadataDocument.builder()
                        .fileName(file.getOriginalFilename())
                        .fileType(file.getContentType())
                        .fileSize(file.getSize())
                        .secureUrl(uploadResult.get("secure_url").toString())
                        .publicId(uploadResult.get("public_id").toString())
                        .clerkId(clerkId)
                        .isPublic(false)
                        .uploadedAt(LocalDateTime.now())
                        .build();

        return repository.save(savedFile);
    }

    // 🔥 Get My Files
    public List<FileMetadataDocument> getFilesByUser(String clerkId) {
        return repository.findByClerkIdOrderByUploadedAtDesc(clerkId);
    }

    // 🔥 Get Single File
    public FileMetadataDocument getFile(String fileId) {
        return repository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("File not found"));
    }

    // 🔥 Toggle Public
    public FileMetadataDocument togglePublic(String fileId) {

        FileMetadataDocument file = getFile(fileId);

        file.setIsPublic(!file.getIsPublic());

        return repository.save(file);
    }

    // 🔥 Delete File (Cloudinary + DB)
    public void deleteFile(String fileId) throws Exception {

        FileMetadataDocument file = getFile(fileId);

        cloudinary.uploader().destroy(
                file.getPublicId(),
                ObjectUtils.asMap("resource_type", "auto")
        );

        repository.deleteById(fileId);
    }
}
