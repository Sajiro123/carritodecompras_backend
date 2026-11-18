package com.shop.carrito.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record CreateReviewRequest(
    @NotBlank(message = "El nombre de usuario es obligatorio")
    String userName,

    @NotNull(message = "La calificación es obligatoria")
    @Min(value = 1, message = "La calificación mínima es 1")
    @Max(value = 5, message = "La calificación máxima es 5")
    Integer rating,

    @NotBlank(message = "El comentario es obligatorio")
    String comment,

    List<String> photos
) {}
