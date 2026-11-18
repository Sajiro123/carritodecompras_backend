package com.shop.carrito.repository;

import com.shop.carrito.entity.AgenciaEnvio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AgenciaEnvioRepository extends JpaRepository<AgenciaEnvio, Long> {

    List<AgenciaEnvio> findByActivoTrueOrderByDepartamentoAscProvinciaAscDistritoAsc();

    @Query("SELECT a FROM AgenciaEnvio a WHERE a.activo = true AND (" +
           "LOWER(a.nombre) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
           "LOWER(a.direccion) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
           "LOWER(a.distrito) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
           "LOWER(a.provincia) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
           "LOWER(a.departamento) LIKE LOWER(CONCAT('%', :q, '%'))) " +
           "ORDER BY a.departamento ASC, a.distrito ASC")
    List<AgenciaEnvio> searchByQuery(@Param("q") String q);
}
