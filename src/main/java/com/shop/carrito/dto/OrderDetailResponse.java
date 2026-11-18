package com.shop.carrito.dto;

import com.shop.carrito.entity.Order;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record OrderDetailResponse(
        Long id,
        String customerName,
        String customerEmail,
        String customerPhone,
        String address,
        BigDecimal total,
        String status,
        Instant createdAt,
        List<ItemDetail> items
) {
    public record ItemDetail(Long productId, String productName, Integer quantity, BigDecimal unitPrice) {}

    public static OrderDetailResponse from(Order order) {
        var items = order.getItems().stream()
                .map(i -> new ItemDetail(i.getProduct().getId(), i.getProduct().getName(), i.getQuantity(), i.getUnitPrice()))
                .toList();
        return new OrderDetailResponse(
                order.getId(),
                order.getCustomerName(),
                order.getCustomerEmail(),
                order.getCustomerPhone(),
                order.getAddress(),
                order.getTotal(),
                order.getStatus().name(),
                order.getCreatedAt(),
                items
        );
    }
}
