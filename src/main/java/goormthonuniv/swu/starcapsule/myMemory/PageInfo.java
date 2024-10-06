package goormthonuniv.swu.starcapsule.myMemory;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PageInfo {
    private int currentPage;
    private int pageSize;
    private int totalPages;
    private int totalElements;
}

