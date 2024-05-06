package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class SysSettingDto {
    private SysSetting4AuditDto auditDto;
    private SysSetting4CommentDto commentDto;
    private SysSetting4PostDto postDto;
    private SysSetting4LikeDto likeDto;
    private SysSetting4RegisterDto registerDto;
    private SysSetting4EmailDto emailDto;


}
