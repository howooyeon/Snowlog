package goormthonuniv.swu.starcapsule.snowball;

import goormthonuniv.swu.starcapsule.memory.Memory;
import goormthonuniv.swu.starcapsule.myMemory.MyMemory;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class MemoryDto {
    private Long id;
    private String objectName;
    private String writerName;
    private LocalDateTime createAt;

    public MemoryDto(Memory memory) {
        this.id = memory.getId();
        this.objectName = memory.getMemoryObjectShape().getObjectShapeName();
        this.writerName = memory.getWriter();
        this.createAt = memory.getCreatedAt();
    }

    public MemoryDto(MyMemory myMemory) {
        this.id = myMemory.getId();
        this.objectName = myMemory.getMyMemoryShape().getName();
        this.writerName = myMemory.getUser().getNickname();
        this.createAt = myMemory.getCreateAt();
    }
}
