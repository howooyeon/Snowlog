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
    private SnowballInfo snowballInfo;
    private PaginationData paginationData;

    public SnowballMemoryResponse(Snowball snowball, Integer page, String isoServerTime) {
        this.snowballInfo = new SnowballInfo(snowball.getId(), snowball.getSnowballName(),
                snowball.getMyMemories().size(), snowball.getMemories().size());
        this.paginationData = new PaginationData(page, isoServerTime, snowball);
    }

    @Getter
    public static class SnowballInfo {
        private Long id;
        @JsonProperty("snowball_name")
        private String snowballName;
        @JsonProperty("self")
        private Integer self;
        @JsonProperty("received")
        private Integer received;

        public SnowballInfo(Long id, String snowballName, Integer self, Integer received) {
            this.id = id;
            this.snowballName = snowballName;
            this.self = self;
            this.received = received;
        }
    }

    @Getter
    public static class PaginationData {
        @JsonProperty("page")
        private Integer page;
        @JsonProperty("total_page")
        private Integer totalPage;
        @JsonProperty("server_time")
        private String serverTime;
        @JsonProperty("memories")
        private List<MemoryDto> memories = new ArrayList<>();

        public PaginationData(Integer page, String serverTime, Snowball snowball) {
            this.page = page;
            this.serverTime = serverTime;
            int totalMemories = snowball.getMemories().size() + snowball.getMyMemories().size();
            this.totalPage = (totalMemories + 5) / 6;

            // Memory 리스트 생성 및 정렬
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
            this.memories.sort(Comparator.comparing(MemoryDto::getCreate_at));

            // 페이지 처리
            int startIndex = (page - 1) * 6;
            int endIndex = Math.min(startIndex + 6, this.memories.size());
            if (startIndex < this.memories.size()) {
                this.memories = this.memories.subList(startIndex, endIndex);
            } else {
                this.memories = new ArrayList<>(); // 페이지 범위 초과 시 빈 리스트 반환
            }
        }
    }
}
