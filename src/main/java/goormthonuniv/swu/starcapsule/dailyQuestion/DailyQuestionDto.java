package goormthonuniv.swu.starcapsule.dailyQuestion;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class DailyQuestionDto {
    private Long id;
    private String question;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime date;

    public static DailyQuestionDto fromEntity(DailyQuestion dailyQuestion) {
        return DailyQuestionDto.builder()
                .id(dailyQuestion.getId())
                .question(dailyQuestion.getQuestion())
                .date(dailyQuestion.getDate())
                .build();
    }
}
