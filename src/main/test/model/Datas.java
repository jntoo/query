package model;

public class Datas {
    private String name;
    private String abc;
    private String data;
    // {"name":"name","abc":"abc","data":"data"}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAbc() {
        return abc;
    }

    public void setAbc(String abc) {
        this.abc = abc;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Datas{" +
                "name='" + name + '\'' +
                ", abc='" + abc + '\'' +
                ", data='" + data + '\'' +
                '}';
    }
}
