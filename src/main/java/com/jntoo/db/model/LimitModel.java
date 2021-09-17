package com.jntoo.db.model;

public class LimitModel {
    private Integer offset;
    private Integer size;

    public LimitModel(){}

    public LimitModel(Integer size) {
        this.size = size;
    }
    public LimitModel(Integer offset, Integer size) {
        this.offset = offset;
        this.size = size;
    }

    public Integer getOffset() {
        return offset;
    }
    public void setOffset(Integer offset) {
        this.offset = offset;
    }
    public Integer getSize() {
        return size;
    }
    public void setSize(Integer size) {
        this.size = size;
    }
}
