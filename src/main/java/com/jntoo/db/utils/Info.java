package com.jntoo.db.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jntoo.db.QueryMap;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Info {
    /**
     * 获取json 数据中的address 字段
     * @param addre JSON 结构得字符串
     * @return 返回解析后得地址
     */
    public static String address(Object addre)
    {
        String add = addre.toString();
        if(add == null || add.length() == 0){
            return "";
        }
        JSONObject json =  JSONObject.parseObject(add);
        if(json != null && !json.isEmpty()){
            return json.getString("address");
        }
        return "";
    }
    /**
     * url 编码，中文要进行编码输出
     * @param str url 地址
     * @return 编码后得URL路径
     */
    public static String urlencode(Object str)
    {
        try{
            return java.net.URLEncoder.encode(String.valueOf(str), "UTF-8");
        }catch (Exception e){
            e.printStackTrace();
        }
        return str.toString();
    }

    /**
     * url 解码
     * @param str 待解码得URL 地址
     * @return  解码后得地址
     */
    public static String urldecode(Object str)
    {
        try{
            return java.net.URLDecoder.decode(String.valueOf(str), "UTF-8");
        }catch (Exception e){
            e.printStackTrace();
        }
        return str.toString();
    }

    public static String jsonEncode(Object source)
    {
        return JSON.toJSONString(source);
    }

    public static JSONObject jsonDecode(Object source)
    {
        if(source == null){
            return JSON.parseObject("{}");
        }

        return JSON.parseObject( String.valueOf( source ));
    }

    public static JSONArray jsonDecodeArray(Object source )
    {
        if(source == null){
            return JSON.parseArray("[]");
        }
        return JSON.parseArray( String.valueOf( source ));
    }

    public static List objectSplit(String exp , Object str)
    {
        List arr = new ArrayList();
        if(str == null)
        {
            return arr;
        }
        String s = String.valueOf(str);
        String[] sp = s.split(exp);
        return Arrays.asList(sp);
    }

    /**
     * 获取所有子集下的id
     * @param table 表名
     * @param pid   父级字段
     * @param value 获取的所有子集
     * @return 子集id
     */
    public static String getAllChild( String table , String pid , Object value)
    {
        List templists = DB.name(table).select();
        return StringUtil.join(",",getAllChild( table ,  pid , value , templists));
    }

    /**
     * 获取所有子集下的id
     * @param table 表名
     * @param pid   父级字段
     * @param value 获取的所有子集
     * @return
     */
    public static List getAllChild( String table , String pid , Object value , List templists)
    {
        List $ret = null;
        List<HashMap> lists = templists;
        List $result = new ArrayList();

        String parentid = String.valueOf(value);
        $result.add(parentid);
        for (HashMap child : lists){
            if(child.get(pid).equals(parentid))
            {
                $ret = getAllChild( table , pid , child.get("id") , templists );
                if($ret.size() > 0){
                    $result.addAll($ret);
                }
            }
        }
        return $result;
    }
    public static String postion(String table , String pid , String name , String value)
    {
        List items = new ArrayList();
        String parentid = value;
        do {
            Map mp = DB.name(table).find(parentid);
            if(mp == null || mp.isEmpty()){
                break;
            }
            items.add(mp.get(name));
            parentid = mp.get(pid).toString();
        }while ( !parentid.equals("") && !parentid.equals("0") );
        Collections.reverse(items);
        return StringUtil.join(" ",items);
    }

    public static String getTreeOption(String table , String pid , String name , Object value)
    {
        return postion(table , pid , name , String.valueOf(value));
    }

    /**
     * 获取唯一id，生成随机编号
     * @return 字符串
     */
    public synchronized static String getID() {

        Random random = new Random();
        StringBuffer ret = new StringBuffer(20);
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("MMddHHmmss", Locale.CHINA);
        ret.append(format.format(date));
        String rand = String.valueOf(Math.abs(random.nextInt()));
        //ret.append(getDateStr());
        ret.append(rand.substring(0, 4));

        return ret.toString();
    }

    /**
     * 字符串截取，先把html 标签去除
     * @param source  源字符串
     * @param length  截取长度
     * @return 截取后得字符串
     */
    public static String subStr(Object source, int length) {
        return subStr(source , length , "...");
    }

    /**
     * 字符串截取，先把html 标签去除
     * @param source 源字符串
     * @param length 截取长度
     * @return 截取后得字符串
     */
    public static String subStr(Object source, int length , String append) {
        if(source == null) return "";
        String str = delHTMLTag(source);

        if (str.length() > length) {
            str = ( str.substring(0, length)) + append;
        }
        return str;
    }

    /**
     * 删除html标签
     * @param htmlStrParam 待删除得HTML 字符串
     * @return 格式化后得字符串
     */
    public static String delHTMLTag(Object htmlStrParam) {

        String htmlStr = String.valueOf(htmlStrParam);
        String regEx_script = "<script[^>]*?>[\\s\\S]*?<\\/script>"; //定义script的正则表达式
        String regEx_style = "<style[^>]*?>[\\s\\S]*?<\\/style>"; //定义style的正则表达式
        String regEx_html = "<[^>]+>"; //定义HTML标签的正则表达式

        Pattern p_script = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);
        Matcher m_script = p_script.matcher(htmlStr);
        htmlStr = m_script.replaceAll(""); //过滤script标签

        Pattern p_style = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);
        Matcher m_style = p_style.matcher(htmlStr);
        htmlStr = m_style.replaceAll(""); //过滤style标签

        Pattern p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
        Matcher m_html = p_html.matcher(htmlStr);
        htmlStr = m_html.replaceAll(""); //过滤html标签

        return htmlStr.trim(); //返回文本字符串
    }

    /**
     * 比较时间大小
     */
    public static long compare_datetime(String DATE1, String DATE2) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date dt1 = df.parse(DATE1);
            Date dt2 = df.parse(DATE2);
            return dt1.getTime()-dt2.getTime();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return -1;
    }

    /**
     * 将HTML 标签格式化
     * @param source 源代码
     * @return 格式化后得字符串
     */

    public static String html(Object source) {
        if(source == null){
            return "";
        }
        return html(source.toString());
    }
    /**
     * 将HTML 标签格式化
     * @param source 源代码
     * @return 格式化后得字符串
     */
    public static String html(String source) {
        if (source == null) {
            return "";
        }
        String html = "";
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < source.length(); i++) {
            char c = source.charAt(i);
            switch (c) {
                case '<':
                    buffer.append("&lt;");
                    break;
                case '>':
                    buffer.append("&gt;");
                    break;
                case '&':
                    buffer.append("&amp;");
                    break;
                case '"':
                    buffer.append("&quot;");
                    break;
                default:
                    buffer.append(c);
            }
        }
        html = buffer.toString();
        return html;
    }


    /**
     * 多图只取第一张图片
     * @param nImages 图片列表
     * @return 第一张图片
     */
    public static String images(Object nImages)
    {
        String str = nImages == null ? "" : nImages.toString();
        if(str.indexOf(",")>=0){
            String[] li = str.split(",");
            return li[0];
        }
        return str;
    }

}
