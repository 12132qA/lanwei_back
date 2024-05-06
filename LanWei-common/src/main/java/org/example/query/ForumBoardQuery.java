package org.example.query;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.List;

/**
 * @author 张根勇
 */
@Component
@NoArgsConstructor
@ToString
@AllArgsConstructor
@Data
@TableName("forum_board")
public class ForumBoardQuery implements Serializable {
    Integer boardId;

    Integer pBoardId;

    String boardName;

    String cover;

    String boardDesc;

    private List<ForumBoardQuery> children;

    Integer sort;

    Integer postType;
}