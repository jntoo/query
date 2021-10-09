package com.jntoo.db;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.jntoo.db.annotation.FieldType;
import com.jntoo.db.annotation.Fields;
import com.jntoo.db.model.FieldInfoModel;
import com.jntoo.db.model.QMap;
import com.jntoo.db.model.TableModel;
import com.jntoo.db.utils.StringUtil;
import com.jntoo.db.utils.TableManageUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

public class DB {

    /**
     * 自动带前缀得表信息
     *
     * @param name 无前缀表
     * @return 返回QueryMap 实例
     */
    static public QueryMap name(String name) {
        return new QueryMap(name);
    }

    /**
     * 无前缀设置
     *
     * @param table 完整得表名称
     * @return 返回QueryMap 实例
     */
    public static QueryMap table(String table) {
        QueryMap queryMap = new QueryMap(table);
        queryMap.setPrefix("");
        return queryMap;
    }

    /**
     * 根据Class 创建表实例
     *
     * @param cls 实体类得对象Class
     * @return 操作实体对象得值
     */
    public static <T> QueryWrapper<T> name(Class<T> cls) {
        return new QueryWrapper(cls);
    }



    /**
     * 根据实体对象操作
     *
     * @param cls 实体对象得实例
     * @param <T> 实体对象
     * @return 操作实体对象得值
     */
    public static <T> QueryWrapper<T> name(T cls) {
        return new QueryWrapper(cls);
    }

    /**
     * 查询数据返回 List-QMap 数据
     * @param sql sql 语句
     * @param binds 绑定得值
     * @return List-QMap 数据
     */
    public static List select(String sql , Object... binds)
    {
        return select(sql , Map.class , binds);
    }

    public static Map find(String sql , Object... binds)
    {
        return find(sql , Map.class , binds);
    }

    /**
     * 执行更新语句
     *
     * @param sql      更新得sql
     * @param bindData 绑定得值
     * @return 更新行数
     */
    public static int execute(String sql, Object... bindData) {
        return executeUpdate(sql , bindData);
    }

    /**
     * 执行更新语句
     *
     * @param sql      更新得sql
     * @param bindData 绑定得值
     * @return 更新行数
     */
    public static int executeUpdate(String sql, Object... bindData) {
        PreparedStatement rs = null;
        int id = -1;
        Connection conn = Configuration.getConnection();
        try {
            rs = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            setBindData(rs, bindData);
            id = rs.executeUpdate();
            log(rs.toString() );
            log(sql, bindData);
        } catch (SQLException e) {
            log(e, sql,bindData);
        } finally {
            DB.release(rs, null);
            Configuration.closeConnection(conn);
        }
        return id;
    }

    /*public static void closeConn(Connection connection)
    {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }*/

    /**
     * 执行插入语句
     *
     * @param sql      执行插入语句
     * @param bindData 绑定得值
     * @return 当前实例
     */
    public static int executeInsert(String sql, Object... bindData) {
        PreparedStatement rs = null;
        ResultSet rsKey = null;
        int id = -1;
        Connection conn = Configuration.getConnection();
        try {

            rs = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            setBindData(rs, bindData);
            rs.executeUpdate();
            rsKey = rs.getGeneratedKeys();
            rsKey.next();
            id = rsKey.getInt(1);

            log(rs.toString());
            DB.log(sql, bindData);

        } catch (SQLException e) {
            DB.log(e, sql,bindData);
        } finally {
            DB.release(rs, rsKey);
            Configuration.closeConnection(conn);
        }
        return id;
    }


    /**
     * 根据sql 语句查询一行数据
     * @param sql sql 语句
     * @param cls 实体类Class
     * @param binds 绑定得值
     * @return 实体值
     */
    public static<T> T find(String sql, Class<T> cls, Object... binds) {
        Connection conn = Configuration.getConnection();
        T data = null;
        PreparedStatement statement = null;
        ResultSet rs = null;
        try {
            statement = conn.prepareStatement(sql);
            if (binds.length > 0) {
                setBindData(statement, binds);
            }
            rs = statement.executeQuery();
            data = fetchEntity(rs, cls);

            log(statement.toString() );
            log(sql , binds);
        } catch (SQLException e) {
            log(e , sql,binds);
            e.printStackTrace();
        } finally {
            release(statement , rs);
            Configuration.closeConnection(conn);
        }
        return data;
    }

    /**
     * 根据sql 语句查询多行数据
     * @param sql sql 语句
     * @param cls 实体类对象
     * @param binds 数据库绑定得值
     * @return 实体类得列表
     */
    public static<T> List select(String sql, Class<T> cls, Object... binds) {
        Connection conn = Configuration.getConnection();
        List list = new ArrayList();
        PreparedStatement statement = null;
        ResultSet rs = null;
        try {
            statement = conn.prepareStatement(sql);
            if (binds.length > 0) {
                setBindData(statement, binds);
            }
            rs = statement.executeQuery();
            T data = null;
            while ((data = fetchEntity(rs, cls)) != null) {
                list.add(data);
            }
            log(statement.toString());
            log(sql, binds);
        } catch (SQLException e) {
            e.printStackTrace();
            log(e , sql,binds);
        } finally {
            release(statement , rs);
            Configuration.closeConnection(conn);
        }
        return list;
    }


    static public void release(Statement st, ResultSet rs) {
        try {
            if (rs != null) {
                rs.close();
            }
            if (st != null) {
                st.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 输出调试信息
     *
     * @param message 调试信息
     * @param data 数据行
     */
    static public void log(String message , Object... data) {
        if (Configuration.isDebug()) {
            // 输出日期
            System.out.print(date("yyyy-MM-dd HH:mm:ss  "));
            System.out.println(message);
            if(data.length > 0){
                List datas = new ArrayList();
                for (int i = 0; i < data.length; i++) {
                    System.out.print(String.format("(%s) %s ",data.getClass().getSimpleName() , String.valueOf(data[i])));
                }
                System.out.println("");
            }
        }
    }

    /**
     * 输出调试信息2
     *
     * @param message1 消息1
     * @param message2 消息2
     */
    static public void log(String message1, Object message2) {
        if (Configuration.isDebug()) {
            // 输出日期
            System.out.print(date("yyyy-MM-dd HH:mm:ss  "));
            System.out.println("------ " + message1);
            if (message2 instanceof Collection) {
                System.out.println("------ " + StringUtil.join(" , ", message2));
            } else {
                System.out.println("------ " + message2);
            }
        }
    }

    /**
     * 获取当前日期
     *
     * @param format 日期得格式
     * @return 日期信息
     */
    static public String date(String format) {
        try {//yyyyMMddHHmmss
            SimpleDateFormat formatter = new SimpleDateFormat(format);
            Date currentTime_1 = new Date();
            return formatter.format(currentTime_1);
        } catch (Exception e) {
        }
        return "";
    }

    /**
     * 调试输出sql错误信息
     *
     * @param e   错误信息
     * @param sql 附带得sql 语句
     */
    static public void log(SQLException e, String sql , Object[] data) {
        int code = e.getErrorCode();
        String message = e.getMessage();

        String errorMessage = String.format("SQL execute Error Code: %d data Count: %d sql: \n%s\nMessage:%s", code, data.length ,  sql, message);

        if(data.length > 0){
            errorMessage += "\n";
            for (int i = 0; i < data.length; i++) {
                errorMessage += "---?"+i+"="+data[i];
            }
        }
        System.err.println(errorMessage);
    }

    public static <T> T fetchEntity(ResultSet rs, Class<T> table) throws SQLException {
        if (Map.class.isAssignableFrom(table)) {
            return (T)fetchMap(rs);
        }
        TableModel model = TableManageUtils.getTable(table);
        return fetchEntity(rs, model);
    }

    public static <T> T fetchEntity(ResultSet rs, TableModel tableModel) throws SQLException {
        if (rs == null) {
            return null;
        }
        T data = (T) getInstance(tableModel.getEntity());
        try {
            if (rs.next()) {
                List<FieldInfoModel> fields = tableModel.getFieldInfos(); // data.getClass().getDeclaredFields();
                for (FieldInfoModel infoModel : fields) {
                    try {
                        Field field = infoModel.getField();
                        //FieldInfoModel infoModel = tableModel.fieldInfo.get(field.getName());
                        if (infoModel == null) continue;
                        try {
                            rs.getObject(infoModel.getName());
                        }catch (SQLException e){
                            continue;
                        }

                        Object result = infoModel.getGetMethod().invoke(rs, infoModel.getName());
                        Fields annField = infoModel.getAnnField();
                        Class<?> type = field.getType();
                        Method method = infoModel.getSetMethod();

                        if (annField != null && annField.type() == FieldType.JSON) {
                            try {
                                String jsonData = String.valueOf(result);
                                // JSON 数据格式
                                if (Map.class.isAssignableFrom(type)) {
                                    Map map = JSON.parseObject(jsonData);
                                    method.invoke(data, map);
                                } else if (Collection.class.isAssignableFrom(type)) {
                                    Collection collection = JSON.parseArray(jsonData);
                                    method.invoke(data, collection);
                                } else {
                                    method.invoke(data, JSON.parseObject(jsonData, type));
                                }
                            } catch (JSONException e) {

                            }
                        } else {
                            method.invoke(data, result);
                        }
                    } catch (Exception sql) {
                        sql.printStackTrace();
                    }
                }
                return data;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Map fetchMap(ResultSet rs) throws SQLException {
        if (rs == null) return null;
        if (rs.next()) {
            QMap data = new QMap();
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();
            for (int i = 1; i <= columnCount; i++) {
                String name = rsmd.getColumnName(i);
                String value = rs.getString(i);
                if (value == null || value.toLowerCase().equals("null")) {
                    value = "";
                }
                data.put(name, value);
            }
            return data;
        } else {
            return null;
        }
    }

    protected static <T> T getInstance(Class<T> superClass) {
        try {
            if (Map.class.isAssignableFrom(superClass)) {
                return (T) new QMap();
            } else {
                return superClass.newInstance();
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }




    private static void setBindData(PreparedStatement statement, Object[] bindData) throws SQLException {
        int index = 1;
        for (int i = 0; i < bindData.length; i++) {
            Object data = bindData[i];
            if (data instanceof Integer) {
                statement.setInt(index, (Integer) data);
            } else if (data instanceof Float) {
                statement.setFloat(index, (Float) data);
            } else if (data instanceof Long) {
                statement.setLong(index, (Long) data);
            } else if (data instanceof Double) {
                statement.setDouble(index, (Double) data);
            } else if (data instanceof String) {
                statement.setString(index, (String) data);
            } else {
                statement.setObject(index, data);
            }
            index++;

        }
    }

}
