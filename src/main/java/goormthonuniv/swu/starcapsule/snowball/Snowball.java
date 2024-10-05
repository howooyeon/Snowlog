package goormthonuniv.swu.starcapsule.snowball;

import goormthonuniv.swu.starcapsule.memory.Memory;
import goormthonuniv.swu.starcapsule.myMemory.MyMemory;
import goormthonuniv.swu.starcapsule.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
@Entity
@Table(name = "snowball")
public class Snowball {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    @Column(name = "snowball_name")
    private String snowballName;

    @Column(name = "shared_link")
    private String sharedLink;

    @OneToOne(mappedBy = "snowball")
    private User user;

    @OneToMany(mappedBy = "snowball")
    private List<Memory> memories = new ArrayList<>();
    @OneToMany(mappedBy = "snowball")
    private List<MyMemory> myMemories = new ArrayList<>();
    @Builder
    public Snowball(String snowballName, String sharedLink, User user) {
        this.snowballName = snowballName;
        this.sharedLink = sharedLink;
        this.user = user;
    }

    public void updateSnowballName(String snowballName) {
        this.snowballName = snowballName;
    }

}
