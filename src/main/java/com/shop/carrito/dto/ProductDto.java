package com.shop.carrito.dto;

import com.shop.carrito.entity.Product;
import java.math.BigDecimal;

public record ProductDto(
        Long id,
        String name,
        String description,
        BigDecimal price,
        Integer stock,
        String imageUrl
) {
    public static ProductDto from(Product p) {
        return new ProductDto(p.getId(), p.getName(), p.getDescription(), p.getPrice(), p.getStock(), p.getImageUrl());
    }
}
