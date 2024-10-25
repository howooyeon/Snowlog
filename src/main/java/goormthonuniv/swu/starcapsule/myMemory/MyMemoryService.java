package goormthonuniv.swu.starcapsule.myMemory;

import goormthonuniv.swu.starcapsule.dailyQuestion.DailyQuestion;
import goormthonuniv.swu.starcapsule.dailyQuestion.DailyQuestionService;
import goormthonuniv.swu.starcapsule.memory.Memory;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class MyMemoryService {
    private final UserService userService;
    private final MyMemoryRepository myMemoryRepository;
    private final SnowballRepository snowballRepository;
    private final MyMemoryShapeRepository myMemoryShapeRepository;
    private final MemoryService memoryService;
    private final DailyQuestionService dailyQuestionService;

    public MyMemoryService(UserService userService, MyMemoryRepository myMemoryRepository, SnowballRepository snowballRepository, MyMemoryShapeRepository myMemoryShapeRepository, MemoryService memoryService, DailyQuestionService dailyQuestionService) {
        this.userService = userService;
        this.myMemoryRepository = myMemoryRepository;
        this.snowballRepository = snowballRepository;
        this.myMemoryShapeRepository = myMemoryShapeRepository;
        this.memoryService = memoryService;
        this.dailyQuestionService = dailyQuestionService;
    }

    @Transactional
    public void createMemory(String title, String answer, String shapeName, String email, MultipartFile image) throws IOException {
        User user = userService.findByEmail(email);

        MyMemoryShape memoryShape = myMemoryShapeRepository.findByName(shapeName)
                .orElseThrow(() -> new IllegalArgumentException("해당 이름의 메모리 쉐입이 없습니다."));

        Snowball snowball = snowballRepository.findByUser(user)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자에게 스노우볼이 없습니다."));

        DailyQuestion dailyQuestion = dailyQuestionService.getTodayQuestion().orElseThrow(() -> new IllegalArgumentException("오늘의 질문이 없습니다."));

        if(image == null){
            MyMemory myMemory = MyMemory.builder()
                    .title(title)
                    .answer(answer)
                    .createAt(LocalDateTime.now())
                    .snowball(snowball) // 사용자의 스노우볼 할당
                    .myMemoryShape(memoryShape) // 선택된 메모리 쉐입 할당
                    .dailyQuestion(dailyQuestion)
                    .user(user)
                    .build();

            myMemoryRepository.save(myMemory);
        }else{
            // GCP에 이미지를 업로드하고 URL을 가져옴
            String imageUrl = memoryService.getPublicUrl(image);

            MyMemory myMemory = MyMemory.builder()
                    .title(title)
                    .answer(answer)
                    .createAt(LocalDateTime.now())
                    .snowball(snowball) // 사용자의 스노우볼 할당
                    .myMemoryShape(memoryShape) // 선택된 메모리 쉐입 할당
                    .imageUrl(imageUrl) // 이미지 URL 할당
                    .dailyQuestion(dailyQuestion)
                    .user(user)
                    .build();

            myMemoryRepository.save(myMemory);
        }
    }

    // 내 추억에 쓴 글 있는지 없는지
    public boolean existsByDateAndUser(LocalDateTime date, String email) {
        LocalDateTime startOfDay = date.withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endOfDay = startOfDay.plusDays(1);
        return myMemoryRepository.existsByCreateAtAndEmail(startOfDay, endOfDay, email);
    }

    public List<MyMemory> findMyMemoriesByDateAndUserBetween(LocalDateTime startDateTime, LocalDateTime endDateTime, String email) {
        return myMemoryRepository.findByCreateAtBetweenAndEmail(startDateTime, endDateTime, email);
    }

    public MyMemory getMemoryById(Long memoryId) {
        return myMemoryRepository.findById(memoryId)
                .orElse(null);
    }



}
