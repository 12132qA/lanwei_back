package org.example.contorller;

import org.apache.commons.lang3.ArrayUtils;
import org.example.Utils.StringTools;
import org.example.annotation.GlobalInterceptor;
import org.example.config.WebConfig;
import org.example.enums.ResponseCodeEnum;
import org.example.enums.UserOperFrequencyTyoeEnum;
import org.example.exception.BusinessException;
import org.example.pojo.contants.Constants;
import org.example.vo.ResponseVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/file")
public class FileController {

    private static final Logger logger= LoggerFactory.getLogger(FileController.class);
    @Autowired
    private WebConfig webConfig;
    /**
     *
     * @param file
     * @return
     */

    @RequestMapping("/uploadImage")
    @GlobalInterceptor(checkLogin = true,frequencyType = UserOperFrequencyTyoeEnum.IMAGE_UPLOAD)
    public ResponseVO uploadImage(MultipartFile file){
         // 文件未上传
        if(file == null){
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }

       String fileName = file.getOriginalFilename();
         //文件的扩展名
       String fileExtName = StringTools.getFileSuffix(fileName);

       if(ArrayUtils.contains(Constants.IMAGE_SUFFIX,fileName)){
           throw new BusinessException(ResponseCodeEnum.CODE_600);
       }
        String path = copyFile(file);
        Map<String,String> fileMap = new HashMap<>();
        fileMap.put("fileName",path);
        return ResponseVO.getSuccessResponseVO(fileMap);
    }

    private String copyFile(MultipartFile file){

        try {
            String fileName = file.getOriginalFilename();
            String fileExtName = StringTools.getFileSuffix(fileName);
            String fileRealName = StringTools.getRandomString(Constants.LENGTH_15) +fileExtName;
            String folderPath = webConfig.getProjectFolder()+ Constants.FILE_FOLDER_FILE+ Constants.FILE_FOLDER_TEMP;
            File folder= new File(folderPath);
            if(!folder.exists()){
                folder.mkdirs();
            }

            File uploadFile = new File(folderPath+"/"+fileRealName);
            file.transferTo(uploadFile);

            return Constants.FILE_FOLDER_TEMP2 + "/" +fileRealName;

        }catch (Exception e){
            logger.error("上传文件失败",e);
            throw  new BusinessException("上传文件失败");
        }
    }

    @RequestMapping("/getImage/{imageFolder}/{imageName}")
    public void getImage(HttpServletResponse response,
                          @PathVariable("imageFolder") String imageFolder,
                          @PathVariable("imageName") String imageName){

        readImage(response,imageFolder,imageName);
         return;
    }

    /**
     *
     * @param response
     * @param userId
     */

    @RequestMapping("/getAvatar/{userId}")
    public void getImage(HttpServletResponse response,
                         @PathVariable("userId") String userId){

        String avatarFolderName = Constants.FILE_FOLDER_FILE+ Constants.FILE_FOLDER_AVATAR_NAME;
        String avatarPath = webConfig.getProjectFolder()+avatarFolderName+userId+Constants.AVATAR_SUFFIX;
        File avatarFolder = new File(avatarFolderName);

        if(!avatarFolder.exists()){
            avatarFolder.mkdirs();
        }
        File file = new File(avatarPath);
        String imageName = userId+Constants.AVATAR_SUFFIX;
// D:\spring-0001\LanWei-java\LanWei-web\src\main\resources\file\images\202301\avatar\7437465925.jpg
// D:\spring-0001\LanWei-java\LanWei-web\src\main\resources\file\avatar\7437465925.jpg
        if(!file.exists()){
            imageName = userId+ Constants.AVATAR_SUFFIX;
        }

        readImage(response,Constants.FILE_FOLDER_AVATAR_NAME,imageName);

    }

    /***
     * 读文件操作
     */

    public String readImage(HttpServletResponse response,
                          String imageFolder,String imageName){
        ServletOutputStream sos = null;
        FileInputStream in = null;
        ByteArrayOutputStream baos = null;
        try{
            if(StringTools.isEmpty(imageFolder)||StringTools.isBlank(imageName)){
                return "NO";
            }
            String imageSuffix = StringTools.getFileSuffix(imageName);
            String filePath =
                    webConfig.getProjectFolder()
                    + Constants.FILE_FOLDER_FILE ;// + Constants.FILE_FOLDER_IMAGE 去除

            if(imageFolder.startsWith("2023")){
                filePath+= Constants.FILE_FOLDER_IMAGE+'/';
            }

            filePath+= imageFolder+'/'+ imageName;
            if(Constants.FILE_FOLDER_TEMP2.equals(imageFolder)){
                filePath = webConfig.getProjectFolder()+
                        Constants.FILE_FOLDER_FILE+imageFolder+imageName;

            }
//            else if(imageFolder.contains(Constants.FILE_FOLDER_AVATAR_NAME)){
//                filePath = webConfig.getProjectFolder()+Constants.FILE_FOLDER_FILE
//                        +imageFolder+imageName;
//            }
            // D:\spring-0001\LanWei-java\LanWei-web\src\main\resources\file\avatar\7437465925.jpg
            File file = new File(filePath);
            if(!file.exists()){
                return "NO";
            }
            imageSuffix = imageSuffix.replace(".","");
            if(!Constants.FILE_FOLDER_AVATAR_NAME.equals(imageFolder)){
                response.setHeader("Cache-Control","max-age=2592000");
            }
            response.setContentType("image/"+imageSuffix);
            in = new FileInputStream(file);
            sos = response.getOutputStream();
            baos = new ByteArrayOutputStream();
            int ch = 0;
            while(-1!=(ch=in.read())){
                baos.write(ch);
            }
            sos.write(baos.toByteArray());

        }catch (Exception e){
            logger.error("读取图片异常",e);

        }finally {
            if(baos!=null){
                try{
                    baos.close();
                } catch (IOException e) {
                    logger.error("IO异常!!",e);
                    e.printStackTrace();
                }
            }
            if(baos!=null){
                try{
                    baos.close();
                } catch (IOException e) {
                    logger.error("IO异常!!",e);
                    e.printStackTrace();
                }
            }
            if(baos!=null){
                try{
                    baos.close();
                } catch (IOException e) {
                    logger.error("IO异常!!",e);
                    e.printStackTrace();
                }
            }

        }
        return "NO";
    }

}