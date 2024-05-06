package org.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.pojo.ForumBoard;
import org.example.query.ForumBoardQuery;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 板块列表
 * */

public interface ForumBoardService extends IService<ForumBoard> {

    public List<ForumBoardQuery> getBoardTree(Integer postType); // 区分类型 不分 或 全部 加载
}
