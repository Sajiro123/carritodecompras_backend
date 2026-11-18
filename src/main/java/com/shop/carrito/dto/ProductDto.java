package com.shop.carrito.dto;

import com.shop.carrito.entity.Product;
import java.math.BigDecimal;

public record ProductDto(
    Long id,
    String name,
    String slug,
    String description,
    BigDecimal price,
    Integer stock,
    String imageUrl,
    Long categoryId,
    String categoryName,
    String categorySlug
) {
    public static ProductDto from(Product p) {
        String catName = p.getCategory() != null ? p.getCategory().getName() : null;
        String catSlug = p.getCategory() != null ? p.getCategory().getSlug() : null;
        Long catId = p.getCategory() != null ? p.getCategory().getId() : null;
        return new ProductDto(
            p.getId(),
            p.getName(),
            p.getSlug(),
            p.getDescription(),
            p.getPrice(),
            p.getStock(),
            p.getImageUrl(),
            catId,
            catName,
            catSlug
        );
    }
}
