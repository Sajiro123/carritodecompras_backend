package com.shop.carrito.dto;

public record UploadResponse(
        String path,
        String publicUrl,
        String contentType,
        long size
) {}
