package com.tripsnap.api.utils;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Getter
@Builder
public class CombinedPageable{
    final private long page;
    final private long limit;
    final private long offset;
    final private boolean nextDataFetch;

    public static CombinedPageable get(Pageable pageable, Page<?> pageData) {
        var list = pageData.getContent();
        long page = pageable.getPageNumber() - pageData.getTotalElements() / pageable.getPageSize();
        long limit = list.isEmpty() ? pageable.getPageSize() : pageable.getPageSize() - list.size();
        long offset = page == 0 ? 0 : (page*pageable.getPageSize()) - pageData.getTotalElements() % pageable.getPageSize();

        return CombinedPageable.builder().page(page).limit(limit).offset(offset).nextDataFetch(page>=0).build();
    }
}
