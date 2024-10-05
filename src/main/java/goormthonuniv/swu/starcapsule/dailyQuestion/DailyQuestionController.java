package goormthonuniv.swu.starcapsule.dailyQuestion;

import goormthonuniv.swu.starcapsule.global.template.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class DailyQuestionController {

    private final DailyQuestionService dailyQuestionService;

    // 오늘의 질문을 가져오는 API
    @GetMapping("/api/daily-question/today")
    public ResponseEntity<?> getTodayQuestion() {
        Optional<DailyQuestion> todayQuestion = dailyQuestionService.getTodayQuestion();
        if (todayQuestion.isPresent()) {
            return ResponseEntity.ok(BaseResponse.response(todayQuestion.get()));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(BaseResponse.response("오늘의 질문을 찾을 수 없습니다."));
        }
    }
}
