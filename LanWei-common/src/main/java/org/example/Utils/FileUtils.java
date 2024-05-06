package org.example.Utils;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.example.config.AppConfig;
import org.example.dto.FileUploadDto;
import org.example.enums.DateTimePatternEnum;
import org.example.enums.FileUploadTypeEnum;
import org.example.exception.BusinessException;
import org.example.pojo.contants.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;

@Component
public class FileUtils {

    private static final Logger logger = LoggerFactory.getLogger(FileUtils.class);

     @Autowired
    private AppConfig appConfig;

//     @Autowired
//    private FileUploadDto fileUploadDto;

    @Autowired
    private ImageUtils imageUtils;

    // 上传到本地路径
    public FileUploadDto uploadFile2Local(MultipartFile file, String folder,
                                         FileUploadTypeEnum uploadTypeEnum){
        try{
            FileUploadDto uploadDto = new FileUploadDto();
            String originalFileName = file.getOriginalFilename();
            String fileSuffix = StringTools.getFileSuffix(originalFileName);

            if(originalFileName.length()> Constants.LENGTH_200){
                originalFileName = StringTools.getFileName(originalFileName)
                        .substring(0,Constants.LENGTH_190)+fileSuffix;

            }

            if(!ArrayUtils.contains(uploadTypeEnum.getSuffixArray(),fileSuffix)){
               throw new BusinessException("文件类型不正确");
            }


            String month = DateUtil.format(new Date(), DateTimePatternEnum.YYYYMM.getPattern());
           String baseFolder = appConfig.getProjectFolder()+Constants.FILE_FOLDER_FILE;
            File targetFileFolder = new File(baseFolder+folder+month+"/");

            String fileName = StringTools.getRandomString(Constants.LENGTH_15)+fileSuffix;

            File targetFile = new File(targetFileFolder.getParent()+"/"+fileName);

            String localPath = month+"/"+fileName;

            if(uploadTypeEnum == FileUploadTypeEnum.AVATAR){
                // @TODO 头像上传
                targetFile = new File(baseFolder+Constants.FILE_FOLDER_AVATAR_NAME);
                targetFile = new File(targetFileFolder.getPath()+"/"+folder+Constants.AVATAR_SUFFIX);
                localPath = folder+Constants.AVATAR_SUFFIX;
            }
            if(!targetFileFolder.exists()){
                targetFileFolder.mkdirs();
            }

            file.transferTo(targetFile);

            // 压缩图片
            if(uploadTypeEnum == FileUploadTypeEnum.COMMENT_IMAGE){
                String thumbnailName = targetFile.getName().replace(".","_.");
                File thumbnail = new File(targetFile.getParent()+"/"+thumbnailName);
                Boolean thumbnailCreated = imageUtils.createThumbnail(targetFile,
                        Constants.LENGTH_200,
                        Constants.LENGTH_200,
                        thumbnail);

                if(!thumbnailCreated){
                    org.apache.commons.io.FileUtils.copyFile(targetFile,thumbnail);

                }
            }else if(uploadTypeEnum == FileUploadTypeEnum.AVATAR||
            uploadTypeEnum== FileUploadTypeEnum.ARTICLE_COVER){
               imageUtils.createThumbnail(targetFile,Constants.LENGTH_200,Constants.LENGTH_200,
                       targetFile);
            }

            return uploadDto;
        }catch (BusinessException e){
            logger.error("文件上传失败！！！",e);
            throw  e;
        }catch (Exception e){
            logger.error("文件上传失败",e);
            throw new BusinessException("上传文件失败");
        }

    }

}
