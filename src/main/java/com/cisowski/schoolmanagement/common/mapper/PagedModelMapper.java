package com.cisowski.schoolmanagement.common.mapper;

import com.cisowski.schoolmanagement.common.model.PagedResponse;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.function.Function;

public class PagedModelMapper {

    public static <S, T> PagedResponse<T> mapToResponse(Page<S> page, Function<S, T> converter) {
        List<T> convertedContent = page.getContent().stream()
                .map(converter)
                .toList();

        return PagedResponse.<T>builder()
                .content(convertedContent)
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .build();
    }
}
