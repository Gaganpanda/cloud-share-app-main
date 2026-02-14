package in.Gagan.cloudshareapi.repository;

import in.Gagan.cloudshareapi.document.FileMetadataDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface FileMetadataRepository
        extends MongoRepository<FileMetadataDocument, String> {

    List<FileMetadataDocument> findByClerkIdOrderByUploadedAtDesc(String clerkId);
}
