package in.Gagan.cloudshareapi.document;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "files")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileMetadataDocument {

    @Id
    private String id;

    private String name;
    private String type;
    private Long size;

    @Indexed
    private String clerkId;

    private boolean publicStatus;

    private String fileLocation; // Cloudinary URL

    private String cloudinaryPublicId;

    private LocalDateTime uploadedAt;

    public boolean isPublic() {
        return publicStatus;
    }

    public void setPublic(boolean publicStatus) {
        this.publicStatus = publicStatus;
    }
}
