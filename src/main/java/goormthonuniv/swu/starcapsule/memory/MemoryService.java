package goormthonuniv.swu.starcapsule.memory;

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import goormthonuniv.swu.starcapsule.snowball.Snowball;
import goormthonuniv.swu.starcapsule.snowball.SnowballService;
import goormthonuniv.swu.starcapsule.user.User;
import goormthonuniv.swu.starcapsule.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.google.auth.oauth2.GoogleCredentials;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Transactional
@RequiredArgsConstructor
@Service
public class MemoryService {
    private final SnowballService snowballService;
    private final MemoryObjectShapeService memoryObjectShapeService;
    private final MemoryRepository memoryRepository;
    private final String BUCKET_NAME = "snowball-log-image";

    public Memory writeMemory(String userId, String title, String answer, String writer, MultipartFile image, String objectName) throws IOException {
        String imageUrl = null;
        if(image!=null){
            imageUrl = getPublicUrl(image);
        }
        Memory memory = new Memory(title, answer, imageUrl, writer);
        saveMemory(memory);
        MemoryObjectShape memoryObjectShape = memoryObjectShapeService.findByObjectShapeName(objectName);
        String sharedLink = "http://localhost:3000/main/"+userId;
        System.out.println();
        Snowball snowball = snowballService.findBySharedLink(sharedLink);
        memory.setMemoryObjectShape(memoryObjectShape);
        memory.setSnowball(snowball);

        return memory;
    }

    public Memory getMemory(String userId, Long memoryId) throws IOException {
        Snowball snowball = snowballService.findBySharedLink("http://localhost:3000/api/capsule/" + userId);
        List<Memory> memories = snowball.getMemories();

        for (Memory memory : memories) {
            if (memory.getId().equals(memoryId)) {
                return memory;
            }
        }

        throw new IllegalArgumentException("Memory not found with ID " + memoryId);
    }

    public Memory saveMemory(Memory memory){
        return memoryRepository.save(memory);
    }

    public String getPublicUrl(MultipartFile image) throws IOException {
        ClassPathResource resource = new ClassPathResource("snowballlog-e83fd7e609f4.json");

        InputStream credentialsStream = resource.getInputStream();
        GoogleCredentials credentials = GoogleCredentials.fromStream(credentialsStream);

        Storage storage = StorageOptions.newBuilder()
                .setCredentials(credentials)
                .build()
                .getService();

        String fileName = image.getOriginalFilename();

        BlobId blobId = BlobId.of(BUCKET_NAME, fileName);

        BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                .setContentType(image.getContentType())
                .build();

        storage.create(blobInfo, image.getInputStream());

        return String.format("https://storage.googleapis.com/%s/%s", BUCKET_NAME, fileName);
    }
}
