package goormthonuniv.swu.starcapsule.myMemory;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface DailyQuestionRepository extends JpaRepository<DailyQuestion ,Long> {
    Optional<DailyQuestion> findByDate(LocalDate date);
}
