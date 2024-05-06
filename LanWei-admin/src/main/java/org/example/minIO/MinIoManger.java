package org.example.minIO;

import io.minio.*;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * .
 *
 * @belongsProject: LanWei-java
 * @belongsPackage: org.example.minIO
 * @author: ZGY
 * @createTime: 2024-04-03  17:24
 * @version: 1.0
 */
@Component
public class MinIoManger {  // https://min.io/docs/minio/linux/index.html?ref=con

   private MinioClient minioClient =
            MinioClient.builder()
                    .endpoint("http://47.97.190.245:9000")
                    .credentials("admin", "132132qa")
                    .build();


   /**
    * .
    *
    * @author zgy
    * @date 2024/4/4 1:43
    * @param
    * @return
    * @methodName MinIoFileUploader
    *
    *
    **/

    // 文件上传
    public void MinIoFileUploader(String txt) {
        try {
            // 创建bucket
            String bucketName = "tulingmall";
            boolean exists =
                    minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (!exists) {
                // 不存在，创建bucket
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            }
            // 上传文件
            minioClient.uploadObject(
                    UploadObjectArgs.builder()
                            .bucket(bucketName)
                            .object("tuling-mall-master.zip")
                            .filename("F:\\mall\\tuling-mall-master.zip")
                            .build());
            System.out.println("上传文件成功");
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    /**
     * .
     *
     * @author zgy
     * @date 2024/4/4 1:42
     * @param args
     * @return
     * @methodName DownLoadDemo
     *
     *
     **/
    public void  DownLoadDemo  (String[] args) {

        // Download object given the bucket, object name and output file name
        try {
            minioClient.downloadObject(
                    DownloadObjectArgs.builder()
                            .bucket("tulingmall")
                            .object("fox/fox.jpg")
                            .filename("fox.jpg")
                            .build());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}