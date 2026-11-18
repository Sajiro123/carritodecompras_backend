package com.shop.carrito.repository;

import com.shop.carrito.entity.Politica;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PoliticaRepository extends JpaRepository<Politica, Long> {
    Optional<Politica> findBySlug(String slug);
}
