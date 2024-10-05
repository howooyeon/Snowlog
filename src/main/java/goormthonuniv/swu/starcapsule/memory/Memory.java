package goormthonuniv.swu.starcapsule.memory;

import com.fasterxml.jackson.annotation.JsonIgnore;
import goormthonuniv.swu.starcapsule.snowball.Snowball;
import goormthonuniv.swu.starcapsule.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "memory")
public class Memory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "answer")
    private String answer;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "writer")
    private String writer;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="memory_object_id")
    private MemoryObjectShape memoryObjectShape;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="snowball_id")
    private Snowball snowball;

    @Builder
    public Memory(String title, String answer, String imageUrl, String writer) {
        this.title = title;
        this.answer = answer;
        this.imageUrl = imageUrl;
        this.writer = writer;
    }

    public void setMemoryObjectShape(MemoryObjectShape memoryObjectShape) {
        this.memoryObjectShape = memoryObjectShape;
        memoryObjectShape.getMemories().add(this);
    }

    public void setSnowball(Snowball snowball) {
        this.snowball = snowball;
        snowball.getMemories().add(this);
    }

}
