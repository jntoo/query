import com.alibaba.fastjson.JSON;
import com.jntoo.db.*;
import com.jntoo.db.utils.Collect;

import model.Admins;
import com.jntoo.db.utils.DB;
import com.jntoo.db.model.Options;


import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

public class Test {

    public static void main(String[] args) {

        try {
            System.out.println(new File("c:\\upload\\"+"./upload").getCanonicalPath());
        } catch (IOException e) {
            e.printStackTrace();
        }

        DefaultConnection.setUsername("root");
        DefaultConnection.setPwd("root");
        DefaultConnection.setDatabase("test");

        QueryConfig queryConfig = new QueryConfig();
        queryConfig.setPrefix("");
        queryConfig.setConnectionConfig(new DefaultConnection());
        queryConfig.setDebug(true);
        Configuration.setQueryConfig(queryConfig);

        String s = "{\"order\":[\"id desc\"],\"limit\":{\"size\":5},\"where\":[{\"name\":\"a\",\"exp\":\"=\",\"value\":\"c\"}]}";
        Options d = JSON.parseObject(s , Options.class);

        long start = new Date().getTime();

        testHasOne();
        //testHasMany();

        //testSelect();

        /*QueryWrapper<Admins> queryWrapper = DB.name(Admins.class);
        List<Admins> data = queryWrapper.where("id" , 1).limit(2).select();
        System.out.println(data);
        List f=new ArrayList();
        f.add(1);
        List adminsList = DB.name("admins").order("id desc").where("id" , "in" , f ).limit(0,20).select();
        System.out.println(adminsList);
        DB.name("admins").count();
        AdminsDao adminsDao = new AdminsDao();
        adminsDao.limit(1).select();*/
        System.out.println( new Date().getTime() - start );
    }

    public static void isInt( Object data)
    {
        if(data instanceof Long)
        {
            System.out.println("true");
        }
    }

    public static void testHasOne()
    {
        List list = DB.name(Admins.class).select();
        System.out.println(list);
    }


    public static void testSelect()
    {
        System.out.println("test select");
        List data = DB.name("admins").where("username" , "admin")
                .whereIn("id" , "1,2,3")
                .whereBetween("addtime" , "2011-01-01","2012-05-05")
                .whereLike("username" , "%a%")
                .whereLikeNot("username" , "c")
                .whereInNot("id" , "123,2")
                .where("username" ,"eq" , 2)
                .limit(5).order("id" , "desc")
                .group("id").select();
        System.out.println(data);

        Collect<Admins> f = new Collect(1,12);

        Collect lists = DB.name(Admins.class).where("username" , "admin")
                .whereIn("id" , "1,2,3")
                .whereBetween("addtime" , "2011-01-01","2012-05-05")
                .whereLike("username" , "%a%")
                .whereLikeNot("username" , "c")
                .whereInNot("id" , "123,2")
                .where("username" ,"eq" , 2)
                .limit(5).order("id" , "desc")
                .group("id").page(f);

        System.out.println(lists);

    }

}
