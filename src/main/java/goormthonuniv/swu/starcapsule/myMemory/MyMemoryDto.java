package goormthonuniv.swu.starcapsule.myMemory;

import goormthonuniv.swu.starcapsule.dailyQuestion.DailyQuestionDto;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class MyMemoryDto {
    private Long id;
    private String title;
    private String answer;
    private String imageUrl;
    private LocalDateTime createAt;
    private DailyQuestionDto dailyQuestion;
    private MyMemoryShapeDto myMemoryShape;

    public static MyMemoryDto fromEntity(MyMemory memory) {
        return MyMemoryDto.builder()
                .id(memory.getId())
                .title(memory.getTitle())
                .answer(memory.isReleased() ? memory.getAnswer() : "이 메모리는 아직 공개되지 않았습니다.")
                .imageUrl(memory.isReleased() ? memory.getImageUrl() : null)
                .createAt(memory.getCreateAt())
                .dailyQuestion(DailyQuestionDto.fromEntity(memory.getDailyQuestion()))
                .myMemoryShape(MyMemoryShapeDto.fromEntity(memory.getMyMemoryShape()))
                .build();
    }
}



