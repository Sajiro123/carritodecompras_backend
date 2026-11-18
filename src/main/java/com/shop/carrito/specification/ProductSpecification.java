package com.shop.carrito.specification;

import com.shop.carrito.entity.*;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
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

            // Filtro por categoría (soporta múltiples categorías separadas por coma o slug único)
            if (categoria != null && !categoria.isBlank()) {
                Join<Product, Category> catJoin = root.join("category", JoinType.LEFT);
                List<String> catList = Arrays.stream(categoria.split(","))
                        .map(String::trim)
                        .map(String::toLowerCase)
                        .filter(s -> !s.isEmpty())
                        .toList();

                if (!catList.isEmpty()) {
                    List<Predicate> catPredicates = new ArrayList<>();
                    for (String catItem : catList) {
                        if (catItem.matches("\\d+")) {
                            catPredicates.add(cb.equal(catJoin.get("id"), Long.parseLong(catItem)));
                        } else {
                            catPredicates.add(cb.equal(cb.lower(catJoin.get("slug")), catItem));
                        }
                    }
                    predicates.add(cb.or(catPredicates.toArray(new Predicate[0])));
                }
            }

            // Filtro por talla (soporta selección múltiple: M,L,XL a través de variantes)
            if (talla != null && !talla.isBlank()) {
                Join<Product, ProductVariant> variantJoin = root.join("variants", JoinType.INNER);
                Join<ProductVariant, AttributeValue> attrJoin = variantJoin.join("attributeValues", JoinType.INNER);
                Join<AttributeValue, Attribute> metaJoin = attrJoin.join("attribute", JoinType.INNER);

                List<String> sizeList = Arrays.stream(talla.split(","))
                        .map(String::trim)
                        .map(String::toLowerCase)
                        .filter(s -> !s.isEmpty())
                        .toList();

                if (!sizeList.isEmpty()) {
                    predicates.add(cb.and(
                        cb.equal(cb.lower(metaJoin.get("name")), "talla"),
                        cb.lower(attrJoin.get("valor")).in(sizeList)
                    ));
                }
            }

            // Filtro por color (soporta selección múltiple: Negro,Azul a través de variantes)
            if (color != null && !color.isBlank()) {
                Join<Product, ProductVariant> variantJoin = root.join("variants", JoinType.INNER);
                Join<ProductVariant, AttributeValue> attrJoin = variantJoin.join("attributeValues", JoinType.INNER);
                Join<AttributeValue, Attribute> metaJoin = attrJoin.join("attribute", JoinType.INNER);

                List<String> colorList = Arrays.stream(color.split(","))
                        .map(String::trim)
                        .map(String::toLowerCase)
                        .filter(s -> !s.isEmpty())
                        .toList();

                if (!colorList.isEmpty()) {
                    predicates.add(cb.and(
                        cb.equal(cb.lower(metaJoin.get("name")), "color"),
                        cb.lower(attrJoin.get("valor")).in(colorList)
                    ));
                }
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
