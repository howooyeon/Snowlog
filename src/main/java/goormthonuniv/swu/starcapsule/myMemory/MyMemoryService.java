package goormthonuniv.swu.starcapsule.myMemory;

import goormthonuniv.swu.starcapsule.snowball.Snowball;
import goormthonuniv.swu.starcapsule.snowball.SnowballRepository;
import goormthonuniv.swu.starcapsule.snowball.SnowballService;
import goormthonuniv.swu.starcapsule.user.User;
import goormthonuniv.swu.starcapsule.user.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class MyMemoryService {
    private final UserService userService;
    private final MyMemoryRepository myMemoryRepository;
    private final SnowballRepository snowballRepository;
    private final MyMemoryShapeRepository myMemoryShapeRepository;

    public MyMemoryService(UserService userService, MyMemoryRepository myMemoryRepository, SnowballRepository snowballRepository, MyMemoryShapeRepository myMemoryShapeRepository) {
        this.userService = userService;
        this.myMemoryRepository = myMemoryRepository;
        this.snowballRepository = snowballRepository;
        this.myMemoryShapeRepository = myMemoryShapeRepository;
    }

    @Transactional
    public void createMemory(String title, String answer, String shapeName, Long userId) {
        User user = userService.findById(userId);

        MyMemoryShape memoryShape = myMemoryShapeRepository.findByName(shapeName)
                .orElseThrow(() -> new IllegalArgumentException("해당 이름의 메모리 쉐입이 없습니다."));

        Snowball snowball = snowballRepository.findByUser(user)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자에게 스노우볼이 없습니다."));

        MyMemory myMemory = MyMemory.builder()
                .title(title)
                .answer(answer)
                .createAt(LocalDateTime.now())
                .snowball(snowball) // 사용자의 스노우볼 할당
                .myMemoryShape(memoryShape) // 선택된 메모리 쉐입 할당
                .user(user)
                .build();

        myMemoryRepository.save(myMemory);
    }




}

