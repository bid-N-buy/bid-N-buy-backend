package com.bidnbuy.server.dto;

import com.bidnbuy.server.entity.ImageEntity;
import lombok.Data;

@Data
public class ImageDTO {
    private String ImageUrl;

    public ImageEntity toEntity(String ImageUrl) {
        return ImageEntity.builder()
                .imageUrl(ImageUrl)
                .build();
    }
}
