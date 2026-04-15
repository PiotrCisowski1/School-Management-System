package com.cisowski.schoolmanagement;

import com.cisowski.schoolmanagement.common.model.PagedResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;

public abstract class AbstractBaseTest {

    protected <T> PagedResponse<T> toPagedResponseOnlyContent(List<T> content) {
        return PagedResponse.<T>builder()
                .content(content)
                .build();
    }

    protected <T> Page<T> toPageOnlyContent(List<T> content) {
        return new PageImpl<>(content);
    }
}
