package goormthonuniv.swu.starcapsule.snowball;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class SnowballResponse {
    private Long id;
    @JsonProperty("snowball_name")
    private String snowballName;
    @JsonProperty("shared_link")
    private String sharedLink;

    public SnowballResponse(Snowball snowball){
        this.id = snowball.getId();
        this.snowballName = snowball.getSnowballName();
        this.sharedLink = snowball.getSharedLink();
    }
}
