package com.shop.carrito.dto;

import com.shop.carrito.entity.ProductVariant;
import java.math.BigDecimal;
import java.util.List;

public record ProductVariantDto(
    Long id,
    String sku,
    BigDecimal price,
    Integer stock,
    List<AttributeValueDto> attributes
) {
    public static ProductVariantDto from(ProductVariant v) {
        var attrs = (v.getAttributeValues() != null ? v.getAttributeValues() : List.<com.shop.carrito.entity.AttributeValue>of())
                .stream()
                .map(AttributeValueDto::from)
                .toList();
        return new ProductVariantDto(v.getId(), v.getSku(), v.getPrice(), v.getStock(), attrs);
    }
}
