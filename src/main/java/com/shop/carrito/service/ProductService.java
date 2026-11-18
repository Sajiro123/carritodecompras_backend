package com.shop.carrito.service;

import com.shop.carrito.dto.ProductDto;
import com.shop.carrito.repository.ProductRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<ProductDto> getActiveProducts() {
        return productRepository.findByActiveTrue().stream()
                .map(ProductDto::from)
                .toList();
    }

    public ProductDto getById(Long id) {
        var product = productRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado"));
        return ProductDto.from(product);
    }
}
