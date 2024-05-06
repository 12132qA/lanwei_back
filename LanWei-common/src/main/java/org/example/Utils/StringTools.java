package org.example.Utils;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;
//import org.apache.commons.codec.digest.DigestUtils;

public class StringTools {
    public static Boolean isEmpty(String str){
                   //      去空格
        if(null == str || "".equals(str.trim()) || "null".equals(str) ){
            return true;
        }else{
            return false;
        }
    }

    public static String getRandomString(Integer count){
        return RandomStringUtils.random(count,true,true);

    }

    public static String getRandomNumber(Integer count){
        return RandomStringUtils.random(count,false,true);

    }

    public static String encodeM5(String sourceStr){
        return StringTools.isEmpty(sourceStr)?null: DigestUtils.md2Hex(sourceStr);
    }

    public static String getFileSuffix(String fileName){
        return fileName.substring(fileName.lastIndexOf("."));
    }

    public static boolean isBlank(String imageName) {
        return imageName.isEmpty() || imageName.equals(" ");
    }
    public static String escapeHtml(String content) {
        if(StringTools.isEmpty(content)){
            return content;
        }

        content = content.replace("<","&lt;");
        content = content.replace(" ", "nbsp");
        content = content.replace("\n", "<br>");


        return content;
    }

    /**
     *
     * 获取 名字
     * */

    public static String getFileName(String  fileName){
         return  fileName.substring(fileName.lastIndexOf("."));
    }



}
