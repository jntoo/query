package model;

public class Limit {
    private Integer offset;
    private Integer size;

    public Limit(){}

    public Limit(Integer size) {
        this.size = size;
    }
    public Limit(Integer offset, Integer size) {
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
