package com.shop.carrito.config;

import com.shop.carrito.entity.*;
import com.shop.carrito.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class DataSeeder {

    @Bean
    @Transactional
    CommandLineRunner seedData(
            CategoryRepository categoryRepo,
            AttributeRepository attributeRepo,
            AttributeValueRepository attributeValueRepo,
            ProductRepository productRepo,
            ProductVariantRepository variantRepo
    ) {
        return args -> {
            if (categoryRepo.count() > 0 && productRepo.count() > 0) {
                return;
            }

            // 1. Categorías
            Category catRopa = categoryRepo.save(new Category("Ropa", "ropa", "Prendas de vestir urbanas y casuales", "https://images.unsplash.com/photo-1521572267360-ee0c2909d518?w=600"));
            Category catCalzado = categoryRepo.save(new Category("Calzado", "calzado", "Zapatillas urbanas, deportivas y calzado cómodo", "https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=600"));
            Category catAccesorios = categoryRepo.save(new Category("Accesorios", "accesorios", "Mochilas, gorras, billeteras y más", "https://images.unsplash.com/photo-1553062407-98eeb64c6a62?w=600"));
            Category catTecno = categoryRepo.save(new Category("Tecnología", "tecnologia", "Dispositivos inteligentes y accesorios tech", "https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=600"));
            Category catDeportes = categoryRepo.save(new Category("Deportes", "deportes", "Artículos para entrenamiento y vida activa", "https://images.unsplash.com/photo-1517838277536-f5f99be501cd?w=600"));

            // 2. Atributos
            Attribute attrTalla = attributeRepo.save(new Attribute("Talla"));
            Attribute attrColor = attributeRepo.save(new Attribute("Color"));

            // 3. Valores de Atributos
            Map<String, AttributeValue> tallas = new HashMap<>();
            for (String val : List.of("S", "M", "L", "XL", "39", "40", "41", "42", "Única")) {
                tallas.put(val, attributeValueRepo.save(new AttributeValue(attrTalla, val)));
            }

            Map<String, AttributeValue> colores = new HashMap<>();
            for (String val : List.of("Negro", "Blanco", "Azul", "Rojo", "Gris", "Verde")) {
                colores.put(val, attributeValueRepo.save(new AttributeValue(attrColor, val)));
            }

            // 4. Productos

            // --- Producto 1: Polo Básico ---
            Product p1 = new Product(
                    "Polo Básico Algodón Pima",
                    "polo-basico-algodon-pima",
                    "Polo 100% algodón pima peruano, suave al tacto y corte clásico regular fit.",
                    new BigDecimal("39.90"),
                    45,
                    "https://images.unsplash.com/photo-1521572267360-ee0c2909d518?w=800",
                    catRopa
            );
            p1.addImage(new ProductImage(p1, "https://images.unsplash.com/photo-1521572267360-ee0c2909d518?w=800", true, 1));
            p1.addImage(new ProductImage(p1, "https://images.unsplash.com/photo-1583743814966-8936f5b7be1a?w=800", false, 2));
            p1.addImage(new ProductImage(p1, "https://images.unsplash.com/photo-1503342217505-b0a15ec3261c?w=800", false, 3));

            ProductVariant p1v1 = new ProductVariant(p1, "POLO-NEG-S", new BigDecimal("39.90"), 10);
            p1v1.setAttributeValues(List.of(tallas.get("S"), colores.get("Negro")));
            p1.addVariant(p1v1);

            ProductVariant p1v2 = new ProductVariant(p1, "POLO-NEG-M", new BigDecimal("39.90"), 15);
            p1v2.setAttributeValues(List.of(tallas.get("M"), colores.get("Negro")));
            p1.addVariant(p1v2);

            ProductVariant p1v3 = new ProductVariant(p1, "POLO-BLA-M", new BigDecimal("39.90"), 12);
            p1v3.setAttributeValues(List.of(tallas.get("M"), colores.get("Blanco")));
            p1.addVariant(p1v3);

            ProductVariant p1v4 = new ProductVariant(p1, "POLO-AZU-L", new BigDecimal("39.90"), 8);
            p1v4.setAttributeValues(List.of(tallas.get("L"), colores.get("Azul")));
            p1.addVariant(p1v4);

            ProductReview p1r1 = new ProductReview(p1, "Carlos M.", 5, "Excelente polo, el algodón se siente premium y la talla M queda exacta.");
            p1r1.addPhoto(new ReviewPhoto(p1r1, "https://images.unsplash.com/photo-1521572267360-ee0c2909d518?w=400"));
            p1.addReview(p1r1);

            ProductReview p1r2 = new ProductReview(p1, "Andrea Gomez", 4, "Buena tela y acabado. Muy cómodo para el uso diario.");
            p1.addReview(p1r2);

            productRepo.save(p1);

            // --- Producto 2: Zapatillas Urbanas ---
            Product p2 = new Product(
                    "Zapatillas Urbanas Street Pro",
                    "zapatillas-urbanas-street-pro",
                    "Zapatillas con suela antideslizante y amortiguación de impacto, ideales para la ciudad.",
                    new BigDecimal("149.00"),
                    28,
                    "https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=800",
                    catCalzado
            );
            p2.addImage(new ProductImage(p2, "https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=800", true, 1));
            p2.addImage(new ProductImage(p2, "https://images.unsplash.com/photo-1608231387042-66d1773070a5?w=800", false, 2));

            ProductVariant p2v1 = new ProductVariant(p2, "ZAP-ROJ-40", new BigDecimal("149.00"), 8);
            p2v1.setAttributeValues(List.of(tallas.get("40"), colores.get("Rojo")));
            p2.addVariant(p2v1);

            ProductVariant p2v2 = new ProductVariant(p2, "ZAP-ROJ-41", new BigDecimal("149.00"), 10);
            p2v2.setAttributeValues(List.of(tallas.get("41"), colores.get("Rojo")));
            p2.addVariant(p2v2);

            ProductVariant p2v3 = new ProductVariant(p2, "ZAP-NEG-42", new BigDecimal("149.00"), 10);
            p2v3.setAttributeValues(List.of(tallas.get("42"), colores.get("Negro")));
            p2.addVariant(p2v3);

            ProductReview p2r1 = new ProductReview(p2, "Juan Diego", 5, "Las zapatillas son súper cómodas y el diseño resalta bastante.");
            p2r1.addPhoto(new ReviewPhoto(p2r1, "https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=400"));
            p2.addReview(p2r1);

            productRepo.save(p2);

            // --- Producto 3: Mochila 20L Impermeable ---
            Product p3 = new Product(
                    "Mochila Urbana Impermeable 20L",
                    "mochila-urbana-impermeable-20l",
                    "Compartimento para laptop de hasta 15.6 pulgadas, tejido impermeable y cierre antirrobo.",
                    new BigDecimal("89.50"),
                    22,
                    "https://images.unsplash.com/photo-1553062407-98eeb64c6a62?w=800",
                    catAccesorios
            );
            p3.addImage(new ProductImage(p3, "https://images.unsplash.com/photo-1553062407-98eeb64c6a62?w=800", true, 1));
            p3.addImage(new ProductImage(p3, "https://images.unsplash.com/photo-1581605405669-fcdf81165afa?w=800", false, 2));

            ProductVariant p3v1 = new ProductVariant(p3, "MOC-NEG-U", new BigDecimal("89.50"), 12);
            p3v1.setAttributeValues(List.of(tallas.get("Única"), colores.get("Negro")));
            p3.addVariant(p3v1);

            ProductVariant p3v2 = new ProductVariant(p3, "MOC-AZU-U", new BigDecimal("89.50"), 10);
            p3v2.setAttributeValues(List.of(tallas.get("Única"), colores.get("Azul")));
            p3.addVariant(p3v2);

            ProductReview p3r1 = new ProductReview(p3, "Lucia P.", 5, "La uso para ir a la universidad, entra mi laptop perfecta.");
            p3.addReview(p3r1);

            productRepo.save(p3);

            // --- Producto 4: Gorra Deportiva ---
            Product p4 = new Product(
                    "Gorra Deportiva Dry-Fit",
                    "gorra-deportiva-dry-fit",
                    "Gorra transpirable con banda absorbente y visera curva con ajuste regulable.",
                    new BigDecimal("29.00"),
                    40,
                    "https://images.unsplash.com/photo-1588850561407-ed78c282e89b?w=800",
                    catAccesorios
            );
            p4.addImage(new ProductImage(p4, "https://images.unsplash.com/photo-1588850561407-ed78c282e89b?w=800", true, 1));

            ProductVariant p4v1 = new ProductVariant(p4, "GOR-NEG-U", new BigDecimal("29.00"), 20);
            p4v1.setAttributeValues(List.of(tallas.get("Única"), colores.get("Negro")));
            p4.addVariant(p4v1);

            ProductVariant p4v2 = new ProductVariant(p4, "GOR-BLA-U", new BigDecimal("29.00"), 20);
            p4v2.setAttributeValues(List.of(tallas.get("Única"), colores.get("Blanco")));
            p4.addVariant(p4v2);

            productRepo.save(p4);

            // --- Producto 5: Smartwatch Deportivo ---
            Product p5 = new Product(
                    "Smartwatch Sport Fit 50m",
                    "smartwatch-sport-fit-50m",
                    "Monitor de frecuencia cardíaca, resistencia al agua 5 ATM y batería de 10 días de duración.",
                    new BigDecimal("119.90"),
                    15,
                    "https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=800",
                    catTecno
            );
            p5.addImage(new ProductImage(p5, "https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=800", true, 1));
            p5.addImage(new ProductImage(p5, "https://images.unsplash.com/photo-1508685096489-7aacd43bd3b1?w=800", false, 2));

            ProductVariant p5v1 = new ProductVariant(p5, "WAT-NEG-U", new BigDecimal("119.90"), 10);
            p5v1.setAttributeValues(List.of(tallas.get("Única"), colores.get("Negro")));
            p5.addVariant(p5v1);

            ProductVariant p5v2 = new ProductVariant(p5, "WAT-GRI-U", new BigDecimal("119.90"), 5);
            p5v2.setAttributeValues(List.of(tallas.get("Única"), colores.get("Gris")));
            p5.addVariant(p5v2);

            ProductReview p5r1 = new ProductReview(p5, "Renato S.", 5, "Gran relación calidad-precio. Las notificaciones llegan en tiempo real.");
            p5r1.addPhoto(new ReviewPhoto(p5r1, "https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=400"));
            p5.addReview(p5r1);

            productRepo.save(p5);

            // --- Producto 6: Casaca Denim ---
            Product p6 = new Product(
                    "Casaca Denim Vintage Blue",
                    "casaca-denim-vintage-blue",
                    "Casaca de jean clásico prelavado con botones metálicos y bolsillos frontales.",
                    new BigDecimal("179.00"),
                    18,
                    "https://images.unsplash.com/photo-1576995853123-5a10305d93c0?w=800",
                    catRopa
            );
            p6.addImage(new ProductImage(p6, "https://images.unsplash.com/photo-1576995853123-5a10305d93c0?w=800", true, 1));

            ProductVariant p6v1 = new ProductVariant(p6, "CAS-AZU-M", new BigDecimal("179.00"), 8);
            p6v1.setAttributeValues(List.of(tallas.get("M"), colores.get("Azul")));
            p6.addVariant(p6v1);

            ProductVariant p6v2 = new ProductVariant(p6, "CAS-AZU-L", new BigDecimal("179.00"), 10);
            p6v2.setAttributeValues(List.of(tallas.get("L"), colores.get("Azul")));
            p6.addVariant(p6v2);

            productRepo.save(p6);

            // --- Producto 7: Audífonos Inalámbricos ---
            Product p7 = new Product(
                    "Audífonos Inalámbricos True Bass",
                    "audifonos-inalambricos-true-bass",
                    "Cancelación pasiva de ruido, Bluetooth 5.3 y estuche con carga rápida USB-C.",
                    new BigDecimal("79.90"),
                    35,
                    "https://images.unsplash.com/photo-1590658268037-6bf12165a8df?w=800",
                    catTecno
            );
            p7.addImage(new ProductImage(p7, "https://images.unsplash.com/photo-1590658268037-6bf12165a8df?w=800", true, 1));

            ProductVariant p7v1 = new ProductVariant(p7, "AUD-NEG-U", new BigDecimal("79.90"), 20);
            p7v1.setAttributeValues(List.of(tallas.get("Única"), colores.get("Negro")));
            p7.addVariant(p7v1);

            ProductVariant p7v2 = new ProductVariant(p7, "AUD-BLA-U", new BigDecimal("79.90"), 15);
            p7v2.setAttributeValues(List.of(tallas.get("Única"), colores.get("Blanco")));
            p7.addVariant(p7v2);

            productRepo.save(p7);

            // --- Producto 8: Zapatillas Running Speed ---
            Product p8 = new Product(
                    "Zapatillas Running Speed Boost",
                    "zapatillas-running-speed-boost",
                    "Diseñadas para maratón y entrenamientos de alta intensidad con retorno de energía.",
                    new BigDecimal("219.00"),
                    14,
                    "https://images.unsplash.com/photo-1584735935682-2f2b69dff9d2?w=800",
                    catCalzado
            );
            p8.addImage(new ProductImage(p8, "https://images.unsplash.com/photo-1584735935682-2f2b69dff9d2?w=800", true, 1));

            ProductVariant p8v1 = new ProductVariant(p8, "RUN-NEG-41", new BigDecimal("219.00"), 6);
            p8v1.setAttributeValues(List.of(tallas.get("41"), colores.get("Negro")));
            p8.addVariant(p8v1);

            ProductVariant p8v2 = new ProductVariant(p8, "RUN-AZU-42", new BigDecimal("219.00"), 8);
            p8v2.setAttributeValues(List.of(tallas.get("42"), colores.get("Azul")));
            p8.addVariant(p8v2);

            productRepo.save(p8);

            // --- Producto 9: Pantalón Jogger Training ---
            Product p9 = new Product(
                    "Pantalón Jogger Training Slim",
                    "pantalon-jogger-training-slim",
                    "Tejido elástico de alta flexibilidad, bolsillos laterales con cremallera.",
                    new BigDecimal("69.90"),
                    26,
                    "https://images.unsplash.com/photo-1552902865-b72c031ac5ea?w=800",
                    catDeportes
            );
            p9.addImage(new ProductImage(p9, "https://images.unsplash.com/photo-1552902865-b72c031ac5ea?w=800", true, 1));

            ProductVariant p9v1 = new ProductVariant(p9, "JOG-NEG-M", new BigDecimal("69.90"), 12);
            p9v1.setAttributeValues(List.of(tallas.get("M"), colores.get("Negro")));
            p9.addVariant(p9v1);

            ProductVariant p9v2 = new ProductVariant(p9, "JOG-GRI-L", new BigDecimal("69.90"), 14);
            p9v2.setAttributeValues(List.of(tallas.get("L"), colores.get("Gris")));
            p9.addVariant(p9v2);

            productRepo.save(p9);

            // --- Producto 10: Botella Térmica 1L ---
            Product p10 = new Product(
                    "Botella Térmica Inox 1L",
                    "botella-termica-inox-1l",
                    "Mantiene bebidas frías por 24 horas y calientes por 12 horas. Libre de BPA.",
                    new BigDecimal("49.90"),
                    30,
                    "https://images.unsplash.com/photo-1602143407151-7111542de6e8?w=800",
                    catDeportes
            );
            p10.addImage(new ProductImage(p10, "https://images.unsplash.com/photo-1602143407151-7111542de6e8?w=800", true, 1));

            ProductVariant p10v1 = new ProductVariant(p10, "BOT-NEG-U", new BigDecimal("49.90"), 15);
            p10v1.setAttributeValues(List.of(tallas.get("Única"), colores.get("Negro")));
            p10.addVariant(p10v1);

            ProductVariant p10v2 = new ProductVariant(p10, "BOT-AZU-U", new BigDecimal("49.90"), 15);
            p10v2.setAttributeValues(List.of(tallas.get("Única"), colores.get("Azul")));
            p10.addVariant(p10v2);

            productRepo.save(p10);
        };
    }
}
