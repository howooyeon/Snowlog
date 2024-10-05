package goormthonuniv.swu.starcapsule.dailyQuestion;

import goormthonuniv.swu.starcapsule.dailyQuestion.DailyQuestion;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DailyQuestionService {

    private final DailyQuestionRepository dailyQuestionRepository;

    // 오늘 날짜에 해당하는 질문을 가져옵니다.
    public Optional<DailyQuestion> getTodayQuestion() {
        LocalDate today = LocalDate.now();
        return dailyQuestionRepository.findByDate(today);
    }
}