package org.example.dto;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class FileUploadDto {
    private  String localPath;
    private String originalFileName;
}
