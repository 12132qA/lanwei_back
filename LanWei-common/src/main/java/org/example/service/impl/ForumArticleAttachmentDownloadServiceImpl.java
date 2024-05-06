package org.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.example.Mappers.ForumArticleAttachmentDownloadMapper;
import org.example.Mappers.ForumArticleAttachmentMapper;
import org.example.pojo.ForumArticleAttachment;
import org.example.pojo.ForumArticleAttachmentDownload;
import org.example.service.ForumArticleAttachmentDownloadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * 附件下载
 * */
@Slf4j
@Service
public class ForumArticleAttachmentDownloadServiceImpl extends ServiceImpl<ForumArticleAttachmentDownloadMapper, ForumArticleAttachmentDownload> implements ForumArticleAttachmentDownloadService {

    @Autowired
    private ForumArticleAttachmentDownloadMapper fam;
    @Autowired
    private ForumArticleAttachmentMapper fa;

    @Override
    public ForumArticleAttachmentDownload getForumArticleAttachmentDownLoadByFileIdAndUserId(String fileId, String userId){
        log.info( "foru 获取操作");
        //
        LambdaQueryWrapper<ForumArticleAttachmentDownload> qw = new LambdaQueryWrapper<>();
        //
        LambdaQueryWrapper<ForumArticleAttachmentDownload> and
                = qw.eq(ForumArticleAttachmentDownload::getFileId, fileId)
                .and(w -> w.eq(ForumArticleAttachmentDownload::getUserId, userId));
        return  fam.selectOne(and);

    }
    /**
     * 与上一方法为同一方法
     * */

    public ForumArticleAttachmentDownload selectByFileIdAndUserId(String fileId, String userId){
        log.info( "foru select 获取操作");
        LambdaQueryWrapper<ForumArticleAttachmentDownload> qw = new LambdaQueryWrapper<>();
        LambdaQueryWrapper<ForumArticleAttachmentDownload> and
                = qw.eq(ForumArticleAttachmentDownload::getFileId, fileId)
                .and(w -> w.eq(ForumArticleAttachmentDownload::getUserId, userId));
        return  fam.selectOne(and);

    }

    /**
     * insertOrUpdate
     * <p>
     * 不存在则添加 存在则修改
     **/
    public void insertOrUpdate(ForumArticleAttachmentDownload fad) {
        int res = 0;
        LambdaQueryWrapper<ForumArticleAttachmentDownload> qw = new LambdaQueryWrapper<>();
        LambdaUpdateWrapper<ForumArticleAttachmentDownload> upd = new LambdaUpdateWrapper<>();

        LambdaQueryWrapper<ForumArticleAttachmentDownload> and = qw
                .eq(ForumArticleAttachmentDownload::getUserId, fad.getUserId())
                .and(q -> q.eq(ForumArticleAttachmentDownload::getArticleId, fad.getArticleId()));

        ForumArticleAttachmentDownload forumArticleAttachmentDownload = fam.selectById(and);
        if (forumArticleAttachmentDownload == null) {
            res =    fam.insert(fad);
        } else {
            res = fam.update(fad, upd
                    .eq(ForumArticleAttachmentDownload::getFileId, fad.getFileId())
                    .and(w -> w.eq(ForumArticleAttachmentDownload::getUserId, fad.getUserId())));


        }
    }


    public void updateDownloadCount(String fileId){

        LambdaUpdateWrapper<ForumArticleAttachment> up
                = new LambdaUpdateWrapper<>();
        LambdaQueryWrapper<ForumArticleAttachment> wp = new LambdaQueryWrapper<>();
        // 文章 ID
        wp.eq(ForumArticleAttachment::getFile_id,fileId);
        //  文章
        ForumArticleAttachment f = fa.selectOne(wp);

        if(f!=null){
            up.eq(ForumArticleAttachment::getFile_id,fileId)
               .set(ForumArticleAttachment::getDownload_count, f.getDownload_count()+1);
        }

        fa.update(f, up);
    }

}