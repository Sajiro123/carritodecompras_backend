package com.shop.carrito.dto;

import java.math.BigDecimal;
import java.util.List;

public record ProductDetailDto(
    Long id,
    String name,
    String slug,
    String description,
    BigDecimal price,
    BigDecimal originalPrice,
    BigDecimal samplePrice,
    Integer stock,
    String imageUrl,
    Long categoryId,
    String categoryName,
    String categorySlug,
    String brand,
    String olfactoryFamily,
    String concentration,
    String occasion,
    String volumeLabel,
    String formatType,
    String notes,
    String badge,
    String badgeTone,
    BigDecimal rating,
    Integer reviewsCount,
    String gender,
    Boolean isNiche,
    Boolean fastDeliveryLima,
    List<ProductImageDto> galeria,
    List<ProductVariantDto> variantes,
    List<ProductDto> relacionados
) {}
