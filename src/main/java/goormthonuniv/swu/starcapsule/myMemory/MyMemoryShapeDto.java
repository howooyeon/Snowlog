package goormthonuniv.swu.starcapsule.myMemory;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MyMemoryShapeDto {
    private Long id;
    private String name;
    private String imageUrl;

    public static MyMemoryShapeDto fromEntity(MyMemoryShape myMemoryShape){
        return MyMemoryShapeDto.builder()
                .id(myMemoryShape.getId())
                .name(myMemoryShape.getName())
                .imageUrl(myMemoryShape.getImageUrl())
                .build();
    }
}
