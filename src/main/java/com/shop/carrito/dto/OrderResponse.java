package com.shop.carrito.dto;

import java.math.BigDecimal;

public record OrderResponse(
        Long orderId,
        BigDecimal total,
        String status
) {}
