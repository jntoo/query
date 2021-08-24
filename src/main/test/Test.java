import com.jntoo.db.QueryWrapper;
import model.Admins;
import com.jntoo.db.utils.DB;
import model.Datas;

import java.util.List;

public class Test {
    public static void main(String[] args) {

        QueryWrapper<Admins> queryWrapper = DB.name(Admins.class);
        List<Admins> data = queryWrapper.where("id" , 1).select();
        System.out.println(data);

        Admins admins = new Admins();

        admins.setUsername("username");
        admins.setPwd("username");
        Datas datas = new Datas();
        admins.setDatas(datas);
        datas.setAbc("abcc");
        datas.setData("acxxx");
        datas.setName("name1");
        admins.setId(5);
        System.out.println(DB.name(admins).update());


    }
}
