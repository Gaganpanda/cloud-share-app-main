package in.Gagan.cloudshareapi.repository;

import in.Gagan.cloudshareapi.document.ProfileDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ProfileRepository extends MongoRepository<ProfileDocument, String> {

    Optional<ProfileDocument> findByClerkId(String clerkId);

    Optional<ProfileDocument> findByEmail(String email);

    boolean existsByClerkId(String clerkId);
}
