package com.example.demo.dto.response;

public class PageResponse<T> {
    private int page;
    private int size;
    private long totalElements;
    private T content;

    public PageResponse(int page, int size, long totalElements, T content) {
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.content = content;
    }

    public int getPage() {
        return page;
    }

    public int getSize() {
        return size;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public T getContent() {
        return content;
    }
}
