package org.services.products.utils.page;

import lombok.Getter;

import java.util.List;

@Getter
public class PageResult<T> {
    private final List<T> content;
    private final int page;
    private final int size;
    private final int totalPages;
    private final int totalElements;

    public PageResult(List<T> content, int page, int size, int totalElements) {
        this.content = content;
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = (int) Math.ceil((double) totalElements/size);
    }

    @Override
    public String toString() {
        return "PageResult{" +
                "content=" + content +
                ", page=" + page +
                ", size=" + size +
                ", totalPages=" + totalPages +
                '}';
    }
}
