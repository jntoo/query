package model;


import com.jntoo.db.annotation.FieldType;
import com.jntoo.db.annotation.Fields;
import com.jntoo.db.annotation.Table;
import com.jntoo.db.utils.TimerUtils;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Map;


/**
 *  管理员模块的实体类
 */
@Table
public class Admins implements Serializable {

    private static final long serialVersionUID = 1L;

    @Fields(type = FieldType.PK_AUTO)
    private Integer id;
    private String username;

    @Fields(type = FieldType.DEFAULT , autoUpdate = "Pwd")
    private String pwd;
    private Timestamp addtime;

    @Fields(type = FieldType.JSON)
    private Datas datas;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }


    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username == null ? "" : username.trim();
    }

    public String getPwd() {
        return pwd;
    }
    public void setPwd(String pwd) {
        this.pwd = pwd == null ? "" : pwd.trim();
    }

    public Timestamp getAddtime() {
        return addtime;
    }
    public void setAddtime(Timestamp addtime) {
        this.addtime = addtime;
    }

    public Datas getDatas() {
        return datas;
    }

    public void setDatas(Datas datas) {
        this.datas = datas;
    }

    public String autoPwdUpdate(Map mData)
    {
        return "abccccc";
    }
    @Override
    public String toString() {
        return "Admins{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", pwd='" + pwd + '\'' +
                ", addtime='" + TimerUtils.date("yyyy-MM-dd HH:mm:ss",addtime) + '\'' +
                ", datas=" + datas +
                '}';
    }
}
