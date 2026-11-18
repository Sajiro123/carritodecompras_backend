package com.shop.carrito.service;

import com.shop.carrito.dto.CreateReviewRequest;
import com.shop.carrito.dto.ReviewDto;
import com.shop.carrito.entity.Product;
import com.shop.carrito.entity.ProductReview;
import com.shop.carrito.entity.ReviewPhoto;
import com.shop.carrito.repository.ProductRepository;
import com.shop.carrito.repository.ProductReviewRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ReviewService {

    private final ProductRepository productRepository;
    private final ProductReviewRepository reviewRepository;

    public ReviewService(ProductRepository productRepository, ProductReviewRepository reviewRepository) {
        this.productRepository = productRepository;
        this.reviewRepository = reviewRepository;
    }

    private Product findProductByIdOrSlug(String idOrSlug) {
        if (idOrSlug.matches("\\d+")) {
            Long id = Long.parseLong(idOrSlug);
            return productRepository.findByIdAndActiveTrue(id)
                    .or(() -> productRepository.findBySlugAndActiveTrue(idOrSlug))
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado"));
        }
        return productRepository.findBySlugAndActiveTrue(idOrSlug)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado"));
    }

    @Transactional(readOnly = true)
    public List<ReviewDto> getReviewsForProduct(String idOrSlug) {
        Product product = findProductByIdOrSlug(idOrSlug);
        List<ProductReview> reviews = reviewRepository.findByProductIdOrderByCreatedAtDesc(product.getId());
        return reviews.stream()
                .map(ReviewDto::from)
                .toList();
    }

    @Transactional
    public ReviewDto createReview(String idOrSlug, CreateReviewRequest request) {
        Product product = findProductByIdOrSlug(idOrSlug);

        ProductReview review = new ProductReview(
                product,
                request.userName().trim(),
                request.rating(),
                request.comment().trim()
        );

        if (request.photos() != null) {
            for (String photoUrl : request.photos()) {
                if (photoUrl != null && !photoUrl.isBlank()) {
                    review.addPhoto(new ReviewPhoto(review, photoUrl.trim()));
                }
            }
        }

        ProductReview saved = reviewRepository.save(review);
        return ReviewDto.from(saved);
    }
}
