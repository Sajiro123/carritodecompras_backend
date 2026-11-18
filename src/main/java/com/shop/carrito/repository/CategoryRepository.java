package com.shop.carrito.repository;

import com.shop.carrito.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findByActiveTrue();

    Optional<Category> findBySlugAndActiveTrue(String slug);

    @Query("""
        SELECT c.id AS id,
               c.name AS name,
               c.slug AS slug,
               c.description AS description,
               c.imageUrl AS imageUrl,
               COUNT(p.id) AS productCount
        FROM Category c
        LEFT JOIN Product p ON p.category = c AND p.active = true
        WHERE c.active = true
        GROUP BY c.id, c.name, c.slug, c.description, c.imageUrl
        ORDER BY c.id ASC
    """)
    List<CategoryWithCountProjection> findCategoriesWithProductCount();

    interface CategoryWithCountProjection {
        Long getId();
        String getName();
        String getSlug();
        String getDescription();
        String getImageUrl();
        Long getProductCount();
    }
}
