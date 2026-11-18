package com.shop.carrito.specification;

import com.shop.carrito.entity.*;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ProductSpecification {

    public static Specification<Product> filter(
            String categoria,
            String talla,
            String color,
            BigDecimal minPrecio,
            BigDecimal maxPrecio,
            Boolean disponible,
            String q
    ) {
        return (root, query, cb) -> {
            query.distinct(true);
            List<Predicate> predicates = new ArrayList<>();

            // Siempre productos activos
            predicates.add(cb.isTrue(root.get("active")));

            // Filtro por categoría (por slug o por ID numérico)
            if (categoria != null && !categoria.isBlank()) {
                Join<Product, Category> catJoin = root.join("category", JoinType.LEFT);
                String catClean = categoria.trim().toLowerCase();
                if (catClean.matches("\\d+")) {
                    predicates.add(cb.or(
                        cb.equal(catJoin.get("id"), Long.parseLong(catClean)),
                        cb.equal(cb.lower(catJoin.get("slug")), catClean)
                    ));
                } else {
                    predicates.add(cb.equal(cb.lower(catJoin.get("slug")), catClean));
                }
            }

            // Filtro por talla (a través de variantes y atributo_valor)
            if (talla != null && !talla.isBlank()) {
                Join<Product, ProductVariant> variantJoin = root.join("variants", JoinType.INNER);
                Join<ProductVariant, AttributeValue> attrJoin = variantJoin.join("attributeValues", JoinType.INNER);
                Join<AttributeValue, Attribute> metaJoin = attrJoin.join("attribute", JoinType.INNER);
                predicates.add(cb.and(
                    cb.equal(cb.lower(metaJoin.get("name")), "talla"),
                    cb.equal(cb.lower(attrJoin.get("valor")), talla.trim().toLowerCase())
                ));
            }

            // Filtro por color (a través de variantes y atributo_valor)
            if (color != null && !color.isBlank()) {
                Join<Product, ProductVariant> variantJoin = root.join("variants", JoinType.INNER);
                Join<ProductVariant, AttributeValue> attrJoin = variantJoin.join("attributeValues", JoinType.INNER);
                Join<AttributeValue, Attribute> metaJoin = attrJoin.join("attribute", JoinType.INNER);
                predicates.add(cb.and(
                    cb.equal(cb.lower(metaJoin.get("name")), "color"),
                    cb.equal(cb.lower(attrJoin.get("valor")), color.trim().toLowerCase())
                ));
            }

            // Filtro por precio mínimo
            if (minPrecio != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("price"), minPrecio));
            }

            // Filtro por precio máximo
            if (maxPrecio != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("price"), maxPrecio));
            }

            // Filtro por disponibilidad
            if (disponible != null) {
                if (disponible) {
                    predicates.add(cb.greaterThan(root.get("stock"), 0));
                } else {
                    predicates.add(cb.equal(root.get("stock"), 0));
                }
            }

            // Búsqueda por texto libre en nombre o descripción
            if (q != null && !q.isBlank()) {
                String pattern = "%" + q.trim().toLowerCase() + "%";
                predicates.add(cb.or(
                    cb.like(cb.lower(root.get("name")), pattern),
                    cb.like(cb.lower(root.get("description")), pattern)
                ));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
