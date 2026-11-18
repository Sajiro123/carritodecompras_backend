package com.shop.carrito.repository;

import com.shop.carrito.entity.Ubigeo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UbigeoRepository extends JpaRepository<Ubigeo, String> {

    @Query("SELECT u FROM Ubigeo u WHERE " +
           "LOWER(u.distrito) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
           "LOWER(u.provincia) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
           "LOWER(u.departamento) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
           "u.codigo LIKE CONCAT('%', :q, '%') " +
           "ORDER BY u.departamento ASC, u.provincia ASC, u.distrito ASC")
    List<Ubigeo> searchByQuery(@Param("q") String q);
}
