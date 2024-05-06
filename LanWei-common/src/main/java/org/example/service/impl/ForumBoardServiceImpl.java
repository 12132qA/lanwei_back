package org.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.Mappers.ForumBoardMapper;
import org.example.Utils.CopyTools;
import org.example.pojo.ForumBoard;
import org.example.query.ForumBoardQuery;
import org.example.service.ForumBoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class ForumBoardServiceImpl extends ServiceImpl<ForumBoardMapper, ForumBoard> implements ForumBoardService {
   @Autowired
   ForumBoardMapper forumBoardMapper;

    @Override
    public List<ForumBoardQuery> getBoardTree(Integer postType) {

        /**
         * sort asc 升序排序
         * */

        QueryWrapper<ForumBoard> qw = new QueryWrapper<>();
        qw.orderByAsc("sort");
        if(postType!=null) {
            qw.eq("post_type", postType);
        }
        List<ForumBoard> forumBoards = forumBoardMapper.selectList(qw);
        List<ForumBoardQuery> fq = CopyTools.copyList(forumBoards, ForumBoardQuery.class);

        return  convertLine2Tree(fq,0);
       //     convertLine2Tree(forumBoards,0);
    }

    /**
     * 转化成树形 板块
     * */

    private List<ForumBoardQuery> convertLine2Tree(List<ForumBoardQuery> dataList,Integer pid){
          List<ForumBoardQuery> children = new ArrayList<>();

          for(ForumBoardQuery forumBoard: dataList){
              if(Objects.equals(forumBoard.getPBoardId(), pid)){
                forumBoard.setChildren(convertLine2Tree(dataList, forumBoard.getBoardId()));
                children.add(forumBoard);
              }
          }
          return children;
    }

}
