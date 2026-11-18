package com.shop.carrito.controller;

import com.shop.carrito.dto.CreateProductRequest;
import com.shop.carrito.dto.ProductDto;
import com.shop.carrito.dto.*;
import com.shop.carrito.service.ProductService;
import com.shop.carrito.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;
    private final ReviewService reviewService;

    public ProductController(ProductService productService, ReviewService reviewService) {
        this.productService = productService;
        this.reviewService = reviewService;
    }

    @GetMapping
    public PageResponse<ProductDto> getProducts(
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) String talla,
            @RequestParam(required = false) String color,
            @RequestParam(required = false) BigDecimal minPrecio,
            @RequestParam(required = false) BigDecimal maxPrecio,
            @RequestParam(required = false) Boolean disponible,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "12") Integer size
    ) {
        return productService.getProducts(
                categoria, talla, color, minPrecio, maxPrecio, disponible, q, sort, page, size
        );
    }

    @GetMapping("/filters")
    public FilterOptionsResponse getFilterOptions() {
        return productService.getFilterOptions();
    }

    @GetMapping("/{slug}")
    public ProductDetailDto getBySlug(@PathVariable String slug) {
        return productService.getBySlug(slug);
    }

    @GetMapping("/{id}/reviews")
    public List<ReviewDto> getReviews(@PathVariable String id) {
        return reviewService.getReviewsForProduct(id);
    }

    @PostMapping("/{id}/reviews")
    @ResponseStatus(HttpStatus.CREATED)
    public ReviewDto createReview(
            @PathVariable String id,
            @Valid @RequestBody CreateReviewRequest request
    ) {
        return reviewService.createReview(id, request);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductDto create(@Valid @RequestBody CreateProductRequest request) {
        return productService.create(request);
    }
}
