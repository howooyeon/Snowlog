package goormthonuniv.swu.starcapsule.myMemory;

import goormthonuniv.swu.starcapsule.snowball.Snowball;
import goormthonuniv.swu.starcapsule.user.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Table(name = "my_memory")
@NoArgsConstructor
@Data
@Entity
public class MyMemory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String answer;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(nullable = false)
    private LocalDateTime createAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "snowball_id", nullable = false)
    private Snowball snowball;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "my_memory_shape_id", nullable = false)
    private MyMemoryShape myMemoryShape;

    @PrePersist
    public void prePersist() {
        this.createAt = LocalDateTime.now();
    }

    @Builder
    public MyMemory(String title, String answer, String imageUrl, LocalDateTime createAt, Snowball snowball, User user, MyMemoryShape myMemoryShape) {
        this.title = title;
        this.answer = answer;
        this.imageUrl = imageUrl;
        this.createAt = createAt != null ? createAt : LocalDateTime.now();
        this.snowball = snowball;
        this.myMemoryShape = myMemoryShape;
        this.user = user;
    }

}
