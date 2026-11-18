package com.shop.carrito.repository;

import com.shop.carrito.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByCodigoSeguimientoIgnoreCase(String codigoSeguimiento);
    Optional<Order> findByCodigoSeguimientoIgnoreCaseAndCustomerPhone(String codigoSeguimiento, String customerPhone);
}
