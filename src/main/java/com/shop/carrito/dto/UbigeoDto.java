package com.shop.carrito.dto;

import com.shop.carrito.entity.Ubigeo;
import java.math.BigDecimal;

public record UbigeoDto(
        String codigo,
        String departamento,
        String provincia,
        String distrito,
        String zona,
        BigDecimal costoEnvio,
        String label
) {
    public static UbigeoDto from(Ubigeo u) {
        return new UbigeoDto(
                u.getCodigo(),
                u.getDepartamento(),
                u.getProvincia(),
                u.getDistrito(),
                u.getZona(),
                u.getCostoEnvio(),
                u.getLabel()
        );
    }
}
