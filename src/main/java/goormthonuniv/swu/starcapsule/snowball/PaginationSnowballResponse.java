package goormthonuniv.swu.starcapsule.snowball;

import com.fasterxml.jackson.annotation.JsonProperty;
import goormthonuniv.swu.starcapsule.memory.Memory;
import goormthonuniv.swu.starcapsule.myMemory.MyMemory;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Getter
public class PaginationSnowballResponse {
    private PaginationData paginationData;

    public PaginationSnowballResponse(Integer page, String isoServerTime, Snowball snowball) {
        this.paginationData = new PaginationData(page, isoServerTime, snowball);
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

            int startIndex = (page - 1) * 6;
            int endIndex = Math.min(startIndex + 6, this.memories.size());
            this.memories = startIndex < this.memories.size() ?
                    this.memories.subList(startIndex, endIndex) : new ArrayList<>();
        }
    }
}
