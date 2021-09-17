package model;

import com.jntoo.db.annotation.FieldType;
import com.jntoo.db.annotation.Fields;
import com.jntoo.db.annotation.HasOne;
import com.jntoo.db.annotation.Table;
@Table("adminOne")
public class AdminsOne {

    @Fields(type = FieldType.PK)
    private Integer id;
    private String content;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "AdminsOne{" +
                "id=" + id +
                ", content='" + content + '\'' +
                '}';
    }
}
