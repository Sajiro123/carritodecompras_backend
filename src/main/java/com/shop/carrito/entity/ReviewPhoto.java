package com.shop.carrito.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "review_foto")
public class ReviewPhoto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id", nullable = false)
    @JsonIgnore
    private ProductReview review;

    @Column(name = "image_url", nullable = false, length = 500)
    private String imageUrl;

    public ReviewPhoto() {}

    public ReviewPhoto(ProductReview review, String imageUrl) {
        this.review = review;
        this.imageUrl = imageUrl;
    }

    public Long getId() { return id; }
    public ProductReview getReview() { return review; }
    public void setReview(ProductReview review) { this.review = review; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}
