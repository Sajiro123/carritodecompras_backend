package com.shop.carrito.service;

import com.shop.carrito.dto.CreateProductRequest;
import com.shop.carrito.dto.ProductDto;
import com.shop.carrito.dto.*;
import com.shop.carrito.entity.Product;
import com.shop.carrito.repository.AttributeValueRepository;
import com.shop.carrito.repository.ProductRepository;
import com.shop.carrito.specification.ProductSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final AttributeValueRepository attributeValueRepository;
    private final CategoryService categoryService;

    public ProductService(
            ProductRepository productRepository,
            AttributeValueRepository attributeValueRepository,
            CategoryService categoryService
    ) {
        this.productRepository = productRepository;
        this.attributeValueRepository = attributeValueRepository;
        this.categoryService = categoryService;
    }

    @Transactional(readOnly = true)
    public PageResponse<ProductDto> getProducts(
            String categoria,
            String talla,
            String color,
            BigDecimal minPrecio,
            BigDecimal maxPrecio,
            Boolean disponible,
            String q,
            String sort,
            Integer page,
            Integer size
    ) {
        int pageNumber = (page != null && page >= 0) ? page : 0;
        int pageSize = (size != null && size > 0 && size <= 100) ? size : 12;

        Sort sortObj = parseSort(sort);
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sortObj);

        Specification<Product> spec = ProductSpecification.filter(
                categoria, talla, color, minPrecio, maxPrecio, disponible, q
        );

        Page<ProductDto> productPage = productRepository.findAll(spec, pageable).map(ProductDto::from);
        return PageResponse.from(productPage);
    }

    @Transactional(readOnly = true)
    public FilterOptionsResponse getFilterOptions() {
        var categories = categoryService.getCategoriesWithActiveProductCount();
        var tallas = attributeValueRepository.findDistinctValuesByAttributeName("talla");
        var colores = attributeValueRepository.findDistinctValuesByAttributeName("color");
        BigDecimal minPrice = productRepository.findMinPrice();
        BigDecimal maxPrice = productRepository.findMaxPrice();

        return new FilterOptionsResponse(
                categories,
                tallas,
                colores,
                minPrice != null ? minPrice : BigDecimal.ZERO,
                maxPrice != null ? maxPrice : BigDecimal.valueOf(500)
        );
    }

    @Transactional(readOnly = true)
    public List<ProductDto> getActiveProducts() {
        return productRepository.findByActiveTrue().stream()
                .map(ProductDto::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProductDto getById(Long id) {
        Product product = productRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado"));
        return ProductDto.from(product);
    }

    /** Crea y activa un producto nuevo en el catálogo. */
    @Transactional
    public ProductDto create(CreateProductRequest request) {
        String cleanDescription = request.description() != null && !request.description().isBlank()
                ? request.description().trim()
                : null;
        var product = new Product(
                request.name().trim(),
                cleanDescription,
                request.price(),
                request.stock(),
                request.imageUrl().trim()
        );
        return ProductDto.from(productRepository.save(product));
    }
}
