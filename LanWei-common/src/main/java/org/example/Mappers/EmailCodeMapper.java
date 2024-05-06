package org.example.Mappers;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;
import org.example.pojo.EmailCode;

@Mapper
public interface EmailCodeMapper extends BaseMapper<EmailCode> {
    @Update("update email_code set status = 1 where email = #{email}  and status = 0;")
   public int disableEmailCode(String email);

}
