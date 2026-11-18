package com.shop.carrito.dto;

import java.math.BigDecimal;
import java.util.List;

public record ProductDetailDto(
    Long id,
    String name,
    String slug,
    String description,
    BigDecimal price,
    Integer stock,
    String imageUrl,
    Long categoryId,
    String categoryName,
    String categorySlug,
    List<ProductImageDto> galeria,
    List<ProductVariantDto> variantes,
    List<ProductDto> relacionados
) {}
