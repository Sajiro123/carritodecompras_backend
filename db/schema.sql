CREATE DATABASE IF NOT EXISTS shop CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE shop;

SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS review_foto;
DROP TABLE IF EXISTS producto_review;
DROP TABLE IF EXISTS variante_atributo_valor;
DROP TABLE IF EXISTS producto_variante;
DROP TABLE IF EXISTS atributo_valor;
DROP TABLE IF EXISTS atributo;
DROP TABLE IF EXISTS producto_imagen;
DROP TABLE IF EXISTS order_items;
DROP TABLE IF EXISTS orders;
DROP TABLE IF EXISTS products;
DROP TABLE IF EXISTS categories;
SET FOREIGN_KEY_CHECKS = 1;

-- Categorías
CREATE TABLE categories (
  id          BIGINT AUTO_INCREMENT PRIMARY KEY,
  name        VARCHAR(100)  NOT NULL,
  slug        VARCHAR(120)  NOT NULL UNIQUE,
  description VARCHAR(500)  NULL,
  image_url   VARCHAR(255)  NULL,
  active      TINYINT(1)    NOT NULL DEFAULT 1,
  created_at  TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Productos
CREATE TABLE products (
  id          BIGINT AUTO_INCREMENT PRIMARY KEY,
  name        VARCHAR(150)  NOT NULL,
  slug        VARCHAR(150)  NOT NULL UNIQUE,
  description VARCHAR(1000) NULL,
  price       DECIMAL(10,2) NOT NULL,
  stock       INT           NOT NULL DEFAULT 0,
  image_url   VARCHAR(255)  NULL,
  active      TINYINT(1)    NOT NULL DEFAULT 1,
  category_id BIGINT        NULL,
  created_at  TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_products_category FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Galería de imágenes (producto_imagen)
CREATE TABLE producto_imagen (
  id            BIGINT AUTO_INCREMENT PRIMARY KEY,
  producto_id   BIGINT        NOT NULL,
  image_url     VARCHAR(500)  NOT NULL,
  is_cover      TINYINT(1)    NOT NULL DEFAULT 0,
  display_order INT           NOT NULL DEFAULT 0,
  CONSTRAINT fk_imagen_producto FOREIGN KEY (producto_id) REFERENCES products(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Atributos
CREATE TABLE atributo (
  id   BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(50) NOT NULL UNIQUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Valores de atributos
CREATE TABLE atributo_valor (
  id          BIGINT AUTO_INCREMENT PRIMARY KEY,
  atributo_id BIGINT      NOT NULL,
  valor       VARCHAR(50) NOT NULL,
  CONSTRAINT fk_valor_atributo FOREIGN KEY (atributo_id) REFERENCES atributo(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Variantes de producto con stock (producto_variante)
CREATE TABLE producto_variante (
  id          BIGINT AUTO_INCREMENT PRIMARY KEY,
  producto_id BIGINT        NOT NULL,
  sku         VARCHAR(60)   NOT NULL UNIQUE,
  price       DECIMAL(10,2) NULL,
  stock       INT           NOT NULL DEFAULT 0,
  CONSTRAINT fk_variante_producto FOREIGN KEY (producto_id) REFERENCES products(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Relación variante - atributo_valor
CREATE TABLE variante_atributo_valor (
  variante_id       BIGINT NOT NULL,
  atributo_valor_id BIGINT NOT NULL,
  PRIMARY KEY (variante_id, atributo_valor_id),
  CONSTRAINT fk_vav_variante FOREIGN KEY (variante_id) REFERENCES producto_variante(id) ON DELETE CASCADE,
  CONSTRAINT fk_vav_valor    FOREIGN KEY (atributo_valor_id) REFERENCES atributo_valor(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Reseñas de producto (producto_review)
CREATE TABLE producto_review (
  id          BIGINT AUTO_INCREMENT PRIMARY KEY,
  producto_id BIGINT       NOT NULL,
  user_name   VARCHAR(120) NOT NULL,
  rating      INT          NOT NULL,
  comment     TEXT         NOT NULL,
  created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_review_producto FOREIGN KEY (producto_id) REFERENCES products(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Fotos de reseñas (review_foto)
CREATE TABLE review_foto (
  id         BIGINT AUTO_INCREMENT PRIMARY KEY,
  review_id  BIGINT       NOT NULL,
  image_url  VARCHAR(500) NOT NULL,
  CONSTRAINT fk_foto_review FOREIGN KEY (review_id) REFERENCES producto_review(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Pedidos (orders)
CREATE TABLE orders (
  id             BIGINT AUTO_INCREMENT PRIMARY KEY,
  customer_name  VARCHAR(120)  NOT NULL,
  customer_email VARCHAR(150)  NOT NULL,
  customer_phone VARCHAR(30)   NULL,
  address        VARCHAR(255)  NOT NULL,
  total          DECIMAL(10,2) NOT NULL,
  status         VARCHAR(20)   NOT NULL DEFAULT 'PENDING',
  created_at     TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE order_items (
  id         BIGINT AUTO_INCREMENT PRIMARY KEY,
  order_id   BIGINT        NOT NULL,
  product_id BIGINT        NOT NULL,
  quantity   INT           NOT NULL,
  unit_price DECIMAL(10,2) NOT NULL,
  CONSTRAINT fk_items_order   FOREIGN KEY (order_id)   REFERENCES orders(id)   ON DELETE CASCADE,
  CONSTRAINT fk_items_product FOREIGN KEY (product_id) REFERENCES products(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
