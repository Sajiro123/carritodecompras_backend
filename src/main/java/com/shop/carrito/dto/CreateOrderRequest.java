package com.shop.carrito.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record CreateOrderRequest(
        @NotNull @Valid CustomerDto customer,
        @NotEmpty @Valid List<OrderItemRequest> items
) {
    public record CustomerDto(
            @NotBlank(message = "El nombre es obligatorio") String name,
            @NotBlank(message = "El email es obligatorio") @Email(message = "Email inválido") String email,
            String phone,
            @NotBlank(message = "La dirección es obligatoria") String address
    ) {}

    public record OrderItemRequest(
            @NotNull(message = "productId es obligatorio") Long productId,
            @NotNull @Min(value = 1, message = "La cantidad mínima es 1") Integer quantity
    ) {}
}
