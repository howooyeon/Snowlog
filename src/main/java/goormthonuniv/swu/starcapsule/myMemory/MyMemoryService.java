package goormthonuniv.swu.starcapsule.myMemory;

import goormthonuniv.swu.starcapsule.memory.MemoryService;
import goormthonuniv.swu.starcapsule.snowball.Snowball;
import goormthonuniv.swu.starcapsule.snowball.SnowballRepository;
import goormthonuniv.swu.starcapsule.user.User;
import goormthonuniv.swu.starcapsule.user.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;

@Service
public class MyMemoryService {
    private final UserService userService;
    private final MyMemoryRepository myMemoryRepository;
    private final SnowballRepository snowballRepository;
    private final MyMemoryShapeRepository myMemoryShapeRepository;
    private final MemoryService memoryService;

    public MyMemoryService(UserService userService, MyMemoryRepository myMemoryRepository, SnowballRepository snowballRepository, MyMemoryShapeRepository myMemoryShapeRepository, MemoryService memoryService) {
        this.userService = userService;
        this.myMemoryRepository = myMemoryRepository;
        this.snowballRepository = snowballRepository;
        this.myMemoryShapeRepository = myMemoryShapeRepository;
        this.memoryService = memoryService;
    }

    @Transactional
    public void createMemory(String title, String answer, String shapeName, Long userId, MultipartFile image) throws IOException {
        User user = userService.findById(userId);

        MyMemoryShape memoryShape = myMemoryShapeRepository.findByName(shapeName)
                .orElseThrow(() -> new IllegalArgumentException("해당 이름의 메모리 쉐입이 없습니다."));

        Snowball snowball = snowballRepository.findByUser(user)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자에게 스노우볼이 없습니다."));

        // GCP에 이미지를 업로드하고 URL을 가져옴
        String imageUrl = memoryService.getPublicUrl(image);

        MyMemory myMemory = MyMemory.builder()
                .title(title)
                .answer(answer)
                .createAt(LocalDateTime.now())
                .snowball(snowball) // 사용자의 스노우볼 할당
                .myMemoryShape(memoryShape) // 선택된 메모리 쉐입 할당
                .imageUrl(imageUrl) // 이미지 URL 할당
                .user(user)
                .build();

        myMemoryRepository.save(myMemory);
    }

    public MyMemory getMemoryById(Long memoryId) {
        return myMemoryRepository.findById(memoryId)
                .orElse(null);
    }

    // 공개된 추억 조회
    public Page<MyMemory> getReleasedMemories(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return myMemoryRepository.findByIsReleasedTrue(pageable);
    }

    // 공개되지 않은 추억 조회
    public Page<MyMemory> getUnreleasedMemories(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return myMemoryRepository.findByIsReleasedFalse(pageable);
    }


    // 공개된 추억 총 개수 조회
    public int countReleased() {
        return myMemoryRepository.countByIsReleasedTrue();
    }

    // 공개되지 않은 추억 총 개수 조회
    public int countUnreleasedMemories() {
        return myMemoryRepository.countByIsReleasedFalse();
    }
}
