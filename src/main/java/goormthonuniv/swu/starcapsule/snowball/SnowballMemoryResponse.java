package goormthonuniv.swu.starcapsule.snowball;

import com.fasterxml.jackson.annotation.JsonProperty;
import goormthonuniv.swu.starcapsule.memory.Memory;
import goormthonuniv.swu.starcapsule.myMemory.MyMemory;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Getter
public class SnowballMemoryResponse {
    private Long id;
    @JsonProperty("snowball_name")
    private String snowballName;
    @JsonProperty("received")
    private Integer received;
    @JsonProperty("self")
    private Integer self;
    @JsonProperty("page")
    private Integer page;
    @JsonProperty("total_page")
    private Integer totalPage;
    @JsonProperty("memories")
    private List<MemoryDto> memories = new ArrayList<>();
    @JsonProperty("server_time") // 서버 시간 필드 추가
    private String serverTime;

    public SnowballMemoryResponse(Snowball snowball, Integer page, String isoServerTime) {
        this.id = snowball.getId();
        this.snowballName = snowball.getSnowballName();
        this.received = snowball.getMemories().size();
        this.self = snowball.getMyMemories().size();
        this.page = page;
        this.totalPage = (this.received + this.self + 5) / 6;
        this.serverTime = isoServerTime; // 서버 시간 저장

        // Memory 리스트 생성
        if (snowball.getMemories() != null) {
            for (Memory memory : snowball.getMemories()) {
                this.memories.add(new MemoryDto(memory));
            }
        }
        if (snowball.getMyMemories() != null) {
            for (MyMemory myMemory : snowball.getMyMemories()) {
                this.memories.add(new MemoryDto(myMemory));
            }
        }

        // 생성일 기준으로 정렬
        this.memories.sort(Comparator.comparing(MemoryDto::getCreateAt));

        // 페이지 처리
        int startIndex = (page - 1) * 6;
        int endIndex = Math.min(startIndex + 6, this.memories.size());
        if (startIndex < this.memories.size()) {
            this.memories = this.memories.subList(startIndex, endIndex);
        } else {
            this.memories = new ArrayList<>();  // 페이지 범위 초과 시 빈 리스트 반환
        }
    }
}

