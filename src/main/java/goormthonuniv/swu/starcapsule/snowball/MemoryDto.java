package goormthonuniv.swu.starcapsule.snowball;

import com.fasterxml.jackson.annotation.JsonProperty;
import goormthonuniv.swu.starcapsule.memory.Memory;
import goormthonuniv.swu.starcapsule.myMemory.MyMemory;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class MemoryDto {
    private Long id;
    @JsonProperty("object_name")
    private String object_name;
    @JsonProperty("writer_name")
    private String writer_name;
    private LocalDateTime create_at;

    public MemoryDto(Memory memory) {
        this.id = memory.getId();
        this.object_name = memory.getMemoryObjectShape().getObjectShapeName();
        this.writer_name = memory.getWriter();
        this.create_at = memory.getCreatedAt();
    }

    public MemoryDto(MyMemory myMemory) {
        this.id = myMemory.getId();
        this.object_name = myMemory.getMyMemoryShape().getName();
        this.writer_name = myMemory.getSnowball().getSnowballName();
        this.create_at = myMemory.getCreateAt();
    }
}
