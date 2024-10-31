package goormthonuniv.swu.starcapsule.snowball;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BasicSnowballResponse {
    private String snowballName;
    private int selfCount;
    private int receivedCount;

    public static BasicSnowballResponse from(Snowball snowball) {
        return BasicSnowballResponse.builder()
                .snowballName(snowball.getSnowballName())
                .selfCount(snowball.getMyMemories().size())
                .receivedCount(snowball.getMemories().size())
                .build();
    }
}
