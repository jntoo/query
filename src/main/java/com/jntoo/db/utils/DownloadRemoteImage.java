package com.jntoo.db.utils;


import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 下载远程代码
 */
public class DownloadRemoteImage {
    // 字符编码
    private static final String ECODING = "UTF-8";
    // 获取图片的正则表达式
    private static final String IMGURL_REG = "<img.*src=(.*?)[^>]*?>";
    // 判断是否为远程图片的正则表达式
    private static final String IMGSRC_REG = "(http|https):\"?(.*?)(\"|>|\\s+)";

    private String savePath = "";

    /**
     * 根据编辑器内容获取远程的图片并保存在本地,并替换content 中的内容为本地连接地址
     * @param content 富文本编辑器上的内容
     * @param savePath 保存路径
     * @param prePath 替换的前台路径
     * @return 处理内容后的html 内容
     */
    public static String run(String content , String savePath , String prePath)
    {
        DownloadRemoteImage image = new DownloadRemoteImage();
        image.savePath = savePath;
        List<String> imgUrl = image.getImageUrl(content);
        String text = image.getImageSrc(content , imgUrl , prePath);
        return text;
    }


    /***
     * 获取ImageUrl地址
     *
     * @param HTML
     * @return
     */
    private List<String> getImageUrl(String HTML) {
        Matcher matcher = Pattern.compile(IMGURL_REG).matcher(HTML);
        List<String> listImgUrl = new ArrayList();
        while (matcher.find()) {
            listImgUrl.add(matcher.group());
        }
        return listImgUrl;
    }

    /***
     * 获取ImageSrc地址
     *
     * @param listImageUrl
     * @return
     */
    private String getImageSrc(String content , List<String> listImageUrl , String path) {
        //List<String> listImgSrc = new ArrayList<String>();

        for (String image : listImageUrl) {
            Matcher matcher = Pattern.compile(IMGSRC_REG).matcher(image);
            while (matcher.find()) {
                String src = matcher.group().substring(0, matcher.group().length() - 1);
                String newsFile = download(src , path);
                if(newsFile!= null){
                    content = content.replace(src , newsFile);
                }
            }
        }
        return content;
    }

    private static long downloadIndex = 1;

    /**
     * 根据url 生成保存的文件名
     * @param url
     * @return
     */
    private String getFileName( String url )
    {
        //URL u = new URL(url);
        String ext = "png";
        String filename = new Date().getTime()+downloadIndex + "."+ext;
        downloadIndex++;
        return filename;
    }

    /**
     * 下载远程图片
     * @param url
     * @param path
     * @return
     */
    private String download(String url, String path) {
        String result = "";
        String imageName = "";
        try {
            if(url.indexOf("http")>=0){
                imageName = getFileName( url ); //url.substring(url.lastIndexOf("/") + 1, url.length());
                URL uri = new URL(url);

                URLConnection conn = uri.openConnection();
                String referer = url;
                if(url.indexOf("baidu.com") != -1){
                    referer = "https://www.baidu.com/s?ie=utf-8&wd=commons-fileupload";
                }

                conn.setRequestProperty("Referer" , referer);
                conn.setRequestProperty("User-Agent" , "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.102 Safari/537.36");
                conn.setDoInput(true);

                InputStream in = conn.getInputStream();

                //HttpServletRequest request = Request.getRequest();
                String paths = new File(savePath + path).getCanonicalPath(); //request.getSession().getServletContext().getRealPath(path);

                FileOutputStream fo = new FileOutputStream(new File(paths,imageName));
                byte[] buf = new byte[1024];
                int length = 0;
                System.out.println("开始下载:" + url);
                while ((length = in.read(buf, 0, buf.length)) != -1) {
                    fo.write(buf, 0, length);
                }
                in.close();
                fo.close();
                System.out.println(path+"/"+imageName + "下载完成");
            }
        } catch (Exception e) {
            System.out.println("下载失败");
            return null;
        }
        return path+"/"+imageName;
    }


}
