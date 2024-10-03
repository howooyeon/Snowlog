package goormthonuniv.swu.starcapsule.myMemory;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "daily_question")
@Data
@NoArgsConstructor
public class DailyQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String text; // 질문 내용

    @Column(nullable = false, unique = true)
    private LocalDate date; // 해당 질문이 사용되는 날짜

    public DailyQuestion(String text, LocalDate date) {
        this.text = text;
        this.date = date;
    }
}
