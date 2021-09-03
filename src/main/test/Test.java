import com.jntoo.db.DefaultConnection;
import com.jntoo.db.QueryMap;
import com.jntoo.db.QueryWrapper;
import model.Admins;
import com.jntoo.db.utils.DB;
import model.AdminsOne;
import model.Datas;

import java.lang.reflect.Field;
import java.util.List;

public class Test {


    public static void main(String[] args) {
        DefaultConnection.setUsername("root");
        DefaultConnection.setPwd("root");
        DefaultConnection.setDatabase("javamvc08652gxstglxt");

        QueryWrapper<Admins> queryWrapper = DB.name(Admins.class);
        List<Admins> data = queryWrapper.where("id" , 1).limit(2).select();
        System.out.println(data);

        List adminsList = DB.name("admins").limit(0,20).select();

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
