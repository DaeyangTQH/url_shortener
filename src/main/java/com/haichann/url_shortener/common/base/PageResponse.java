package com.haichann.url_shortener.common.base;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Paginated response wrapper.")
public class PageResponse<T> {

    @Schema(description = "Items in the current page.")
    List<T> content;

    @Schema(description = "Zero-based page index.", example = "0")
    int page;

    @Schema(description = "Page size.", example = "10")
    int size;

    @Schema(description = "Total number of items across all pages.", example = "42")
    long totalElements;

    @Schema(description = "Total number of pages.", example = "5")
    int totalPages;

    @Schema(description = "Whether this is the first page.", example = "true")
    boolean first;

    @Schema(description = "Whether this is the last page.", example = "false")
    boolean last;

    public static <T> PageResponse<T> from(Page<T> page) {
        return PageResponse.<T>builder()
                .content(page.getContent())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .build();
    }
}
