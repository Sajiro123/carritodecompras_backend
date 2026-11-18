CREATE DATABASE IF NOT EXISTS shop CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE shop;

CREATE TABLE IF NOT EXISTS products (
  id          BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  name        VARCHAR(120)  NOT NULL,
  description VARCHAR(500)  NULL,
  price       DECIMAL(10,2) NOT NULL,
  stock       INT UNSIGNED  NOT NULL DEFAULT 0,
  image_url   VARCHAR(255)  NULL,
  active      TINYINT(1)    NOT NULL DEFAULT 1,
  created_at  TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS orders (
  id             BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  customer_name  VARCHAR(120)  NOT NULL,
  customer_email VARCHAR(150)  NOT NULL,
  customer_phone VARCHAR(30)   NULL,
  address        VARCHAR(255)  NOT NULL,
  total          DECIMAL(10,2) NOT NULL,
  status         VARCHAR(20)   NOT NULL DEFAULT 'PENDING',
  created_at     TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS order_items (
  id         BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  order_id   BIGINT UNSIGNED NOT NULL,
  product_id BIGINT UNSIGNED NOT NULL,
  quantity   INT UNSIGNED    NOT NULL,
  unit_price DECIMAL(10,2)   NOT NULL,
  CONSTRAINT fk_items_order   FOREIGN KEY (order_id)   REFERENCES orders(id)   ON DELETE CASCADE,
  CONSTRAINT fk_items_product FOREIGN KEY (product_id) REFERENCES products(id)
);

INSERT INTO products (name, description, price, stock, image_url) VALUES
('Polo básico',       'Algodón 100%, color negro',   39.90, 50, 'https://picsum.photos/seed/polo/400/300'),
('Zapatillas urbanas','Cómodas para el día a día',  149.00, 20, 'https://picsum.photos/seed/shoes/400/300'),
('Mochila 20L',       'Resistente al agua',          89.50, 15, 'https://picsum.photos/seed/bag/400/300'),
('Gorra deportiva',   'Ajustable',                   29.00, 40, 'https://picsum.photos/seed/cap/400/300'),
('Reloj digital',     'Sumergible hasta 50m',       119.90, 10, 'https://picsum.photos/seed/watch/400/300');
