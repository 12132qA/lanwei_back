package org.example.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.List;

@Component
@NoArgsConstructor
@ToString
@AllArgsConstructor
@Data
@TableName("forum_board")
public class ForumBoard  implements Serializable {
    Integer boardId;

   Integer pBoardId;

    String boardName;

    String cover;

    String boardDesc;

//    private List<ForumBoard> children;

    int sort;

    int postType;
}
