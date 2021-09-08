import com.jntoo.db.*;
import model.Admins;
import com.jntoo.db.utils.DB;
import model.AdminsOne;
import model.Datas;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class Test {


    public static void main(String[] args) {


        DefaultConnection.setUsername("root");
        DefaultConnection.setPwd("root");
        DefaultConnection.setDatabase("javamvc08652gxstglxt");

        QueryConfig queryConfig = new QueryConfig();
        queryConfig.setPrefix("");
        queryConfig.setConnectionConfig(new DefaultConnection());
        queryConfig.setDebug(true);
        Configuration.setQueryConfig(queryConfig);


        QueryWrapper<Admins> queryWrapper = DB.name(Admins.class);
        List<Admins> data = queryWrapper.where("id" , 1).limit(2).select();
        System.out.println(data);
        List f=new ArrayList();
        f.add(1);


        List adminsList = DB.name("admins").order("id desc").where("id" , "in" , f ).limit(0,20).select();

        System.out.println(adminsList);
        DB.name("admins").count();
    }

    public static void isInt( Object data)
    {
        if(data instanceof Long)
        {
            System.out.println("true");
        }
    }

}
