package goormthonuniv.swu.starcapsule.myMemory;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class MyMemoryListDto {
    private List<MyMemoryDto> memory;
    private PageInfo pageInfo;
}
