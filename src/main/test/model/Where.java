package model;

public class Where {
    private String name;
    private String exp;
    private Object value;
    private String connect;
    public Where(){

    }

    public Where(String field , Object value){
        this(field , null , value,null);
    }

    public Where(String field , String exp , Object value)
    {
        this(field , null , value,null);
    }

    public Where(String field ,String exp, Object value , String connect){
        name = field;
        this.exp = exp == null ? "=" : exp;
        this.value = value;
        this.connect = connect == null ? "and" : connect;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExp() {
        return exp;
    }

    public void setExp(String exp) {
        this.exp = exp;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getConnect() {
        return connect;
    }

    public void setConnect(String connect) {
        this.connect = connect;
    }
}
