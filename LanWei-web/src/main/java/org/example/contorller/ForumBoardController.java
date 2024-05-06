package org.example.contorller;

import org.example.pojo.ForumBoard;
import org.example.service.ForumBoardService;
import org.example.service.impl.ForumBoardServiceImpl;
import org.example.vo.ResponseVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
/**
 * 板块信息
 * */

@RestController
@RequestMapping("/board")
public class ForumBoardController {

    @Autowired
    private ForumBoardServiceImpl forumBoardService;


    @RequestMapping("/loadBoard")
    public ResponseVO loadBoard(){
        return ResponseVO.getSuccessResponseVO(forumBoardService.getBoardTree(null));
    }

}
