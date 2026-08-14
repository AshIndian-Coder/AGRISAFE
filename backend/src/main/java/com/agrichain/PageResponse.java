package com.agrichain.common.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Paginated response wrapper
 */
@Data
@Builder
public class PageResponse<T> {
    
    private List<T> items;
    private PaginationInfo pagination;

    @Data
    @Builder
    public static class PaginationInfo {
        private int page;
        private int pageSize;
        private long totalItems;
        private int totalPages;
        private boolean hasNext;
        private boolean hasPrevious;
    }

    public static <T, U> PageResponse<U> from(Page<T> page, Function<T, U> mapper) {
        List<U> items = page.getContent().stream()
                .map(mapper)
                .collect(Collectors.toList());

        return PageResponse.<U>builder()
                .items(items)
                .pagination(PaginationInfo.builder()
                        .page(page.getNumber() + 1)
                        .pageSize(page.getSize())
                        .totalItems(page.getTotalElements())
                        .totalPages(page.getTotalPages())
                        .hasNext(page.hasNext())
                        .hasPrevious(page.hasPrevious())
                        .build())
                .build();
    }
}
