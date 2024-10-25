package goormthonuniv.swu.starcapsule.memory;

import lombok.Getter;

@Getter
public class MemoryResponse {
    private Long id;
    private String title;
    private String answer;
    private String imageUrl;
    private String writer;
    private String object;

    public MemoryResponse(Memory memory){
        this.id = memory.getId();
        this.title = memory.getTitle();
        this.answer = memory.getAnswer();
        this.imageUrl = memory.getImageUrl();
        this.writer = memory.getWriter();
        this.object = memory.getMemoryObjectShape().getObjectShapeName();
    }

}
