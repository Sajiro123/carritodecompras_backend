package com.shop.carrito.dto;

import com.shop.carrito.entity.Order;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record OrderDetailResponse(
        Long id,
        String codigoSeguimiento,
        String customerName,
        String customerEmail,
        String customerPhone,
        String tipoEntrega,
        String ubigeoCodigo,
        String direccionEnvio,
        String referenciaEnvio,
        Long tiendaId,
        Long agenciaId,
        String receptorNombre,
        String receptorDni,
        String tipoComprobante,
        String tipoDocumento,
        String numeroDocumento,
        String razonSocial,
        String direccionFiscal,
        String metodoPago,
        BigDecimal subtotal,
        BigDecimal costoEnvio,
        BigDecimal total,
        String status,
        Instant createdAt,
        List<ItemDetail> items
) {
    public record ItemDetail(
            Long productId,
            String productName,
            String productImageUrl,
            Integer quantity,
            BigDecimal unitPrice,
            BigDecimal subtotal
    ) {}

    public static OrderDetailResponse from(Order order) {
        var items = order.getItems().stream()
                .map(i -> new ItemDetail(
                        i.getProduct().getId(),
                        i.getProduct().getName(),
                        i.getProduct().getImageUrl(),
                        i.getQuantity(),
                        i.getUnitPrice(),
                        i.getSubtotal() != null ? i.getSubtotal() : i.getUnitPrice().multiply(BigDecimal.valueOf(i.getQuantity()))
                ))
                .toList();

        return new OrderDetailResponse(
                order.getId(),
                order.getCodigoSeguimiento(),
                order.getCustomerName(),
                order.getCustomerEmail(),
                order.getCustomerPhone(),
                order.getTipoEntrega(),
                order.getUbigeoCodigo(),
                order.getDireccionEnvio(),
                order.getReferenciaEnvio(),
                order.getTiendaId(),
                order.getAgenciaId(),
                order.getReceptorNombre(),
                order.getReceptorDni(),
                order.getTipoComprobante(),
                order.getTipoDocumento(),
                order.getNumeroDocumento(),
                order.getRazonSocial(),
                order.getDireccionFiscal(),
                order.getMetodoPago(),
                order.getSubtotal(),
                order.getCostoEnvio(),
                order.getTotal(),
                order.getStatus().name(),
                order.getCreatedAt(),
                items
        );
    }
}
