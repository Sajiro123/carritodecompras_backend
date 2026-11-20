package com.shop.carrito.dto;

import com.shop.carrito.entity.Product;
import java.math.BigDecimal;

public record ProductDto(
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
    Boolean fastDeliveryLima
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
            p.getOriginalPrice(),
            p.getSamplePrice(),
            p.getStock(),
            p.getImageUrl(),
            catId,
            catName,
            catSlug,
            p.getBrand(),
            p.getOlfactoryFamily(),
            p.getConcentration(),
            p.getOccasion(),
            p.getVolumeLabel(),
            p.getFormatType(),
            p.getNotes(),
            p.getBadge(),
            p.getBadgeTone(),
            p.getRating(),
            p.getReviewsCount(),
            p.getGender(),
            p.getIsNiche(),
            p.getFastDeliveryLima()
        );
    }
}
