package in.Gagan.cloudshareapi.document;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "files")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileMetadataDocument {

    @Id
    private String id;

    private String fileName;
    private String fileType;
    private Long fileSize;

    private String secureUrl;
    private String publicId;

    private String clerkId;

    private Boolean isPublic;

    private LocalDateTime uploadedAt;
}
