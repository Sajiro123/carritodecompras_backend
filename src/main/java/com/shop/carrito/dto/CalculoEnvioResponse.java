package com.shop.carrito.dto;

import java.math.BigDecimal;
import java.math.RoundingMode;

public record CalculoEnvioResponse(
        BigDecimal costoEnvio,
        boolean gratis,
        BigDecimal umbralGratis,
        BigDecimal subtotal,
        BigDecimal faltanteParaGratis,
        double porcentajeGratis,
        String mensaje,
        String zona,
        String diasEntrega
) {
    public static CalculoEnvioResponse calcular(BigDecimal subtotal, BigDecimal costoBase, String zona, String diasEntrega) {
        BigDecimal umbral = BigDecimal.valueOf(300.00);
        if (subtotal == null) subtotal = BigDecimal.ZERO;
        if (costoBase == null) costoBase = BigDecimal.valueOf(18.00);

        boolean esGratis = subtotal.compareTo(umbral) >= 0;
        BigDecimal costoFinal = esGratis ? BigDecimal.ZERO : costoBase;
        BigDecimal faltante = esGratis ? BigDecimal.ZERO : umbral.subtract(subtotal);

        double pct = 0.0;
        if (umbral.compareTo(BigDecimal.ZERO) > 0) {
            pct = subtotal.multiply(BigDecimal.valueOf(100))
                    .divide(umbral, 2, RoundingMode.HALF_UP)
                    .doubleValue();
            if (pct > 100.0) pct = 100.0;
        }

        String msg;
        if (esGratis) {
            msg = "¡Felicidades, tienes envío gratis!";
        } else {
            msg = String.format("¡Te faltan S/ %.2f para tener envío gratis!", faltante.doubleValue());
        }

        return new CalculoEnvioResponse(
                costoFinal,
                esGratis,
                umbral,
                subtotal,
                faltante,
                pct,
                msg,
                zona != null ? zona : "GENERAL",
                diasEntrega != null ? diasEntrega : "24 a 48 horas"
        );
    }
}
