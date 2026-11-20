package com.shop.carrito.repository;

import com.shop.carrito.entity.Category;
import com.shop.carrito.entity.Product;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    List<Product> findByActiveTrue();

    Optional<Product> findByIdAndActiveTrue(Long id);

    Optional<Product> findBySlugAndActiveTrue(String slug);

    List<Product> findTop4ByCategoryAndActiveTrueAndIdNot(Category category, Long id);

    List<Product> findTop4ByActiveTrueAndIdNot(Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Product p WHERE p.id = :id AND p.active = true")
    Optional<Product> findActiveByIdForUpdate(@Param("id") Long id);

    @Query("SELECT MIN(p.price) FROM Product p WHERE p.active = true")
    BigDecimal findMinPrice();

    @Query("SELECT MAX(p.price) FROM Product p WHERE p.active = true")
    BigDecimal findMaxPrice();

    @Query("SELECT p.olfactoryFamily, COUNT(p) FROM Product p WHERE p.active = true AND p.olfactoryFamily IS NOT NULL GROUP BY p.olfactoryFamily ORDER BY COUNT(p) DESC, p.olfactoryFamily ASC")
    List<Object[]> countByOlfactoryFamily();

    @Query("SELECT p.brand, COUNT(p) FROM Product p WHERE p.active = true AND p.brand IS NOT NULL GROUP BY p.brand ORDER BY COUNT(p) DESC, p.brand ASC")
    List<Object[]> countByBrand();

    @Query("SELECT DISTINCT p.concentration FROM Product p WHERE p.active = true AND p.concentration IS NOT NULL ORDER BY p.concentration ASC")
    List<String> findDistinctConcentrations();

    @Query("SELECT DISTINCT p.formatType FROM Product p WHERE p.active = true AND p.formatType IS NOT NULL ORDER BY p.formatType ASC")
    List<String> findDistinctFormatTypes();

    @Query("SELECT DISTINCT p.gender FROM Product p WHERE p.active = true AND p.gender IS NOT NULL ORDER BY p.gender ASC")
    List<String> findDistinctGenders();

    @Query("SELECT p.occasion FROM Product p WHERE p.active = true AND p.occasion IS NOT NULL")
    List<String> findAllOccasionStrings();
}
