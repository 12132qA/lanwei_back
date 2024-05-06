package org.example.Mappers;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;
import org.example.pojo.UserInfo;

@Mapper
public interface UserInfoMapper extends BaseMapper<UserInfo> {

    /*
    *
    * 乐观锁
    * */
    @Update("update user_info set current_integral = current_integral+#{integral} ," +
            " total_integral = total_integral+#{integral}   where user_id  = #{user_id}  " +
            "and current_integral+#{integral} >= 0 "

            )
    int updateIntegral(String userId,Integer integral);
}
