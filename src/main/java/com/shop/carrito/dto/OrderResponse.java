package com.shop.carrito.dto;

import java.math.BigDecimal;

public record OrderResponse(
        Long orderId,
        String codigoSeguimiento,
        BigDecimal subtotal,
        BigDecimal costoEnvio,
        BigDecimal total,
        String status
) {
    public OrderResponse(Long orderId, BigDecimal total, String status) {
        this(orderId, "", total, BigDecimal.ZERO, total, status);
    }
}
