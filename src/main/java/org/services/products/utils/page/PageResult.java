package org.services.products.utils.page;

import lombok.Data;
import lombok.Getter;

import java.util.List;

@Data
public class PageResult<T> {
    private final List<T> content;
    private final int page;
    private final int size;
    private  int totalPages;
    private final int totalElements;

    public PageResult(List<T> content, int page, int size, int totalElements) {
        this.content = content;
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = (int) Math.ceil((double) totalElements/size);
    }

}
