package com.jntoo.db.build;


import com.jntoo.db.model.LimitModel;
import com.jntoo.db.utils.*;
import com.jntoo.db.*;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * SQLserver 语句构造器
 */
public class SqlServer extends Builder{
    //@Override
    protected String pageSql  = "SELECT T1.* FROM (SELECT build.*, ROW_NUMBER() OVER (%ORDER%) AS ROW_NUMBER FROM (SELECT %DISTINCT% %FIELD% FROM %TABLE%%JOIN%%WHERE%%GROUP%%HAVING%) AS build) AS T1 %LIMIT%";
    public SqlServer() {
        super();
        //if(isPage()){
        //    selectSql = "SELECT T1.* FROM (SELECT build.*, ROW_NUMBER() OVER (%ORDER%) AS ROW_NUMBER FROM (SELECT %DISTINCT% %FIELD% FROM %TABLE%%JOIN%%WHERE%%GROUP%%HAVING%) AS build) AS T1 %LIMIT%";
        //}
    }

    @Override
    protected String getSelectSql() {
        if(isPage()){
            return pageSql;
        }
        String sql = "SELECT %LIMIT% %DISTINCT% %FIELD% FROM %TABLE%%FORCE%%JOIN%%WHERE%%GROUP%%HAVING%%ORDER% %LOCK%";
        return sql;
    }

    @Override
    public String parseIfNull(String func, String str) {
        return "ISNULL("+func+" , "+str+")";
    }

    @Override
    public String parseOrder() {
        ArrayList list = (ArrayList) getQuery().getOptions().getOrder();
        if(list == null || list.size() == 0){
            return isPage() ? " ORDER BY rand() " : "";
        }else{
              for (int j=0;j<list.size();j++)
              {
                  Object obj = list.get(j);
                  String str = obj.toString();
                  String[] arrs = str.split(",");
                  for (int i=0; i < arrs.length;i++)
                  {
                      String s = arrs[i];
                      if (s.indexOf(".") != -1)
                      {
                          String[] ss = s.split("\\.");
                          arrs[i] = "build." + ss[1];
                      }
                  }
                  list.set(j, StringUtil.join(",",arrs));

              }
          }
        return super.parseOrder();
    }

    @Override
    protected String getTableFind(String name) {
        return String.format("SELECT top 1 * FROM %s WHERE 1=1" , name);
    }

    @Override
    protected String parseLimit() {
        LimitModel map = getQuery().getOptions().getLimit();
        if(map == null){
            return "";
        }
        Long offset = map.getOffset();
        Long limit  = map.getSize();
        String limitStr = " WHERE ";
        if(!isPage()){
            return "TOP "+limit;
        }else{
            if(offset == null){
                limitStr += "(T1.ROW_NUMBER BETWEEN 1 AND " + limit + ")";
            }else{
                limitStr += "(T1.ROW_NUMBER BETWEEN "+ offset +"+1 AND "+offset+" + "+limit+" )";
            }
            return limitStr;
        }
    }
}
