package com.shop.carrito.dto;

public record CategoryResponse(
    Long id,
    String name,
    String slug,
    String description,
    String imageUrl,
    long activeProductsCount
) {}
