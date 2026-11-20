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
            String q,
            String marca,
            String familia,
            String concentracion,
            String ocasion,
            String formato,
            String genero,
            Boolean nicho,
            Boolean soloDescuento,
            Boolean entrega24h
    ) {
        return (root, query, cb) -> {
            query.distinct(true);
            List<Predicate> predicates = new ArrayList<>();

            // Siempre productos activos
            predicates.add(cb.isTrue(root.get("active")));

            // Filtro por categoría (soporta múltiples categorías separadas por coma o slug único)
            if (categoria != null && !categoria.isBlank()) {
                Join<Product, Category> catJoin = root.join("category", JoinType.LEFT);
                List<String> catList = splitLower(categoria);

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

            // Filtro por talla / presentación (compatibilidad con atributos de variantes)
            if (talla != null && !talla.isBlank()) {
                Join<Product, ProductVariant> variantJoin = root.join("variants", JoinType.INNER);
                Join<ProductVariant, AttributeValue> attrJoin = variantJoin.join("attributeValues", JoinType.INNER);
                Join<AttributeValue, Attribute> metaJoin = attrJoin.join("attribute", JoinType.INNER);

                List<String> sizeList = splitLower(talla);
                if (!sizeList.isEmpty()) {
                    predicates.add(cb.and(
                        cb.or(
                            cb.equal(cb.lower(metaJoin.get("name")), "talla"),
                            cb.equal(cb.lower(metaJoin.get("name")), "presentación")
                        ),
                        cb.lower(attrJoin.get("valor")).in(sizeList)
                    ));
                }
            }

            // Filtro por color (compatibilidad con variantes)
            if (color != null && !color.isBlank()) {
                Join<Product, ProductVariant> variantJoin = root.join("variants", JoinType.INNER);
                Join<ProductVariant, AttributeValue> attrJoin = variantJoin.join("attributeValues", JoinType.INNER);
                Join<AttributeValue, Attribute> metaJoin = attrJoin.join("attribute", JoinType.INNER);

                List<String> colorList = splitLower(color);
                if (!colorList.isEmpty()) {
                    predicates.add(cb.and(
                        cb.equal(cb.lower(metaJoin.get("name")), "color"),
                        cb.lower(attrJoin.get("valor")).in(colorList)
                    ));
                }
            }

            // Filtro por marca (multi-selección)
            if (marca != null && !marca.isBlank()) {
                List<String> brandList = splitLower(marca);
                if (!brandList.isEmpty()) {
                    predicates.add(cb.lower(root.get("brand")).in(brandList));
                }
            }

            // Filtro por familia olfativa (multi-selección)
            if (familia != null && !familia.isBlank()) {
                List<String> famList = splitLower(familia);
                if (!famList.isEmpty()) {
                    predicates.add(cb.lower(root.get("olfactoryFamily")).in(famList));
                }
            }

            // Filtro por concentración (EDP, Extrait, EDT, Elixir)
            if (concentracion != null && !concentracion.isBlank()) {
                List<String> concList = splitLower(concentracion);
                if (!concList.isEmpty()) {
                    predicates.add(cb.lower(root.get("concentration")).in(concList));
                }
            }

            // Filtro por ocasión (multi-selección sobre texto con múltiples ocasiones)
            if (ocasion != null && !ocasion.isBlank()) {
                List<String> occList = splitLower(ocasion);
                if (!occList.isEmpty()) {
                    List<Predicate> occPreds = new ArrayList<>();
                    for (String occ : occList) {
                        occPreds.add(cb.like(cb.lower(root.get("occasion")), "%" + occ + "%"));
                    }
                    predicates.add(cb.or(occPreds.toArray(new Predicate[0])));
                }
            }

            // Filtro por formato & tamaño
            if (formato != null && !formato.isBlank()) {
                List<String> fmtList = splitLower(formato);
                if (!fmtList.isEmpty()) {
                    predicates.add(cb.lower(root.get("formatType")).in(fmtList));
                }
            }

            // Filtro por género (Hombre incluye Hombre + Unisex; Mujer incluye Mujer + Unisex)
            if (genero != null && !genero.isBlank()) {
                List<String> genList = splitLower(genero);
                if (!genList.isEmpty()) {
                    List<Predicate> genPreds = new ArrayList<>();
                    for (String g : genList) {
                        if ("hombre".equals(g)) {
                            genPreds.add(cb.lower(root.get("gender")).in(List.of("hombre", "unisex")));
                        } else if ("mujer".equals(g)) {
                            genPreds.add(cb.lower(root.get("gender")).in(List.of("mujer", "unisex")));
                        } else {
                            genPreds.add(cb.equal(cb.lower(root.get("gender")), g));
                        }
                    }
                    predicates.add(cb.or(genPreds.toArray(new Predicate[0])));
                }
            }

            // Filtro por perfumería nicho
            if (Boolean.TRUE.equals(nicho)) {
                predicates.add(cb.isTrue(root.get("isNiche")));
            }

            // Filtro solo con descuento
            if (Boolean.TRUE.equals(soloDescuento)) {
                predicates.add(cb.and(
                    cb.isNotNull(root.get("originalPrice")),
                    cb.greaterThan(root.get("originalPrice"), root.get("price"))
                ));
            }

            // Filtro En Stock Lima Entrega 24h
            if (Boolean.TRUE.equals(entrega24h)) {
                predicates.add(cb.and(
                    cb.isTrue(root.get("fastDeliveryLima")),
                    cb.greaterThan(root.get("stock"), 0)
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

            // Búsqueda por texto libre en nombre, marca, notas olfativas, familia o descripción
            if (q != null && !q.isBlank()) {
                String pattern = "%" + q.trim().toLowerCase() + "%";
                predicates.add(cb.or(
                    cb.like(cb.lower(root.get("name")), pattern),
                    cb.like(cb.lower(cb.coalesce(root.get("brand"), "")), pattern),
                    cb.like(cb.lower(cb.coalesce(root.get("notes"), "")), pattern),
                    cb.like(cb.lower(cb.coalesce(root.get("olfactoryFamily"), "")), pattern),
                    cb.like(cb.lower(cb.coalesce(root.get("occasion"), "")), pattern),
                    cb.like(cb.lower(cb.coalesce(root.get("description"), "")), pattern)
                ));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private static List<String> splitLower(String csv) {
        return Arrays.stream(csv.split(","))
                .map(String::trim)
                .map(String::toLowerCase)
                .filter(s -> !s.isEmpty())
                .toList();
    }
}
