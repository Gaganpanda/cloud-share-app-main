package in.Gagan.cloudshareapi.service;

import in.Gagan.cloudshareapi.document.FileMetadataDocument;
import in.Gagan.cloudshareapi.repository.FileMetadataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FileMetadataService {

    private final FileMetadataRepository repository;

    public List<FileMetadataDocument> getFilesByUser(String clerkId) {
        return repository.findByClerkIdOrderByUploadedAtDesc(clerkId);
    }

    public void togglePublic(String fileId) {
        FileMetadataDocument file = repository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("File not found"));

        file.setPublic(!file.isPublic());
        repository.save(file);
    }

    public FileMetadataDocument getFile(String fileId) {
        return repository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("File not found"));
    }

    public void deleteFile(String fileId) {
        repository.deleteById(fileId);
    }
}
