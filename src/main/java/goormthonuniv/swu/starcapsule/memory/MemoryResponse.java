package goormthonuniv.swu.starcapsule.memory;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class MemoryResponse {
    private Long id;
    private String title;
    private String answer;
    private String image_url;
    private String writer;
    private String object_name;
    private LocalDateTime create_at;

    public MemoryResponse(Memory memory) {
        this.id = memory.getId();
        this.title = memory.getTitle();
        this.answer = memory.getAnswer();
        this.image_url = memory.getImageUrl();
        this.writer = memory.getWriter();
        this.object_name = memory.getMemoryObjectShape().getObjectShapeName();
        this.create_at = memory.getCreatedAt();
    }
}
