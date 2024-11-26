package goormthonuniv.swu.starcapsule.dailyQuestion;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

public interface DailyQuestionRepository extends JpaRepository<DailyQuestion, Long> {
    @Query("SELECT dq FROM DailyQuestion dq WHERE DATE(dq.date) = :date")
    Optional<DailyQuestion> findByDate(@Param("date") LocalDate date);
}