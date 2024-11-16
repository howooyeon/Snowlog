package goormthonuniv.swu.starcapsule.dailyQuestion;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "daily_question")
@Data
@NoArgsConstructor
public class DailyQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String question;

    @Column(nullable = false, unique = true)
    private LocalDateTime date;

    @Builder
    public DailyQuestion(String question, LocalDateTime date) {
        this.question = question;
        this.date = date;
    }
}
