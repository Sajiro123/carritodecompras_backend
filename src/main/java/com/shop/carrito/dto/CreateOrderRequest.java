package com.shop.carrito.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record CreateOrderRequest(
        // Nuevo formato estructurado por secciones
        ContactoDto contacto,
        EntregaDto entrega,
        ComprobanteDto comprobante,
        PagoDto pago,

        // Compatibilidad hacia atrás opcional
        CustomerDto customer,

        @NotEmpty(message = "El pedido debe contener al menos un producto")
        @Valid
        List<OrderItemRequest> items
) {
    public record ContactoDto(
            String nombre,
            String telefono,
            String email
    ) {}

    public record EntregaDto(
            String tipo, // DOMICILIO, SHALOM, TIENDA
            String ubigeoCodigo,
            String direccion,
            String referencia,
            Long tiendaId,
            Long agenciaId,
            String receptorNombre,
            String receptorDni
    ) {}

    public record ComprobanteDto(
            String tipo, // BOLETA, FACTURA
            String tipoDocumento, // DNI, CE, PASAPORTE, RUC
            String numeroDocumento,
            String razonSocial,
            String direccionFiscal
    ) {}

    public record PagoDto(
            String metodo // BCP, YAPE_PLIN, CULQI, FLOW
    ) {}

    // Formato simple de compatibilidad
    public record CustomerDto(
            String name,
            String email,
            String phone,
            String address
    ) {}

    public record OrderItemRequest(
            @NotNull(message = "productId es obligatorio") Long productId,
            @NotNull Integer quantity
    ) {}
}
