package in.Gagan.cloudshareapi.document;

import lombok.*;
import org.springframework.data.annotation.Id;
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
    private String clerkId;

    // ✅ Clean field name (NO "is" prefix)
    private boolean publicStatus;

    private String fileLocation;
    private LocalDateTime uploadedAt;

    // ===============================
    // Helper methods for frontend
    // ===============================

    public boolean isPublic() {
        return publicStatus;
    }

    public void setPublic(boolean publicStatus) {
        this.publicStatus = publicStatus;
    }
}
