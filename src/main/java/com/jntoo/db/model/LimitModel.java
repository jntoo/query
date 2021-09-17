package com.jntoo.db.model;

public class LimitModel {
    private Long offset;
    private Long size;

    public LimitModel(){}

    public LimitModel(Long size) {
        this.size = size;
    }
    public LimitModel(Long offset, Long size) {
        this.offset = offset;
        this.size = size;
    }

    public Long getOffset() {
        return offset;
    }
    public void setOffset(Long offset) {
        this.offset = offset;
    }
    public Long getSize() {
        return size;
    }
    public void setSize(Long size) {
        this.size = size;
    }
}
