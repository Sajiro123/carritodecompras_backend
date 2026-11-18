package com.shop.carrito.dto;

import com.shop.carrito.entity.AttributeValue;

public record AttributeValueDto(
    Long id,
    String attribute,
    String value
) {
    public static AttributeValueDto from(AttributeValue av) {
        String attrName = av.getAttribute() != null ? av.getAttribute().getName() : "";
        return new AttributeValueDto(av.getId(), attrName, av.getValor());
    }
}
