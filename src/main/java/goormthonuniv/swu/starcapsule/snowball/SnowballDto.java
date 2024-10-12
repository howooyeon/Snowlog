package goormthonuniv.swu.starcapsule.snowball;

import goormthonuniv.swu.starcapsule.user.User;
import lombok.Getter;

@Getter
public class SnowballDto {
    private Long id;
    private String snowballName;
    private Long userId;

    public SnowballDto(Snowball snowball, User user) {
        this.id = snowball.getId();
        this.snowballName = snowball.getSnowballName();
        this.userId = user.getId();
    }
}
