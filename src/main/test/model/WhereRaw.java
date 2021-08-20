package model;

public class WhereRaw extends Where {
    private String raw;

    public WhereRaw(String raw)
    {
        super();
        this.raw = raw;
    }

    public String getRaw() {
        return raw;
    }

    public void setRaw(String raw) {
        this.raw = raw;
    }
}
