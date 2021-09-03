package com.jntoo.db.build;

import com.jntoo.db.Configuration;
import com.jntoo.db.QueryWrapper;
import com.jntoo.db.utils.DB;
import com.jntoo.db.utils.StringUtil;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

public class Builder {
    protected String selectSql = "SELECT%DISTINCT% %FIELD% FROM %TABLE%%FORCE%%JOIN%%WHERE%%GROUP%%HAVING%%ORDER%%LIMIT% %LOCK%";
    protected String updateSql = "UPDATE %TABLE% SET %SET%%JOIN%%WHERE% %LOCK%";
    protected String insertSql = "%INSERT% INTO %TABLE% (%FIELD%) VALUES (%DATA%)";
    protected String deleteSql = "DELETE FROM %TABLE%%JOIN%%WHERE%%ORDER%%LIMIT% %LOCK%";
    private boolean isPage = false;

    protected List<Object> bindData = new ArrayList();


    public Builder()
    {

    }


    static protected Builder content = null;


    /**
     * 构建Builder ，也只能使用这个来生成,会自动判断当前连接的是sqlserver 还是 mysql
     * @return Builder
     */
    static public Builder make()
    {
        if(content == null){
            Connection connect = Configuration.getConnectionConfig().getConn();
            String str = connect.toString();
            if(str.indexOf("com.mysql") != -1){
                content = new Mysql();
            }else{
                content = new SqlServer();
            }
            Configuration.getConnectionConfig().closeConn(connect);
        }
        return content;
    }


    /**
     * 获取尚未构建的查询语句，子类可以替换他
     * @return String
     */
    protected String getSelectSql()
    {
        return selectSql;
    }


    /**
     * 构建查询语句
     * @param query QueryWrapper实例
     * @return String
     */
    public String buildSelect(QueryWrapper query)
    {
        String sql = getSelectSql();
        bindData = new ArrayList();

        return sql.replace("%DISTINCT%" , parseDistinct(query))
                .replace("%FIELD%" , parseField(query))
                .replace("%TABLE%" , parseTable(query))
                .replace("%FORCE%" , parseForce(query))
                .replace("%JOIN%" , parseJoin(query))
                .replace("%WHERE%" , parseWhere(query))
                .replace("%HAVING%" , parseHaving(query))
                .replace("%GROUP%" , parseGroup(query))
                .replace("%ORDER%" , parseOrder(query))
                .replace("%LIMIT%" , parseLimit(query))
                .replace("%LOCK%" , parseLock(query));
    }

    public void bindData(Object data)
    {
        bindData.add(data);
    }

    public List<Object> getBindData() {
        return bindData == null ? new ArrayList() : bindData;
    }

    /**
     * 构建ifnull 代码
     * @param func 左侧字段是否为空字段
     * @param str  为null 时使用该值
     * @return String
     */
    public String parseIfNull(String func , String str)
    {
        return "IFNULL("+func+" , "+str+")";
    }

    /**
     * 获取MAP 的所有键
     * @param map 从Map获取所有得keys
     * @return List
     */
    protected ArrayList getHashMapKeys(Map map)
    {
        Set keys = map.keySet();
        ArrayList result = new ArrayList();
        Iterator iter = keys.iterator();
        while(iter.hasNext()){
            result.add((String)iter.next());
        }
        return result;
    }

    /**
     * 构建插入语句
     * @param query QueryWrapper实例
     * @param replace 是否使用替换
     * @return String
     */
    public String buildInsert(QueryWrapper query , boolean replace)
    {
        bindData = new ArrayList();
        Map data = query.getData();
        if(data.isEmpty()){
            return "";
        }

        // 没数据不允许插入
        Map formatData = parseData(query,data,true);
        // 经过格式化的数据
        ArrayList fields = getHashMapKeys(formatData);
        Collection values = formatData.values();

        //insertSql = "%INSERT% INTO %TABLE% (%FIELD%) VALUES (%DATA%)";
        return insertSql.replace("%INSERT%" , replace?"REPLACE":"INSERT")
                .replace("%TABLE%" , parseTable(query))
                .replace("%FIELD%" , StringUtil.join(" , " , fields))
                .replace("%DATA%" , StringUtil.join(" , ",values))
                ;
    }

    /**
     * 构建删除语句
     * @param query QueryWrapper实例
     * @return String
     */
    public String buildDelete(QueryWrapper query)
    {
        //protected String deleteSql = "DELETE FROM %TABLE%%JOIN%%WHERE%%ORDER%%LIMIT% %LOCK%";
        bindData = new ArrayList();
        return deleteSql.replace("%TABLE%" , parseTable(query))
                .replace("%JOIN%" , parseJoin(query))
                .replace("%WHERE%" , parseWhere(query))
                .replace("%ORDER%" , parseOrder(query))
                .replace("%LIMIT%" , parseLimit(query))
                .replace("%LOCK%" , parseLock(query))
                ;
    }

    /**
     * 构建更新语句
     * @param query QueryWrapper实例
     * @return String
     */
    public String buildUpdate( QueryWrapper query )
    {
        bindData = new ArrayList();
        Map data = query.getData();
        if(data.isEmpty()){
            return "";
        }
        Map formatData = parseData(query,data,false);
        Set keys = formatData.keySet();
        ArrayList set = new ArrayList(keys.size());
        Iterator iter = keys.iterator();
        while(iter.hasNext()){
            String key = (String)iter.next();
            String val = (String)formatData.get(key);

            set.add(key + "="+val);
        }
        //protected String updateSql = "UPDATE %TABLE% SET %SET%%JOIN%%WHERE%%ORDER%%LIMIT% %LOCK%";
        return updateSql.replace("%TABLE%" , parseTable(query))
                .replace("%SET%" , StringUtil.join(" , " , set))
                .replace("%JOIN%" , parseJoin(query))
                .replace("%WHERE%" , parseWhere(query))
                .replace("%LOCK%" , parseLock(query))
                ;
    }


    /**
     * 根据字段类型获取对应的默认数据
     * @param type 字段得类型
     * @return String
     */
    protected String getFieldDefault(String type)
    {
        String t = type.toUpperCase();
        if(t.equals("DATE")){
            //return "'0000-00-00'";
            return "0000-00-00";
        }else if(t.equals("DATETIME")){

            //return "'0000-00-00 00:00:00'";
            return "0000-00-00 00:00:00";
        }else if(t.equals("TIME")){

            //return "00:00:00";
            return "'00:00:00'";
        }else if(t.equals("TIMESTAMP")){
            String dateString = "";
            try {//yyyyMMddHHmmss
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date currentTime_1 = new Date();
                dateString = formatter.format(currentTime_1);
            } catch (Exception e) {
            }
            //return "'"+dateString+"'";
            return dateString;
        }else if(t.equals("FLOAT") || t.equals("DOUBLE") || t.equals("DECIMAL") || t.indexOf("INT")!=-1){
            return "0";
        }
        //return "''";
        return "";
    }

    /**
     * 获取字段的值
     * @param type 字段类型
     * @param value 默认值为空时自动加入信息
     * @return String
     */
    protected String getFieldValue(String type , String value)
    {
        String t = type.toUpperCase();
        if(value == null || value.equals("")){
            // 等于空值，就写入默认值
            return getFieldDefault(type);
        }
        if(t.equals("FLOAT") || t.equals("DOUBLE") || t.equals("DECIMAL") || t.indexOf("INT")!=-1){
            return value;
        }
        return value;
        //return "'"+value.replace("'" , "\\'")+"'";
    }

    /**
     * 获取表字段信息
     * @param name 表名称
     * @return String
     */
    protected String getTableFind(String name)
    {
        return String.format("SELECT * FROM %s WHERE 1=1 LIMIT 1" , name);
    }

    /**
     * 解析data数据
     * @param query QueryWrapper实例
     * @param data  解析得Map数据
     * @param isInsert 是否插入
     * @return Map
     */
    protected Map parseData(QueryWrapper query , Map data , boolean isInsert)
    {
        Map result = new LinkedHashMap();
        // 分析数据
        Connection connection = null;
        Statement st = null;
        ResultSet rs = null;
        try {
            connection = Configuration.getConnectionConfig().getConn();
            st = connection.createStatement();
            rs = st.executeQuery(getTableFind(parseTable(query)));
            ResultSetMetaData rsmd = rs.getMetaData();
            int len = rsmd.getColumnCount();

            for (int j = 1; j <= len; j++) {
                String col =rsmd.getColumnName(j);
                if (col.toLowerCase().equals("id")) continue;
                String type = rsmd.getColumnTypeName(j);
                if(data.containsKey(col)){
                    // 数据存在
                    Object content = data.get(col);
                    // 判断他的内容
                    if(content instanceof List){
                        List var = (List)content;
                        String v0 = var.get(0).toString().toLowerCase();
                        if(v0.equals("inc")){
                            bindData(var.get(1));
                            result.put(col , col +" + ?");
                        }else if(v0.equals("dec")){
                            bindData(var.get(1));
                            result.put(col , col +" - ?");
                        }
                    }else{
                        bindData(getFieldValue(type,String.valueOf(content)));
                        result.put(col , "?");
                    }
                    //result.put(col , getFieldValue(type , data.get(col)));
                }else{
                    // 插入的时候才将所有字段弄过去
                    if(isInsert){
                        bindData(getFieldDefault(type));
                        result.put(col , "?");
                    }
                }
            }
            rs.close();
            st.close();
        }catch (SQLException e){
            e.printStackTrace();
        }finally {
            DB.release(st , rs);
            if(connection != null){
                Configuration.getConnectionConfig().closeConn(connection);
            }
        }
        return result;
    }


    /**
     * 解析锁表
     * @param query QueryWrapper实例
     * @return String
     */
    protected String parseLock(QueryWrapper query)
    {
        String lock = (String) query.getOption().get("lock");
        if(lock == null){
            return "";
        }
        return lock;
    }

    /**
     * 解析获取的行数
     * @param query QueryWrapper实例
     * @return String
     */
    protected String parseLimit(QueryWrapper query)
    {
        HashMap limit = (HashMap) query.getOption().get("limit");
        if(limit == null || limit.isEmpty()){
            return "";
        }
        Long offset = (Long)limit.get("offset");
        Long pagesize  = (Long)limit.get("limit");
        if( offset == null ){
            bindData(pagesize);
            //return " LIMIT "+pagesize+" ";
            return " LIMIT ? ";
        }
        bindData(offset);
        bindData(pagesize);
        //return " LIMIT "+offset+","+pagesize+" ";
        return " LIMIT ?,? ";
    }

    /**
     * 解析字段
     * @param query QueryWrapper实例
     * @return String
     */
    protected String parseField(QueryWrapper query)
    {
        ArrayList list = (ArrayList) query.getOption().get("field");
        if(list == null || list.size() == 0){
            return "*";
        }
        return StringUtil.join("," , list);
    }

    /**
     * 解析是否强制使用索引
     * @param query QueryWrapper实例
     * @return String
     */
    protected String parseForce(QueryWrapper query)
    {
        ArrayList list = (ArrayList) query.getOption().get("force");
        if(list == null || list.size() == 0){
            return "";
        }
        return String.format(" FORCE INDEX ( %s ) " , StringUtil.join("," , list));
    }

    /**
     * 解析去重复
     * @param query QueryWrapper实例
     * @return String
     */
    protected String parseDistinct(QueryWrapper query)
    {
        if( query.getOption().containsKey("distinct") && Boolean.valueOf(query.getOption().get("distinct").toString()).booleanValue()){
            return " DISTINCT ";
        }
        return "";
    }

    /**
     * 解析Having
     * @param query QueryWrapper实例
     * @return String
     */
    protected String parseHaving(QueryWrapper query)
    {
        if(query.getOption().containsKey("having")){
            return " HAVING "+query.getOption().get("having");
        }
        return "";
    }

    /**
     * 获取DaoModel的某个属性
     * @param query QueryWrapper实例
     * @param key  键值
     * @return String
     */
    protected String getOptionValue(QueryWrapper query , String key)
    {
        String val = ( String )query.getOption().get(key);
        if(val == null){
            return "";
        }
        return val+" ";
    }

    /**
     * 解析表
     * @param query QueryWrapper实例
     * @return 返回解析后得表名称
     */
    public String parseTable(QueryWrapper query)
    {
        String name = query.getPrefix() + query.getName();
        ArrayList list = (ArrayList) query.getOption().get("table");
        if(list == null || list.size() == 0){
            return name+" "+ getOptionValue(query ,"alias");
        }
        if(!StringUtil.isNullOrEmpty(query.getPrefix()))
        {
            for (int i = 0; i < list.size(); i++) {
                list.set(i , query.getPrefix()+list.get(i));
            }
        }
        return StringUtil.join("," , list)+" ";
    }

    /**
     * 解析json 连接
     * @param query QueryWrapper实例
     * @return 解析后得数据
     */
    public String parseJoin(QueryWrapper query)
    {
        ArrayList list = (ArrayList) query.getOption().get("join");
        if(list == null || list.size() == 0){
            return "";
        }
        return " "+StringUtil.join(" " , list)+" ";
    }

    /**
     * 解析分组
     * @param query QueryWrapper实例
     * @return 解析后得分组信息
     */
    public String parseGroup(QueryWrapper query)
    {
        ArrayList orderList = (ArrayList) query.getOption().get("group");
        if(orderList == null || orderList.size() == 0){
            return "";
        }
        StringBuffer buffer = new StringBuffer(" GROUP BY ");
        buffer.append(StringUtil.join(",",orderList)).append(" ");
        return buffer.toString();
    }

    /**
     * 解析排序
     * @param query QueryWrapper实例
     * @return 解析得排序信息
     */
    public String parseOrder(QueryWrapper query)
    {
        ArrayList orderList = (ArrayList) query.getOption().get("order");
        if(orderList == null || orderList.size() == 0){
            return "";
        }
        StringBuffer buffer = new StringBuffer(" ORDER BY ");
        buffer.append(StringUtil.join(",",orderList)).append(" ");
        return buffer.toString();
    }


    /**
     * 解析条件
     * @param query QueryWrapper实例
     * @return 解析后得条件信息
     */
    public String parseWhere( QueryWrapper query )
    {
        ArrayList whereList = (ArrayList) query.getOption().get("where");
        if(whereList == null || whereList.size() == 0){
            return "";
        }
        StringBuffer buffer = new StringBuffer(" WHERE ");

        for(int i=0;i<whereList.size();i++)
        {
            HashMap map = (HashMap) whereList.get(i);
            if(i!=0){
                // 每一个的连接符
                buffer.append(" ");
                buffer.append(map.get("connect") == null ? " AND " : map.get("connect"));
                buffer.append(" ");
            }
            String where = (String) map.get("where");
            if(where != null){
                buffer.append(" ").append(where).append(" ");
            }else{
                String key = (String) map.get("name");
                String exp = (String) map.get("exp");
                Object val = map.get("value");

                if(-1 != key.indexOf("|")){
                    String[] keys = key.split("\\|");
                    buffer.append("(");
                    for(int j=0;j<keys.length;j++){
                        if(j!=0){
                            buffer.append(" OR ");
                        }
                        parseWhereItem(buffer , keys[j] , exp , val);
                    }
                    buffer.append(")");
                }else{
                    parseWhereItem(buffer , key , exp , val);
                }
            }
        }
        return buffer.toString();
    }

    /**
     * 格式化字符串
     * @param val 字符串
     * @return 加入了单引号得字符串
     */

    protected String formatString( String val)
    {
        return "'"+val.replace("'" , "\\'")+"'";
    }

    /**
     * 解析条件子语句
     * @param buffer 字符串缓冲区
     * @param key    字段名
     * @param exp    条件类型
     * @param val    条件值
     */
    protected void parseWhereItem(StringBuffer buffer , String key , String exp , Object val)
    {
        List<String> exps = Arrays.asList("eq,neq,lt,elt,gt,egt".split(","));
        int index = exps.indexOf(exp);
        String[] exps2 = "=,!=,<,<=,>,=>".split(",");
        if(index != -1){
            exp = exps2[index];
        }
        exp = exp.toLowerCase().trim();
        if(exp.equals("in") || exp.equals("not in")){
            List inArrayList = getParseWhereValueArray(val);

            buffer.append(" ");
            buffer.append(key);
            List<Object> sd = new ArrayList(inArrayList.size());
            buffer.append(" "+exp+"(");
            int i=0;
            for (Object data : inArrayList){
                bindData(data);
                if(i>0){
                    buffer.append(",");
                }
                sd.add("?");
                i++;
            }
            buffer.append(")");
        }else if(exp.equals("between") || exp.equals("not between")){
            buffer.append(" ").append(key).append(" ").append(exp).append(" ");
            if(val instanceof String){
                bindData(val);
                //buffer.append(val);
                buffer.append("?");
            }else{
                List str = getParseWhereValueArray(val);
                bindData(str.get(0));
                bindData(str.get(1));
                buffer.append( "?" ).append(" AND ").append( "?" );
            }
        }else{
            buffer.append(" "+key);
            buffer.append(" "+exp+" ");
            bindData(val);
            //buffer.append(formatString(String.valueOf( val)));
            buffer.append("?");
            buffer.append(" ");
        }
    }

    /**
     * 解析条件为数组 或者 list 的时候
     * @param val 数组得信息
     * @return 解析后得List 列表
     */
    protected List getParseWhereValueArray(Object val)
    {
        ArrayList inArrayList = new ArrayList();
        if(val instanceof List){
            return (List) val;
        }else if(val instanceof String || val instanceof String[]){
            String[] inList = val instanceof String ? ((String)val).split(",") : (String[]) val;
            for (int i=0;i<inList.length;i++){
                inArrayList.add(formatString(inList[i]));
            }
        }else if(val instanceof List) {
            for (int i=0;i<((List) val).size();i++)
            {
                inArrayList.add(formatString((String)((List) val).get(i)));
            }
        }else if(val instanceof int[]) {
            for (int i=0;i<((int[]) val).length;i++)
            {
                inArrayList.add(((int[]) val)[i]);
            }
        }else if(val instanceof float[]) {
            for (int i=0;i<((float[]) val).length;i++)
            {
                inArrayList.add(((float[]) val)[i]);
            }
        }else if(val instanceof double[]) {
            for (int i=0;i<((double[]) val).length;i++)
            {
                inArrayList.add(((double[]) val)[i]);
            }
        }else if(val instanceof long[]) {
            for (int i=0;i<((long[]) val).length;i++)
            {
                inArrayList.add(((long[]) val)[i]);
            }
        }else if(val instanceof Iterable)
        {
            Iterator it = ((Iterable)val).iterator();
            while(it.hasNext()){
                Object str = it.next();
                inArrayList.add(formatString(String.valueOf(str)));
            }
        } else if(val instanceof Map) {
            Map var = (Map) val;
            Iterator entries = var.entrySet().iterator();

            while (entries.hasNext()) {
                Map.Entry entry = (Map.Entry) entries.next();
                Object value = entry.getValue();
                inArrayList.add(formatString(String.valueOf(value)));
            }
        } else {
            System.err.println("not instanceof Type "+val.getClass().getName());
        }
        return inArrayList;
    }

    /**
     * 当前DaoModel 是否为分页
     * @return 是否是分页模式
     */
    public boolean isPage() {
        return isPage;
    }

    /**
     * 设置为分页
     * @param page 设置是否分页
     */
    public void setPage(boolean page) {
        isPage = page;
    }
}
