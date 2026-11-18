package com.shop.carrito.service;

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

    @Transactional(readOnly = true)
    public ProductDetailDto getBySlug(String slug) {
        Product product;
        if (slug.matches("\\d+")) {
            Long id = Long.parseLong(slug);
            product = productRepository.findByIdAndActiveTrue(id)
                    .or(() -> productRepository.findBySlugAndActiveTrue(slug))
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado"));
        } else {
            product = productRepository.findBySlugAndActiveTrue(slug)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado"));
        }

        // Galería (producto_imagen)
        var galeria = (product.getImages() != null ? product.getImages() : List.<com.shop.carrito.entity.ProductImage>of())
                .stream()
                .map(ProductImageDto::from)
                .toList();

        // Variantes con stock y atributos (producto_variante + atributo_valor)
        var variantes = (product.getVariants() != null ? product.getVariants() : List.<com.shop.carrito.entity.ProductVariant>of())
                .stream()
                .map(ProductVariantDto::from)
                .toList();

        // Productos relacionados (de la misma categoría o catálogo general)
        List<ProductDto> relacionados = List.of();
        if (product.getCategory() != null) {
            relacionados = productRepository.findTop4ByCategoryAndActiveTrueAndIdNot(product.getCategory(), product.getId())
                    .stream()
                    .map(ProductDto::from)
                    .toList();
        }
        if (relacionados.isEmpty()) {
            relacionados = productRepository.findTop4ByActiveTrueAndIdNot(product.getId())
                    .stream()
                    .map(ProductDto::from)
                    .toList();
        }

        Long catId = product.getCategory() != null ? product.getCategory().getId() : null;
        String catName = product.getCategory() != null ? product.getCategory().getName() : null;
        String catSlug = product.getCategory() != null ? product.getCategory().getSlug() : null;

        return new ProductDetailDto(
                product.getId(),
                product.getName(),
                product.getSlug(),
                product.getDescription(),
                product.getPrice(),
                product.getStock(),
                product.getImageUrl(),
                catId,
                catName,
                catSlug,
                galeria,
                variantes,
                relacionados
        );
    }

    private Sort parseSort(String sort) {
        if (sort == null || sort.isBlank()) {
            return Sort.by(Sort.Direction.ASC, "id");
        }
        String s = sort.trim().toLowerCase();
        return switch (s) {
            case "price,asc", "precio_asc", "price-asc", "asc" -> Sort.by(Sort.Direction.ASC, "price");
            case "price,desc", "precio_desc", "price-desc", "desc" -> Sort.by(Sort.Direction.DESC, "price");
            case "name,asc", "nombre_asc", "name-asc" -> Sort.by(Sort.Direction.ASC, "name");
            case "name,desc", "nombre_desc", "name-desc" -> Sort.by(Sort.Direction.DESC, "name");
            case "newest", "date,desc", "recent", "created,desc" -> Sort.by(Sort.Direction.DESC, "createdAt");
            default -> Sort.by(Sort.Direction.ASC, "id");
        };
    }
}
