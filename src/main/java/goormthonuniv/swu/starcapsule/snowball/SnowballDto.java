package goormthonuniv.swu.starcapsule.snowball;

import lombok.Getter;

@Getter
public class SnowballDto {
    private Long id;
    private String snowballName;
    private String userId;

    public SnowballDto(Snowball snowball, String userId) {
        this.id = snowball.getId();
        this.snowballName = snowball.getSnowballName();
        this.userId = userId;
    }
}
