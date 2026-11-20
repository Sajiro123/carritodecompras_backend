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
            ProductRepository productRepo
    ) {
        return args -> {
            if (categoryRepo.count() > 0 && productRepo.count() > 0) {
                return;
            }

            // 1. Categorías de Alta Perfumería
            Category catNicho = categoryRepo.save(new Category(
                    "Perfumería Nicho",
                    "perfumeria-nicho",
                    "Casas independientes de alta perfumería con materias primas excepcionales",
                    "https://images.unsplash.com/photo-1594035910387-fea47794261f?w=600&q=80"
            ));
            Category catHombre = categoryRepo.save(new Category(
                    "Hombre",
                    "hombre",
                    "Composiciones masculinas amaderadas, especiadas y aromáticas",
                    "https://images.unsplash.com/photo-1523293182086-7651a899d37f?w=600&q=80"
            ));
            Category catMujer = categoryRepo.save(new Category(
                    "Mujer",
                    "mujer",
                    "Fragancias femeninas florales, orientales y gourmand de alta estela",
                    "https://images.unsplash.com/photo-1541643600914-78b084683601?w=600&q=80"
            ));
            Category catOcasiones = categoryRepo.save(new Category(
                    "Ocasiones Especiales",
                    "ocasiones-especiales",
                    "Extractos puros y eaux de parfum de magnetismo nocturno y gala",
                    "https://images.unsplash.com/photo-1592945403244-b3fbafd7f539?w=600&q=80"
            ));
            Category catDecants = categoryRepo.save(new Category(
                    "Decants & Sets",
                    "decants-sets",
                    "Decants oficiales certificados de 5ml, 10ml y Discovery Sets curados",
                    "https://images.unsplash.com/photo-1588405748880-12d1d2a59f75?w=600&q=80"
            ));

            // 2. Atributos de Perfumería
            Attribute attrPresentacion = attributeRepo.save(new Attribute("Presentación"));
            Attribute attrConcentracion = attributeRepo.save(new Attribute("Concentración"));

            Map<String, AttributeValue> presentaciones = new HashMap<>();
            for (String val : List.of("Frasco 125ml", "Frasco 100ml", "Frasco 90ml", "Frasco 75ml", "Frasco 70ml", "Frasco 60ml", "Decant 10ml", "Muestra 5ml", "Set 5 × 5ml")) {
                presentaciones.put(val, attributeValueRepo.save(new AttributeValue(attrPresentacion, val)));
            }

            Map<String, AttributeValue> concentraciones = new HashMap<>();
            for (String val : List.of("EDP", "Extrait", "EDT", "Elixir")) {
                concentraciones.put(val, attributeValueRepo.save(new AttributeValue(attrConcentracion, val)));
            }

            // 3. Catálogo Curado de Alta Perfumería (14 fragancias)

            // 1. Parfums de Marly - Layton
            Product p1 = buildPerfume(
                    "Layton Eau de Parfum",
                    "layton-eau-de-parfum",
                    "Una obra maestra amaderada y oriental que abre con manzana verde crujiente, bergamota y lavanda, reposando sobre un corazón especiado de cardamomo y una base adictiva de vainilla Bourbon y sándalo.",
                    "1090.00", "1250.00", "65.00", 14,
                    "https://images.unsplash.com/photo-1594035910387-fea47794261f?w=800&q=80",
                    catOcasiones, "Parfums de Marly", "Amaderado", "EDP",
                    "Noche & Gala, Citas Románticas", "125 ML EDP", "Frasco 100ml+",
                    "Manzana crujiente, Vainilla Bourbon, Cardamomo, Sándalo",
                    "BEST SELLER", "dark", "4.9", 154, "Hombre", true, true
            );
            p1.addImage(new ProductImage(p1, "https://images.unsplash.com/photo-1594035910387-fea47794261f?w=800&q=80", true, 1));
            p1.addImage(new ProductImage(p1, "https://images.unsplash.com/photo-1523293182086-7651a899d37f?w=800&q=80", false, 2));
            addPerfumeVariants(p1, "PDM-LAY", "Frasco 125ml", "EDP", "1090.00", "115.00", "65.00", presentaciones, concentraciones);
            p1.addReview(new ProductReview(p1, "Sebastián V.", 5, "Proyección increíble durante más de 10 horas. Llegó sellado con código de lote verificable en Lima."));
            p1.addReview(new ProductReview(p1, "Diego R.", 5, "Probé primero la muestra de 5ml y terminé comprando el frasco de 125ml. Elegancia pura para noches de gala."));
            productRepo.save(p1);

            // 2. Tom Ford - Tobacco Vanille
            Product p2 = buildPerfume(
                    "Tobacco Vanille Eau de Parfum",
                    "tobacco-vanille-eau-de-parfum",
                    "Opulento, cálido e icónico. Evoca los clubes privados londinenses con hoja de tabaco aromático, especias nobles, cacao cremoso, frutos secos y vainilla de Madagascar.",
                    "1450.00", null, "85.00", 9,
                    "https://images.unsplash.com/photo-1592945403244-b3fbafd7f539?w=800&q=80",
                    catOcasiones, "Tom Ford", "Oriental / Especiado", "EDP",
                    "Noche & Gala, Citas Románticas", "100 ML EDP", "Frasco 100ml+",
                    "Hoja de tabaco especiado, Flor de tabaco, Vainilla, Cacao",
                    "OCASIÓN NOCHE", "gold", "4.9", 138, "Unisex", true, true
            );
            p2.addImage(new ProductImage(p2, "https://images.unsplash.com/photo-1592945403244-b3fbafd7f539?w=800&q=80", true, 1));
            p2.addImage(new ProductImage(p2, "https://images.unsplash.com/photo-1587017539504-67cfbddac569?w=800&q=80", false, 2));
            addPerfumeVariants(p2, "TF-TOBVAN", "Frasco 100ml", "EDP", "1450.00", "150.00", "85.00", presentaciones, concentraciones);
            p2.addReview(new ProductReview(p2, "Valeria M.", 5, "Un clásico absoluto de invierno y noche. Dos atomizaciones bastan para destacar toda la velada."));
            productRepo.save(p2);

            // 3. Dior - Sauvage Elixir
            Product p3 = buildPerfume(
                    "Sauvage Elixir",
                    "sauvage-elixir",
                    "Concentración extraordinaria comparable a un licor fino. Una estela embriagadora de canela, nuez moscada, esencia de lavanda hecha a medida y maderas licorosas.",
                    "680.00", "800.00", "55.00", 22,
                    "https://images.unsplash.com/photo-1523293182086-7651a899d37f?w=800&q=80",
                    catHombre, "Dior", "Oriental / Especiado", "Elixir",
                    "Noche & Gala, Club & Fiesta", "60 ML ELIXIR", "Frasco 50-90ml",
                    "Canela, Nuez moscada, Lavanda fina, Sándalo y Regaliz",
                    "-15% OFF", "red", "4.8", 210, "Hombre", false, true
            );
            p3.addImage(new ProductImage(p3, "https://images.unsplash.com/photo-1523293182086-7651a899d37f?w=800&q=80", true, 1));
            p3.addImage(new ProductImage(p3, "https://images.unsplash.com/photo-1563170351-be82bc888aa4?w=800&q=80", false, 2));
            addPerfumeVariants(p3, "DIOR-ELIX", "Frasco 60ml", "Elixir", "680.00", "98.00", "55.00", presentaciones, concentraciones);
            p3.addReview(new ProductReview(p3, "Martín C.", 5, "Duración bestial en piel y ropa. Entrega express en San Isidro en menos de 24 horas."));
            productRepo.save(p3);

            // 4. Nishane - Ani Extrait de Parfum
            Product p4 = buildPerfume(
                    "Ani Extrait de Parfum",
                    "ani-extrait-de-parfum",
                    "Extracto de perfume de la casa turca Nishane. Contrasta una salida chispeante de jengibre y bergamota con la vainilla más refinada, benjuí y maderas cremosas.",
                    "980.00", null, "60.00", 11,
                    "https://images.unsplash.com/photo-1587017539504-67cfbddac569?w=800&q=80",
                    catNicho, "Nishane", "Gourmand", "Extrait",
                    "Citas Románticas, Noche & Gala", "100 ML EXTRAIT", "Frasco 100ml+",
                    "Jengibre picante, Bergamota, Vainilla turca, Cardamomo",
                    "PERFUMERÍA NICHO", "soft", "4.9", 87, "Unisex", true, true
            );
            p4.addImage(new ProductImage(p4, "https://images.unsplash.com/photo-1587017539504-67cfbddac569?w=800&q=80", true, 1));
            addPerfumeVariants(p4, "NISH-ANI", "Frasco 100ml", "Extrait", "980.00", "110.00", "60.00", presentaciones, concentraciones);
            p4.addReview(new ProductReview(p4, "Camila F.", 5, "La mejor vainilla especiada que he probado. Totalmente unisex y con fijación eterna."));
            productRepo.save(p4);

            // 5. Creed - Aventus Millésime
            Product p5 = buildPerfume(
                    "Aventus Millésime",
                    "aventus-millesime",
                    "El ícono mundial de la alta perfumería masculina. Combina piña jugosa, grosella negra y bergamota con un corazón ahumado de abedul seco, pachulí y auténtico ámbar gris.",
                    "1390.00", null, "80.00", 8,
                    "https://images.unsplash.com/photo-1563170351-be82bc888aa4?w=800&q=80",
                    catNicho, "Creed", "Cítrico Fresco", "EDP",
                    "Oficina / Firma, Noche & Gala, Verano & Fresco", "100 ML MILLÉSIME", "Frasco 100ml+",
                    "Piña ahumada, Abedul seco, Grosella negra, Ámbar gris",
                    "ÍCONO MUNDIAL", "dark", "5.0", 342, "Hombre", true, true
            );
            p5.addImage(new ProductImage(p5, "https://images.unsplash.com/photo-1563170351-be82bc888aa4?w=800&q=80", true, 1));
            addPerfumeVariants(p5, "CRD-AVEN", "Frasco 100ml", "EDP", "1390.00", "145.00", "80.00", presentaciones, concentraciones);
            p5.addReview(new ProductReview(p5, "Alejandro T.", 5, "Batch reciente con excelente balance frutal y ahumado. 100% original verificado."));
            productRepo.save(p5);

            // 6. Xerjoff 1861 - Naxos
            Product p6 = buildPerfume(
                    "Naxos Eau de Parfum",
                    "naxos-eau-de-parfum",
                    "Tributo italiano al esplendor de Sicilia. Miel blanca artesanal fundida con lavanda fresca, hoja de tabaco dorado, canela y haba tonka envolvente.",
                    "990.00", null, "62.00", 13,
                    "https://images.unsplash.com/photo-1615634260167-c8cdede054de?w=800&q=80",
                    catNicho, "Xerjoff 1861", "Gourmand", "EDP",
                    "Noche & Gala, Citas Románticas", "100 ML EDP", "Frasco 100ml+",
                    "Miel blanca pura, Hoja de tabaco, Lavanda siciliana, Tonka",
                    "PERFUMERÍA NICHO", "soft", "4.9", 98, "Unisex", true, true
            );
            p6.addImage(new ProductImage(p6, "https://images.unsplash.com/photo-1615634260167-c8cdede054de?w=800&q=80", true, 1));
            addPerfumeVariants(p6, "XERJ-NAX", "Frasco 100ml", "EDP", "990.00", "112.00", "62.00", presentaciones, concentraciones);
            p6.addReview(new ProductReview(p6, "Renato L.", 5, "Presentación impecable de Xerjoff y aroma hipnótico. Recibí cumplidos desde el primer día."));
            productRepo.save(p6);

            // 7. Yves Saint Laurent - Bleu Électrique
            Product p7 = buildPerfume(
                    "Bleu Électrique",
                    "bleu-electrique",
                    "La interpretación más electrizante y sensual de La Nuit de L'Homme. Cardamomo vibrante y jengibre helado sobre una base cremosa de lavanda y cedro nocturno.",
                    "490.00", "580.00", "38.00", 19,
                    "https://images.unsplash.com/photo-1585386959984-a4155224a1ad?w=800&q=80",
                    catHombre, "Yves Saint Laurent", "Oriental / Especiado", "EDT",
                    "Citas Románticas, Club & Fiesta", "100 ML EDT INTENSE", "Frasco 100ml+",
                    "Cardamomo intenso, Jengibre helado, Lavanda, Cedro",
                    "OFERTA ESPECIAL", "red", "4.8", 115, "Hombre", false, true
            );
            p7.addImage(new ProductImage(p7, "https://images.unsplash.com/photo-1585386959984-a4155224a1ad?w=800&q=80", true, 1));
            addPerfumeVariants(p7, "YSL-BLEU", "Frasco 100ml", "EDT", "490.00", "68.00", "38.00", presentaciones, concentraciones);
            productRepo.save(p7);

            // 8. Initio Privés - Side Effect
            Product p8 = buildPerfume(
                    "Side Effect",
                    "side-effect",
                    "Magnetismo sin concesiones. Un acorde embriagador de ron añejo, tabaco oscuro, canela cálida y vainilla seductora con un fondo ligeramente ahumado y resinoso.",
                    "1280.00", null, "75.00", 7,
                    "https://images.unsplash.com/photo-1547887538-e3a2f32cb1cc?w=800&q=80",
                    catOcasiones, "Initio Privés", "Cuero / Ahumado", "EDP",
                    "Noche & Gala, Club & Fiesta, Citas Románticas", "90 ML EDP", "Frasco 50-90ml",
                    "Ron añejo, Tabaco oscuro, Canela, Vainilla y Azafrán",
                    "EXCLUSIVO", "dark", "4.9", 52, "Unisex", true, true
            );
            p8.addImage(new ProductImage(p8, "https://images.unsplash.com/photo-1547887538-e3a2f32cb1cc?w=800&q=80", true, 1));
            addPerfumeVariants(p8, "INIT-SIDE", "Frasco 90ml", "EDP", "1280.00", "138.00", "75.00", presentaciones, concentraciones);
            productRepo.save(p8);

            // 9. Maison Francis Kurkdjian - Baccarat Rouge 540 Extrait
            Product p9 = buildPerfume(
                    "Baccarat Rouge 540 Extrait",
                    "baccarat-rouge-540-extrait",
                    "Alquimia luminosa de alta perfumería parisina. Almendra amarga de Marruecos, azafrán precioso, jazmín Grandiflorum de Egipto y un halo cristalino de ámbar gris.",
                    "1650.00", null, "95.00", 6,
                    "https://images.unsplash.com/photo-1541643600914-78b084683601?w=800&q=80",
                    catMujer, "Maison Francis Kurkdjian", "Oriental / Especiado", "Extrait",
                    "Noche & Gala, Citas Románticas", "70 ML EXTRAIT", "Frasco 50-90ml",
                    "Azafrán, Almendra amarga, Jazmín egipcio, Ámbar gris",
                    "ÍCONO MUNDIAL", "dark", "5.0", 268, "Mujer", true, true
            );
            p9.addImage(new ProductImage(p9, "https://images.unsplash.com/photo-1541643600914-78b084683601?w=800&q=80", true, 1));
            addPerfumeVariants(p9, "MFK-BR540", "Frasco 70ml", "Extrait", "1650.00", "175.00", "95.00", presentaciones, concentraciones);
            p9.addReview(new ProductReview(p9, "Lucía P.", 5, "Inconfundible y sublime. Llegó con empaque de boutique y muestra de cortesía."));
            productRepo.save(p9);

            // 10. Parfums de Marly - Delina Exclusif
            Product p10 = buildPerfume(
                    "Delina Exclusif",
                    "delina-exclusif",
                    "Un bouquet majestuoso de rosa turca envuelto en lichi jugoso, pera aterciopelada, un velo misterioso de incienso y una estela cremosa de vainilla y almizcle.",
                    "1180.00", "1320.00", "70.00", 10,
                    "https://images.unsplash.com/photo-1595425970377-c9703cf48b6d?w=800&q=80",
                    catMujer, "Parfums de Marly", "Floral", "EDP",
                    "Citas Románticas, Noche & Gala", "75 ML EDP", "Frasco 50-90ml",
                    "Rosa turca, Lichi, Pera, Incienso suave, Vainilla",
                    "-10% OFF", "red", "4.9", 146, "Mujer", true, true
            );
            p10.addImage(new ProductImage(p10, "https://images.unsplash.com/photo-1595425970377-c9703cf48b6d?w=800&q=80", true, 1));
            addPerfumeVariants(p10, "PDM-DEL", "Frasco 75ml", "EDP", "1180.00", "125.00", "70.00", presentaciones, concentraciones);
            productRepo.save(p10);

            // 11. Byredo - Bal d'Afrique
            Product p11 = buildPerfume(
                    "Bal d'Afrique Eau de Parfum",
                    "bal-dafrique-eau-de-parfum",
                    "Luminoso, sofisticado y vanguardista. Celebración cítrica y amaderada con bergamota brillante, limón de Amalfi, violeta africana, vetiver limpio y cedro de Marruecos.",
                    "920.00", null, "58.00", 15,
                    "https://images.unsplash.com/photo-1590736704728-f4730bb30770?w=800&q=80",
                    catMujer, "Byredo", "Cítrico Fresco", "EDP",
                    "Oficina / Firma, Verano & Fresco", "100 ML EDP", "Frasco 100ml+",
                    "Bergamota, Limón de Amalfi, Vetiver, Violeta, Cedro",
                    "PERFUMERÍA NICHO", "soft", "4.8", 74, "Mujer", true, true
            );
            p11.addImage(new ProductImage(p11, "https://images.unsplash.com/photo-1590736704728-f4730bb30770?w=800&q=80", true, 1));
            addPerfumeVariants(p11, "BYR-BAL", "Frasco 100ml", "EDP", "920.00", "105.00", "58.00", presentaciones, concentraciones);
            productRepo.save(p11);

            // 12. Yves Saint Laurent - Libre Intense
            Product p12 = buildPerfume(
                    "Libre Intense Eau de Parfum",
                    "libre-intense-eau-de-parfum",
                    "Tensión ardiente entre la frescura de la lavanda francesa y la sensualidad de la flor de azahar de Marruecos, coronada con orquídea y vainilla cálida.",
                    "540.00", "630.00", "42.00", 24,
                    "https://images.unsplash.com/photo-1619994403073-2cec844b8e63?w=800&q=80",
                    catMujer, "Yves Saint Laurent", "Floral", "EDP",
                    "Citas Románticas, Oficina / Firma, Noche & Gala", "90 ML EDP", "Frasco 50-90ml",
                    "Lavanda francesa, Flor de azahar, Orquídea, Vainilla",
                    "-15% OFF", "red", "4.8", 183, "Mujer", false, true
            );
            p12.addImage(new ProductImage(p12, "https://images.unsplash.com/photo-1619994403073-2cec844b8e63?w=800&q=80", true, 1));
            addPerfumeVariants(p12, "YSL-LIB", "Frasco 90ml", "EDP", "540.00", "76.00", "42.00", presentaciones, concentraciones);
            productRepo.save(p12);

            // 13. Discovery Set Alta Perfumería (5 x 5ml)
            Product p13 = buildPerfume(
                    "Discovery Set Nicho — Gala & Noche (5 × 5ml)",
                    "discovery-set-nicho-gala-noche",
                    "Estuche de degustación oficial con 5 atomizadores de vidrio de 5ml envasados al momento: Layton, Tobacco Vanille, Naxos, Ani Extrait y Side Effect.",
                    "240.00", "295.00", "55.00", 30,
                    "https://images.unsplash.com/photo-1588405748880-12d1d2a59f75?w=800&q=80",
                    catDecants, "Tom Ford", "Amaderado", "Extrait",
                    "Noche & Gala, Citas Románticas, Oficina / Firma", "SET 5 × 5 ML", "Discovery Set",
                    "5 Decants certificados 5ml · Estuche rígido con guía olfativa",
                    "SET CURADO", "gold", "5.0", 92, "Unisex", true, true
            );
            p13.addImage(new ProductImage(p13, "https://images.unsplash.com/photo-1588405748880-12d1d2a59f75?w=800&q=80", true, 1));
            ProductVariant p13v1 = new ProductVariant(p13, "SET-GALA-5X5", new BigDecimal("240.00"), 30);
            p13v1.setAttributeValues(List.of(presentaciones.get("Set 5 × 5ml"), concentraciones.get("Extrait")));
            p13.addVariant(p13v1);
            productRepo.save(p13);

            // 14. Decant Oficial Tom Ford Oud Wood 10ml
            Product p14 = buildPerfume(
                    "Oud Wood — Decant Oficial 10ml",
                    "oud-wood-decant-oficial-10ml",
                    "Atomizador de viaje de 10ml extraído de frasco original verificado. Madera de oud exótica suavizada con palo de rosa, cardamomo, sándalo y vetiver.",
                    "165.00", null, "85.00", 35,
                    "https://images.unsplash.com/photo-1608528577891-eb055944f2e7?w=800&q=80",
                    catDecants, "Tom Ford", "Amaderado", "EDP",
                    "Oficina / Firma, Noche & Gala", "10 ML DECANT", "Decant 5ml / 10ml",
                    "Madera de oud rara, Palo de rosa, Cardamomo, Sándalo",
                    "DECANT OFICIAL", "soft", "4.9", 64, "Unisex", true, true
            );
            p14.addImage(new ProductImage(p14, "https://images.unsplash.com/photo-1608528577891-eb055944f2e7?w=800&q=80", true, 1));
            ProductVariant p14v1 = new ProductVariant(p14, "TF-OUD-10ML", new BigDecimal("165.00"), 20);
            p14v1.setAttributeValues(List.of(presentaciones.get("Decant 10ml"), concentraciones.get("EDP")));
            p14.addVariant(p14v1);
            ProductVariant p14v2 = new ProductVariant(p14, "TF-OUD-5ML", new BigDecimal("85.00"), 15);
            p14v2.setAttributeValues(List.of(presentaciones.get("Muestra 5ml"), concentraciones.get("EDP")));
            p14.addVariant(p14v2);
            productRepo.save(p14);
        };
    }

    private Product buildPerfume(
            String name, String slug, String description,
            String price, String originalPrice, String samplePrice, int stock,
            String imageUrl, Category category, String brand,
            String olfactoryFamily, String concentration, String occasion,
            String volumeLabel, String formatType, String notes,
            String badge, String badgeTone, String rating, int reviewsCount,
            String gender, boolean isNiche, boolean fastDeliveryLima
    ) {
        Product p = new Product(name, slug, description, new BigDecimal(price), stock, imageUrl, category);
        p.setBrand(brand);
        p.setOlfactoryFamily(olfactoryFamily);
        p.setConcentration(concentration);
        p.setOccasion(occasion);
        p.setVolumeLabel(volumeLabel);
        p.setFormatType(formatType);
        p.setNotes(notes);
        p.setBadge(badge);
        p.setBadgeTone(badgeTone);
        if (originalPrice != null) {
            p.setOriginalPrice(new BigDecimal(originalPrice));
        }
        p.setSamplePrice(new BigDecimal(samplePrice));
        p.setRating(new BigDecimal(rating));
        p.setReviewsCount(reviewsCount);
        p.setGender(gender);
        p.setIsNiche(isNiche);
        p.setFastDeliveryLima(fastDeliveryLima);
        return p;
    }

    private void addPerfumeVariants(
            Product product, String skuPrefix, String bottleLabel, String concLabel,
            String bottlePrice, String decant10Price, String sample5Price,
            Map<String, AttributeValue> presentaciones,
            Map<String, AttributeValue> concentraciones
    ) {
        AttributeValue concVal = concentraciones.get(concLabel);

        ProductVariant vBottle = new ProductVariant(product, skuPrefix + "-FULL", new BigDecimal(bottlePrice), product.getStock());
        vBottle.setAttributeValues(List.of(presentaciones.get(bottleLabel), concVal));
        product.addVariant(vBottle);

        ProductVariant v10 = new ProductVariant(product, skuPrefix + "-10ML", new BigDecimal(decant10Price), 25);
        v10.setAttributeValues(List.of(presentaciones.get("Decant 10ml"), concVal));
        product.addVariant(v10);

        ProductVariant v5 = new ProductVariant(product, skuPrefix + "-5ML", new BigDecimal(sample5Price), 40);
        v5.setAttributeValues(List.of(presentaciones.get("Muestra 5ml"), concVal));
        product.addVariant(v5);
    }
}
