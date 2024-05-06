package org.example.Utils;

import com.sun.org.apache.bcel.internal.generic.RET;
import org.example.config.AppConfig;
import org.example.enums.DateTimePatternEnum;
import org.example.pojo.contants.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
//import java.awt.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *  JAVA 生成 缩略图的方法
 * */
@Component
public class ImageUtils {


    private static final Logger logger = LoggerFactory.getLogger(ImageUtils.class);
    @Autowired
    private AppConfig appConfig;


    /**
     * 缩略图
     * */
    public static Boolean createThumbnail(File file,int thumbnailWidth,int thumbnailHeight,File targetFile){
        try{
            BufferedImage src = ImageIO.read(file);
            // thumbnailWidth 缩略图的宽度   thumbnailHeight 缩略图的高度
           int sorceW = src.getWidth();
           int sorceH = src.getHeight();
           //小于 指定高宽不压缩
            if(sorceW<=thumbnailWidth){
                return false;
            }
            int height = sorceH;// 目标文件的高度
            if(sorceW>thumbnailWidth){ // 目标文件宽度大于指定宽度
                height = thumbnailWidth * sorceH /  sorceW;
            } else{
                //目标文件宽度小于指定宽度 那么缩略图大小更原图一样大
                thumbnailWidth = sorceW;
                height = sorceH;
            }
            //生成宽度为 150的缩略图
            BufferedImage dst = new BufferedImage(thumbnailWidth,height,BufferedImage.TYPE_INT_RGB);
            Image scaleImage = src.getScaledInstance(thumbnailWidth,height,Image.SCALE_SMOOTH);
            Graphics2D g = dst.createGraphics();
            g.drawImage(scaleImage,0,0,thumbnailWidth,height,null);
            g.dispose();

            int resultH = dst.getHeight();
            //高度过大的 ，裁剪图片
            if(resultH>thumbnailWidth){
                resultH = thumbnailHeight;
                dst = dst.getSubimage(0,0,thumbnailWidth,resultH);
            }

            ImageIO.write(dst,"JPEG",targetFile);
            return true;
        } catch (Exception e){

            e.printStackTrace();
        }
        return false;
    }

    /**
     * 图片替换
     * */
    public String resetImageHtml(String html) throws ParseException {


        // 按月份分目录
       String month = DateUtil.format(new Date(),
               DateTimePatternEnum.YYYYMM.getPattern());

        List<String> imageList =
                getImageList(html);
        for(String img : imageList){
            resetImage(img,month);
        }
        return month;
    }

    public String resetImage(String imagePath,String month) throws ParseException {

        if(StringTools.isEmpty(imagePath)||!imagePath.contains(Constants.FILE_FOLDER_TEMP2)){
                return imagePath;
        }
        imagePath  = imagePath
                .replace(Constants.READ_IMAGE_PATH,"");
        if(StringTools.isEmpty(month)){
            month = DateUtil.format(new Date(),DateTimePatternEnum.YYYYMM.getPattern());
        }
        // /api/file/getImage/temp/jsdhkjdsjghjdbsvg.jpg -> 202403/psdhjsjbdhsjn.jpg
        String imageFileName = month+"/"+imagePath.substring(imagePath.lastIndexOf("/")+1);
        File targetFile = new File(appConfig.getProjectFolder()+
                Constants.FILE_FOLDER_FILE+Constants.FILE_FOLDER_IMAGE+imageFileName);
        try {

            File tempFile = new File(appConfig.getProjectFolder()+
                    Constants.FILE_FOLDER_FILE+imagePath);
            org.apache.commons.io.FileUtils.copyFile(tempFile,targetFile);
            org.apache.commons.io.FileUtils.copyFile(new File(appConfig.getProjectFolder() +
            Constants.FILE_FOLDER_FILE + imagePath),targetFile);


        } catch (IOException e) {
            logger.error("复制图片失败",e);
            return imagePath;
        }

        return imageFileName;
    }

    public List<String> getImageList(String html){
        java.util.List<String> imageList = new ArrayList<>();
        String regEx_img = "(<img.*src\\s*=\\s*(.*?)[^>]*?)";
        Pattern p_image = Pattern.compile(regEx_img,Pattern.CASE_INSENSITIVE);
        Matcher m_image = p_image.matcher(html);
        while (m_image.find()){
             String img = m_image.group();
             Matcher m = Pattern
                     .compile("src\\s*=\\s*\"?(.*?)(\"|>|\\s+)")
                     .matcher(img);
             while (m.find()){
                 String imageUrl = m.group();
                 imageList.add(imageUrl);
             }
             return imageList;
        }


        return null;

    }
}
