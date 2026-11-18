package com.shop.carrito.config;

import com.shop.carrito.entity.Product;
import com.shop.carrito.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.List;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seedProducts(ProductRepository repo) {
        return args -> {
            if (repo.count() > 0) return;

            repo.saveAll(List.of(
                    new Product("Polo básico", "Algodón 100%, color negro", new BigDecimal("39.90"), 50, "https://picsum.photos/seed/polo/400/300"),
                    new Product("Zapatillas urbanas", "Cómodas para el día a día", new BigDecimal("149.00"), 20, "https://picsum.photos/seed/shoes/400/300"),
                    new Product("Mochila 20L", "Resistente al agua", new BigDecimal("89.50"), 15, "https://picsum.photos/seed/bag/400/300"),
                    new Product("Gorra deportiva", "Ajustable", new BigDecimal("29.00"), 40, "https://picsum.photos/seed/cap/400/300"),
                    new Product("Reloj digital", "Sumergible hasta 50m", new BigDecimal("119.90"), 10, "https://picsum.photos/seed/watch/400/300")
            ));
        };
    }
}
