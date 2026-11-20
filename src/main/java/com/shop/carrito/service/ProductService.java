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
import java.text.Normalizer;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

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
            String marca,
            String familia,
            String concentracion,
            String ocasion,
            String formato,
            String genero,
            Boolean nicho,
            Boolean soloDescuento,
            Boolean entrega24h,
            String sort,
            Integer page,
            Integer size
    ) {
        int pageNumber = (page != null && page >= 0) ? page : 0;
        int pageSize = (size != null && size > 0 && size <= 100) ? size : 12;

        Sort sortObj = parseSort(sort);
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sortObj);

        Specification<Product> spec = ProductSpecification.filter(
                categoria, talla, color, minPrecio, maxPrecio, disponible, q,
                marca, familia, concentracion, ocasion, formato, genero, nicho, soloDescuento, entrega24h
        );

        Page<ProductDto> productPage = productRepository.findAll(spec, pageable).map(ProductDto::from);
        return PageResponse.from(productPage);
    }

    @Transactional(readOnly = true)
    public FilterOptionsResponse getFilterOptions() {
        var categories = categoryService.getCategoriesWithActiveProductCount();
        var tallas = attributeValueRepository.findDistinctValuesByAttributeName("presentación");
        var colores = attributeValueRepository.findDistinctValuesByAttributeName("concentración");
        BigDecimal minPrice = productRepository.findMinPrice();
        BigDecimal maxPrice = productRepository.findMaxPrice();

        List<FilterFacetDto> familias = productRepository.countByOlfactoryFamily().stream()
                .map(row -> new FilterFacetDto((String) row[0], ((Number) row[1]).longValue()))
                .toList();

        List<FilterFacetDto> marcas = productRepository.countByBrand().stream()
                .map(row -> new FilterFacetDto((String) row[0], ((Number) row[1]).longValue()))
                .toList();

        List<String> concentraciones = productRepository.findDistinctConcentrations();
        if (concentraciones.isEmpty()) {
            concentraciones = List.of("EDP", "Extrait", "EDT", "Elixir");
        }

        List<String> formatos = productRepository.findDistinctFormatTypes();
        List<String> generos = productRepository.findDistinctGenders();

        Set<String> occasionSet = new LinkedHashSet<>();
        for (String occRaw : productRepository.findAllOccasionStrings()) {
            if (occRaw != null) {
                for (String token : occRaw.split(",")) {
                    String clean = token.trim();
                    if (!clean.isEmpty()) {
                        occasionSet.add(clean);
                    }
                }
            }
        }
        List<String> ocasiones = occasionSet.isEmpty()
                ? List.of("Citas Románticas", "Noche & Gala", "Oficina / Firma", "Club & Fiesta", "Verano & Fresco")
                : List.copyOf(occasionSet);

        return new FilterOptionsResponse(
                categories,
                tallas,
                colores,
                minPrice != null ? minPrice : BigDecimal.ZERO,
                maxPrice != null ? maxPrice : BigDecimal.valueOf(1800),
                familias,
                concentraciones,
                ocasiones,
                formatos,
                marcas,
                generos
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

        var galeria = (product.getImages() != null ? product.getImages() : List.<com.shop.carrito.entity.ProductImage>of())
                .stream()
                .map(ProductImageDto::from)
                .toList();

        var variantes = (product.getVariants() != null ? product.getVariants() : List.<com.shop.carrito.entity.ProductVariant>of())
                .stream()
                .map(ProductVariantDto::from)
                .toList();

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
                product.getOriginalPrice(),
                product.getSamplePrice(),
                product.getStock(),
                product.getImageUrl(),
                catId,
                catName,
                catSlug,
                product.getBrand(),
                product.getOlfactoryFamily(),
                product.getConcentration(),
                product.getOccasion(),
                product.getVolumeLabel(),
                product.getFormatType(),
                product.getNotes(),
                product.getBadge(),
                product.getBadgeTone(),
                product.getRating(),
                product.getReviewsCount(),
                product.getGender(),
                product.getIsNiche(),
                product.getFastDeliveryLima(),
                galeria,
                variantes,
                relacionados
        );
    }

    /** Crea y activa un producto nuevo en el catálogo. */
    @Transactional
    public ProductDto create(CreateProductRequest request) {
        String cleanName = request.name().trim();
        String cleanDescription = request.description() != null && !request.description().isBlank()
                ? request.description().trim()
                : null;
        var product = new Product(
                cleanName,
                buildUniqueSlug(cleanName),
                cleanDescription,
                request.price(),
                request.stock(),
                request.imageUrl().trim(),
                null
        );
        product.setBrand("AROMA ATELIER");
        product.setConcentration("EDP");
        product.setVolumeLabel("100 ML EDP");
        product.setFormatType("Frasco 100ml+");
        product.setGender("Unisex");
        product.setFastDeliveryLima(true);
        return ProductDto.from(productRepository.save(product));
    }

    private String buildUniqueSlug(String name) {
        String base = Normalizer.normalize(name, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-+|-+$", "");
        if (base.isBlank()) {
            base = "fragancia";
        }
        if (base.length() > 130) {
            base = base.substring(0, 130);
        }
        if (productRepository.findBySlugAndActiveTrue(base).isEmpty()) {
            return base;
        }
        return base + "-" + UUID.randomUUID().toString().substring(0, 6);
    }

    private Sort parseSort(String sort) {
        if (sort == null || sort.isBlank() || "bestsellers".equalsIgnoreCase(sort.trim())) {
            return Sort.by(Sort.Order.desc("reviewsCount"), Sort.Order.asc("id"));
        }
        String s = sort.trim().toLowerCase();
        return switch (s) {
            case "price,asc", "precio_asc", "price-asc", "asc" -> Sort.by(Sort.Direction.ASC, "price");
            case "price,desc", "precio_desc", "price-desc", "desc" -> Sort.by(Sort.Direction.DESC, "price");
            case "name,asc", "nombre_asc", "name-asc" -> Sort.by(Sort.Direction.ASC, "name");
            case "name,desc", "nombre_desc", "name-desc" -> Sort.by(Sort.Direction.DESC, "name");
            case "rating,desc", "rating" -> Sort.by(Sort.Order.desc("rating"), Sort.Order.desc("reviewsCount"));
            case "newest", "date,desc", "recent", "created,desc" -> Sort.by(Sort.Direction.DESC, "createdAt");
            default -> Sort.by(Sort.Order.desc("reviewsCount"), Sort.Order.asc("id"));
        };
    }
}
