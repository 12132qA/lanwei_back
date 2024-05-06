package org.example.contorller;

import com.alibaba.fastjson.JSON;
import io.minio.*;
import io.minio.messages.Item;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.example.vo.ResponseVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.*;

/**
 * .
 *
 * @belongsProject: LanWei-java
 * @belongsPackage: org.example.contorller
 * @author: ZGY
 * @createTime: 2024-04-04  01:51
 * @version: 1.0
 */

@RestController
@Slf4j
public class MinioController {
    @Autowired
    private MinioClient minioClient;
    @Value("${minio.bucketName}")
    private String bucketName;
    @GetMapping("/list")
    public List<Object> list() throws Exception {
        //获取bucket列表
        Iterable<Result<Item>> myObjects = minioClient.listObjects(
                ListObjectsArgs.builder().bucket(bucketName).build());
        Iterator<Result<Item>> iterator = myObjects.iterator();
        List<Object> items = new ArrayList<>();
        String format = "{'fileName':'%s','fileSize':'%s'}";
        while (iterator.hasNext()) {
            Item item = iterator.next().get();
            items.add(JSON.parse(String.format(format, item.objectName(),
                    formatFileSize(item.size()))));
        }
        return items;
    }

    @PostMapping("/upload")
    public ResponseVO upload(@RequestParam(name = "file", required = false)
                      MultipartFile[] file) {
        if (file == null || file.length == 0) {
            return new ResponseVO("上传文件不能为空");
        }
        List<String> orgfileNameList = new ArrayList<>(file.length);

        for (MultipartFile multipartFile : file) {
            String orgfileName = multipartFile.getOriginalFilename();
            orgfileNameList.add(orgfileName);
            try {
                //文件上传
                InputStream in = multipartFile.getInputStream();
                minioClient.putObject(

                        PutObjectArgs.builder().bucket(bucketName).object(orgfileName).stream(
                                        in, multipartFile.getSize(), -1)
                                .contentType(multipartFile.getContentType())
                                .build());
                in.close();
            } catch (Exception e) {
                log.error(e.getMessage());
                return new ResponseVO("上传失败");
            }
        }
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("bucketName", bucketName);
        data.put("fileName", orgfileNameList);
        return new ResponseVO("上传成功",data);
    }

    @RequestMapping("/download/{fileName}")
    public void download(HttpServletResponse response, @PathVariable("fileName")
    String fileName) {
        InputStream in = null;
        try {
            // 获取对象信息
            StatObjectResponse stat = minioClient.statObject(

                    StatObjectArgs.builder().bucket(bucketName).object(fileName).build());
            response.setContentType(stat.contentType());
            response.setHeader("Content-Disposition", "attachment;filename=" +
                    URLEncoder.encode(fileName, "UTF-8"));
            //文件下载
            in = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .build());
            IOUtils.copy(in, response.getOutputStream());
        } catch (Exception e) {
            log.error(e.getMessage());
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    log.error(e.getMessage());
                }
            }
        }
    }
    @DeleteMapping("/delete/{fileName}")
    public ResponseVO delete(@PathVariable("fileName") String fileName) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder().bucket(bucketName).object(fileName).build());
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseVO("删除失败");
        }
        return new ResponseVO("删除成功",null);
    }
    private static String formatFileSize(long fileS) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        String wrongSize = "0B";
        if (fileS == 0) {
            return wrongSize;
        }
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + " B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + " KB";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + " MB";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + " GB";
        }
        return fileSizeString;
    }
}