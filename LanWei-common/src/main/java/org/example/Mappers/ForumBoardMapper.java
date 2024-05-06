package org.example.Mappers;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.pojo.ForumBoard;

@Mapper
public interface ForumBoardMapper extends BaseMapper<ForumBoard> {
}
