package com.shop.carrito.dto;

import java.math.BigDecimal;
import java.util.List;

public record FilterOptionsResponse(
    List<CategoryResponse> categories,
    List<String> tallas,
    List<String> colores,
    BigDecimal minPrice,
    BigDecimal maxPrice,
    List<FilterFacetDto> familias,
    List<String> concentraciones,
    List<String> ocasiones,
    List<String> formatos,
    List<FilterFacetDto> marcas,
    List<String> generos
) {}
