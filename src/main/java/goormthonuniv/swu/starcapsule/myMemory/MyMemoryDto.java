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
    private String image_url;
    private LocalDateTime create_at;
    private DailyQuestionDto daily_question;
    private MyMemoryShapeDto my_memory_shape;

    public static MyMemoryDto fromEntity(MyMemory memory) {
        return MyMemoryDto.builder()
                .id(memory.getId())
                .title(memory.getTitle())
                .answer(memory.getAnswer())
                .image_url(memory.getImageUrl())
                .create_at(memory.getCreateAt())
                .daily_question(DailyQuestionDto.fromEntity(memory.getDailyQuestion()))
                .my_memory_shape(MyMemoryShapeDto.fromEntity(memory.getMyMemoryShape()))
                .build();
    }
}



