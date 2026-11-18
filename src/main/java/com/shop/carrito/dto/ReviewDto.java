package com.shop.carrito.dto;

import com.shop.carrito.entity.ProductReview;
import com.shop.carrito.entity.ReviewPhoto;
import java.time.Instant;
import java.util.List;

public record ReviewDto(
    Long id,
    Long productId,
    String userName,
    Integer rating,
    String comment,
    List<String> photos,
    Instant createdAt
) {
    public static ReviewDto from(ProductReview r) {
        List<String> photoUrls = r.getPhotos() != null
                ? r.getPhotos().stream().map(ReviewPhoto::getImageUrl).toList()
                : List.of();
        Long prodId = r.getProduct() != null ? r.getProduct().getId() : null;
        return new ReviewDto(
            r.getId(),
            prodId,
            r.getUserName(),
            r.getRating(),
            r.getComment(),
            photoUrls,
            r.getCreatedAt()
        );
    }
}
