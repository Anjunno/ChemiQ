package com.chemiq.DTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ImageKeyUpdateRequestDto {
    private String originalFileKey; // 변환 전 HEIC 파일 키
    private String newFileKey;      // 변환 후 JPG 파일 키
}
