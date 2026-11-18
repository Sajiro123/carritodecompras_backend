package com.shop.carrito.repository;

import com.shop.carrito.entity.Attribute;
import com.shop.carrito.entity.AttributeValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AttributeValueRepository extends JpaRepository<AttributeValue, Long> {
    Optional<AttributeValue> findByAttributeAndValorIgnoreCase(Attribute attribute, String valor);

    @Query("SELECT DISTINCT av.valor FROM AttributeValue av WHERE LOWER(av.attribute.name) = LOWER(:attrName) ORDER BY av.valor ASC")
    List<String> findDistinctValuesByAttributeName(@Param("attrName") String attrName);
}
