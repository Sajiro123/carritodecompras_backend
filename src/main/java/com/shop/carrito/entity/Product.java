package com.shop.carrito.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(length = 150, unique = true)
    private String slug;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private Integer stock = 0;

    @Column(name = "image_url", length = 255)
    private String imageUrl;

    @Column(nullable = false)
    private Boolean active = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(length = 100)
    private String brand;

    @Column(name = "olfactory_family", length = 80)
    private String olfactoryFamily;

    @Column(length = 40)
    private String concentration;

    @Column(length = 180)
    private String occasion;

    @Column(name = "volume_label", length = 60)
    private String volumeLabel;

    @Column(name = "format_type", length = 60)
    private String formatType;

    @Column(length = 300)
    private String notes;

    @Column(length = 50)
    private String badge;

    @Column(name = "badge_tone", length = 20)
    private String badgeTone;

    @Column(name = "original_price", precision = 10, scale = 2)
    private BigDecimal originalPrice;

    @Column(name = "sample_price", precision = 10, scale = 2)
    private BigDecimal samplePrice;

    @Column(precision = 3, scale = 1)
    private BigDecimal rating = new BigDecimal("4.9");

    @Column(name = "reviews_count")
    private Integer reviewsCount = 0;

    @Column(length = 30)
    private String gender;

    @Column(name = "is_niche")
    private Boolean isNiche = false;

    @Column(name = "fast_delivery_lima")
    private Boolean fastDeliveryLima = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("displayOrder ASC")
    private List<ProductImage> images = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductVariant> variants = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("createdAt DESC")
    private List<ProductReview> reviews = new ArrayList<>();

    public Product() {}

    public Product(String name, String description, BigDecimal price, Integer stock, String imageUrl) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.stock = stock;
        this.imageUrl = imageUrl;
        this.active = true;
        this.createdAt = Instant.now();
    }

    public Product(String name, String slug, String description, BigDecimal price, Integer stock, String imageUrl, Category category) {
        this.name = name;
        this.slug = slug;
        this.description = description;
        this.price = price;
        this.stock = stock;
        this.imageUrl = imageUrl;
        this.category = category;
        this.active = true;
        this.createdAt = Instant.now();
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }
    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }
    public String getOlfactoryFamily() { return olfactoryFamily; }
    public void setOlfactoryFamily(String olfactoryFamily) { this.olfactoryFamily = olfactoryFamily; }
    public String getConcentration() { return concentration; }
    public void setConcentration(String concentration) { this.concentration = concentration; }
    public String getOccasion() { return occasion; }
    public void setOccasion(String occasion) { this.occasion = occasion; }
    public String getVolumeLabel() { return volumeLabel; }
    public void setVolumeLabel(String volumeLabel) { this.volumeLabel = volumeLabel; }
    public String getFormatType() { return formatType; }
    public void setFormatType(String formatType) { this.formatType = formatType; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public String getBadge() { return badge; }
    public void setBadge(String badge) { this.badge = badge; }
    public String getBadgeTone() { return badgeTone; }
    public void setBadgeTone(String badgeTone) { this.badgeTone = badgeTone; }
    public BigDecimal getOriginalPrice() { return originalPrice; }
    public void setOriginalPrice(BigDecimal originalPrice) { this.originalPrice = originalPrice; }
    public BigDecimal getSamplePrice() { return samplePrice; }
    public void setSamplePrice(BigDecimal samplePrice) { this.samplePrice = samplePrice; }
    public BigDecimal getRating() { return rating; }
    public void setRating(BigDecimal rating) { this.rating = rating; }
    public Integer getReviewsCount() { return reviewsCount; }
    public void setReviewsCount(Integer reviewsCount) { this.reviewsCount = reviewsCount; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public Boolean getIsNiche() { return isNiche; }
    public void setIsNiche(Boolean isNiche) { this.isNiche = isNiche; }
    public Boolean getFastDeliveryLima() { return fastDeliveryLima; }
    public void setFastDeliveryLima(Boolean fastDeliveryLima) { this.fastDeliveryLima = fastDeliveryLima; }
    public Instant getCreatedAt() { return createdAt; }
    public List<ProductImage> getImages() { return images; }
    public void setImages(List<ProductImage> images) { this.images = images; }
    public List<ProductVariant> getVariants() { return variants; }
    public void setVariants(List<ProductVariant> variants) { this.variants = variants; }
    public List<ProductReview> getReviews() { return reviews; }
    public void setReviews(List<ProductReview> reviews) { this.reviews = reviews; }

    public void addImage(ProductImage image) {
        images.add(image);
        image.setProduct(this);
    }

    public void addVariant(ProductVariant variant) {
        variants.add(variant);
        variant.setProduct(this);
    }

    public void addReview(ProductReview review) {
        reviews.add(review);
        review.setProduct(this);
    }
}
