package com.shop.carrito.dto;

import com.shop.carrito.entity.ProductImage;

public record ProductImageDto(
    Long id,
    String imageUrl,
    Boolean isCover,
    Integer displayOrder
) {
    public static ProductImageDto from(ProductImage img) {
        return new ProductImageDto(img.getId(), img.getImageUrl(), img.getIsCover(), img.getDisplayOrder());
    }
}
