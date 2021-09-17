package com.jntoo.db.utils;

import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class StringUtil {

    public static final char UNDERLINE = '_';

    /**
     * 判断字符串是否为null或者空字符串
     * @param obj 对象
     * @return 是否kong
     */
    public static boolean isNullOrEmpty( Object obj )
    {
        if(obj == null){
            return true;
        }
        return "".equals(obj);
    }

    /**
     * 将第一个字母变为大写
     * @param str 字母
     * @return 改变后得字符串
     */
    public static String firstCharUpper(String str)
    {
        if(isNullOrEmpty(str))return str;
        return str.substring(0,1).toUpperCase()+str.substring(1);
    }

    /**
     * 驼峰转下划线
     * @param param 值
     * @param charType 是否大写写
     * @return 当前值
     */
    public static String camelToUnderline(String param, Integer charType) {
        if (param == null || "".equals(param.trim())) {
            return "";
        }
        int len = param.length();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            char c = param.charAt(i);
            if (Character.isUpperCase(c) && i > 0) {
                sb.append(UNDERLINE);
            }
            if (charType == 2) {
                sb.append(Character.toUpperCase(c));  //统一都转大写
            } else {
                sb.append(Character.toLowerCase(c));  //统一都转小写
            }
        }
        return sb.toString();
    }


    /**
     * 下划线转驼峰
     * @param param 值
     * @return 转换后得值
     */
    public static String underlineToCamel(String param) {
        if (param == null || "".equals(param.trim())) {
            return "";
        }
        int len = param.length();
        StringBuilder sb = new StringBuilder(len);
        Boolean flag = false; // "_" 后转大写标志,默认字符前面没有"_"
        for (int i = 0; i < len; i++) {
            char c = param.charAt(i);
            if (c == UNDERLINE) {
                flag = true;
                continue;   //标志设置为true,跳过
            } else {
                if (flag == true) {
                    //表示当前字符前面是"_" ,当前字符转大写
                    sb.append(Character.toUpperCase(param.charAt(i)));
                    flag = false;  //重置标识
                } else {
                    sb.append(Character.toLowerCase(param.charAt(i)));
                }
            }
        }
        return sb.toString();
    }



    /**
     * 将数组或者List 转化为 按des 隔开的字符串
     * @param des 分割值
     * @param list 列表
     * @return 处理号得值
     */
    public static String join(String des , Object list){
        StringBuffer buffer = new StringBuffer();

        if(list instanceof int[]) {
            int[] var = (int[]) list;
            for(int i=0;i<var.length;i++){
                if(i>0){
                    buffer.append(des);
                }
                buffer.append(var[i]);
            }
        }else if(list instanceof long[]) {
            long[] var = (long[]) list;
            for(int i=0;i<var.length;i++){
                if(i>0){
                    buffer.append(des);
                }
                buffer.append(var[i]);
            }
        }else if(list instanceof double[]) {
            double[] var = (double[]) list;
            for(int i=0;i<var.length;i++){
                if(i>0){
                    buffer.append(des);
                }
                buffer.append(var[i]);
            }
        }else if(list instanceof float[]) {
            float[] var = (float[]) list;
            for(int i=0;i<var.length;i++){
                if(i>0){
                    buffer.append(des);
                }
                buffer.append(var[i]);
            }
        }else if(list instanceof String[]) {
            String[] var = (String[]) list;
            for(int i=0;i<var.length;i++){
                if(i>0){
                    buffer.append(des);
                }
                buffer.append(var[i]);
            }
        }else if(list instanceof boolean[]) {
            boolean[] var = (boolean[]) list;
            for(int i=0;i<var.length;i++){
                if(i>0){
                    buffer.append(des);
                }
                buffer.append(var[i]?"true" : "false");
            }
        }else if(list instanceof List){
            List var = (List) list;
            for(int i=0;i<var.size();i++){
                if(i>0){
                    buffer.append(des);
                }
                buffer.append(var.get(i));
            }
        }else if(list instanceof Map){
            Map var = (Map) list;
            Iterator entries = var.entrySet().iterator();
            int i=0;
            while (entries.hasNext()) {
                if(i>0){
                    buffer.append(des);
                }
                Map.Entry entry = (Map.Entry) entries.next();
                Object value = entry.getValue();
                buffer.append(value);
                i++;
            }
        }else if(list instanceof Iterable)
        {
            Iterator it = ((Iterable)list).iterator();
            int i=0;
            while(it.hasNext()){
                if(i > 0) buffer.append(des);
                Object str = it.next();
                buffer.append(str);
                i++;
            }
        }
        return buffer.toString();
    }

    protected String parseName(String name) {
        String c = toLineString(name);
        return c.substring(0, 1).toUpperCase() + c.substring(1);
    }


    public static String toLineString(String string) {

        StringBuilder stringBuilder = new StringBuilder();
        String[] str = string.split("_");
        for (String string2 : str) {
            if (stringBuilder.length() == 0) {
                stringBuilder.append(string2);
            } else {
                stringBuilder.append(string2.substring(0, 1).toUpperCase());
                stringBuilder.append(string2.substring(1));
            }
        }
        return stringBuilder.toString();
    }
}
