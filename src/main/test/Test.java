import com.jntoo.db.QueryWrapper;
import model.Admins;
import com.jntoo.db.utils.DB;
import model.AdminsOne;
import model.Datas;

import java.lang.reflect.Field;
import java.util.List;

public class Test {
    public static void main(String[] args) {

        Field[] fields = AdminsOne.class.getDeclaredFields();


        QueryWrapper<Admins> queryWrapper = DB.name(Admins.class);
        List<Admins> data = queryWrapper.where("id" , 1).select();
        System.out.println(data);


        Admins admins = new Admins();
        admins.setUsername("username");

        Datas datas = new Datas();
        admins.setDatas(datas);
        datas.setAbc("abcc");
        datas.setData("acxxx");
        datas.setName("name1");
        admins.setId(5);
        System.out.println(DB.name(admins).update());



        List adminsList = DB.name("admins").select();

        System.out.println(adminsList);


    }
}
