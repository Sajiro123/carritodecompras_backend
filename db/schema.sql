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

-- 2. UBIGEO DEL PERU (Distrito, Provincia, Departamento, Codigo 6 digitos)
CREATE TABLE IF NOT EXISTS ubigeo (
  codigo       VARCHAR(6)   PRIMARY KEY,
  departamento VARCHAR(60)  NOT NULL,
  provincia    VARCHAR(60)  NOT NULL,
  distrito     VARCHAR(60)  NOT NULL,
  zona         VARCHAR(30)  NOT NULL DEFAULT 'PROVINCIAS',
  costo_envio  DECIMAL(10,2) NOT NULL DEFAULT 18.00
);

-- 3. ZONAS DE ENVIO
CREATE TABLE IF NOT EXISTS zona_envio (
  id             BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  nombre         VARCHAR(60)   NOT NULL,
  costo_base     DECIMAL(10,2) NOT NULL,
  umbral_gratis  DECIMAL(10,2) NOT NULL DEFAULT 300.00,
  dias_entrega   VARCHAR(60)   NOT NULL DEFAULT '24 a 48 horas'
);

-- 4. TIENDAS FISICAS
CREATE TABLE IF NOT EXISTS tienda (
  id          BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  nombre      VARCHAR(120)  NOT NULL,
  direccion   VARCHAR(255)  NOT NULL,
  distrito    VARCHAR(80)   NOT NULL,
  departamento VARCHAR(60)  NOT NULL DEFAULT 'Lima',
  telefono    VARCHAR(30)   NULL,
  horario     VARCHAR(120)  NOT NULL,
  activo      TINYINT(1)    NOT NULL DEFAULT 1
);

-- 5. AGENCIAS DE ENVIO (Shalom)
CREATE TABLE IF NOT EXISTS agencia_envio (
  id           BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  empresa      VARCHAR(60)   NOT NULL DEFAULT 'Shalom',
  nombre       VARCHAR(120)  NOT NULL,
  direccion    VARCHAR(255)  NOT NULL,
  departamento VARCHAR(60)   NOT NULL,
  provincia    VARCHAR(60)   NOT NULL,
  distrito     VARCHAR(60)   NOT NULL,
  telefono     VARCHAR(30)   NULL,
  activo       TINYINT(1)    NOT NULL DEFAULT 1
);

-- 6. PEDIDOS / ORDERS
CREATE TABLE IF NOT EXISTS orders (
  id                  BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  codigo_seguimiento  VARCHAR(30)   NOT NULL UNIQUE,
  customer_name       VARCHAR(120)  NOT NULL,
  customer_email      VARCHAR(150)  NOT NULL,
  customer_phone      VARCHAR(30)   NOT NULL,
  tipo_entrega        VARCHAR(30)   NOT NULL DEFAULT 'DOMICILIO',
  ubigeo_codigo       VARCHAR(6)    NULL,
  direccion_envio     VARCHAR(255)  NULL,
  referencia_envio    VARCHAR(255)  NULL,
  tienda_id           BIGINT UNSIGNED NULL,
  agencia_id          BIGINT UNSIGNED NULL,
  receptor_nombre     VARCHAR(120)  NULL,
  receptor_dni        VARCHAR(20)   NULL,
  tipo_comprobante    VARCHAR(20)   NOT NULL DEFAULT 'BOLETA',
  tipo_documento      VARCHAR(20)   NULL,
  numero_documento    VARCHAR(20)   NOT NULL,
  razon_social        VARCHAR(150)  NULL,
  direccion_fiscal    VARCHAR(255)  NULL,
  metodo_pago         VARCHAR(30)   NOT NULL DEFAULT 'BCP',
  subtotal            DECIMAL(10,2) NOT NULL,
  costo_envio         DECIMAL(10,2) NOT NULL DEFAULT 0.00,
  total               DECIMAL(10,2) NOT NULL,
  status              VARCHAR(20)   NOT NULL DEFAULT 'PENDING',
  created_at          TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 7. DETALLE DEL PEDIDO
CREATE TABLE IF NOT EXISTS order_items (
  id         BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  order_id   BIGINT UNSIGNED NOT NULL,
  product_id BIGINT UNSIGNED NOT NULL,
  quantity   INT UNSIGNED    NOT NULL,
  unit_price DECIMAL(10,2)   NOT NULL,
  subtotal   DECIMAL(10,2)   NOT NULL,
  CONSTRAINT fk_items_order   FOREIGN KEY (order_id)   REFERENCES orders(id)   ON DELETE CASCADE,
  CONSTRAINT fk_items_product FOREIGN KEY (product_id) REFERENCES products(id)
);

-- 8. POLITICAS
CREATE TABLE IF NOT EXISTS politica (
  id             BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  slug           VARCHAR(80)   NOT NULL UNIQUE,
  titulo         VARCHAR(150)  NOT NULL,
  contenido      LONGTEXT      NOT NULL,
  actualizado_el TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 9. NEWSLETTER
CREATE TABLE IF NOT EXISTS newsletter (
  id         BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  email      VARCHAR(150)  NOT NULL UNIQUE,
  created_at TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- INSERTS DE PRUEBA
INSERT INTO products (name, description, price, stock, image_url)
SELECT 'Polo básico', 'Algodón 100%, color negro', 39.90, 50, 'https://picsum.photos/seed/polo/400/300'
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name = 'Polo básico');

INSERT INTO products (name, description, price, stock, image_url)
SELECT 'Zapatillas urbanas', 'Cómodas para el día a día', 149.00, 20, 'https://picsum.photos/seed/shoes/400/300'
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name = 'Zapatillas urbanas');

INSERT INTO products (name, description, price, stock, image_url)
SELECT 'Mochila 20L', 'Resistente al agua', 89.50, 15, 'https://picsum.photos/seed/bag/400/300'
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name = 'Mochila 20L');

INSERT INTO products (name, description, price, stock, image_url)
SELECT 'Gorra deportiva', 'Ajustable', 29.00, 40, 'https://picsum.photos/seed/cap/400/300'
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name = 'Gorra deportiva');

INSERT INTO products (name, description, price, stock, image_url)
SELECT 'Reloj digital', 'Sumergible hasta 50m', 119.90, 10, 'https://picsum.photos/seed/watch/400/300'
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name = 'Reloj digital');

-- UBIGEOS REPRESENTATIVOS (Lima Metropolitana, Callao, Provincias)
INSERT IGNORE INTO ubigeo (codigo, departamento, provincia, distrito, zona, costo_envio) VALUES
('150101', 'Lima', 'Lima', 'Lima Cercado', 'LIMA_METROPOLITANA', 10.00),
('150114', 'Lima', 'Lima', 'La Molina', 'LIMA_METROPOLITANA', 10.00),
('150115', 'Lima', 'Lima', 'La Victoria', 'LIMA_METROPOLITANA', 10.00),
('150116', 'Lima', 'Lima', 'Lince', 'LIMA_METROPOLITANA', 10.00),
('150117', 'Lima', 'Lima', 'Los Olivos', 'LIMA_METROPOLITANA', 10.00),
('150120', 'Lima', 'Lima', 'Magdalena del Mar', 'LIMA_METROPOLITANA', 10.00),
('150122', 'Lima', 'Lima', 'Miraflores', 'LIMA_METROPOLITANA', 10.00),
('150128', 'Lima', 'Lima', 'Pueblo Libre', 'LIMA_METROPOLITANA', 10.00),
('150130', 'Lima', 'Lima', 'San Borja', 'LIMA_METROPOLITANA', 10.00),
('150131', 'Lima', 'Lima', 'San Isidro', 'LIMA_METROPOLITANA', 10.00),
('150132', 'Lima', 'Lima', 'San Juan de Lurigancho', 'LIMA_METROPOLITANA', 10.00),
('150133', 'Lima', 'Lima', 'San Juan de Miraflores', 'LIMA_METROPOLITANA', 10.00),
('150135', 'Lima', 'Lima', 'San Martín de Porres', 'LIMA_METROPOLITANA', 10.00),
('150136', 'Lima', 'Lima', 'San Miguel', 'LIMA_METROPOLITANA', 10.00),
('150140', 'Lima', 'Lima', 'Santiago de Surco', 'LIMA_METROPOLITANA', 10.00),
('150141', 'Lima', 'Lima', 'Surquillo', 'LIMA_METROPOLITANA', 10.00),
('150103', 'Lima', 'Lima', 'Ate', 'LIMA_METROPOLITANA', 10.00),
('150104', 'Lima', 'Lima', 'Barranco', 'LIMA_METROPOLITANA', 10.00),
('150108', 'Lima', 'Lima', 'Chorrillos', 'LIMA_METROPOLITANA', 10.00),
('070101', 'Callao', 'Callao', 'Callao Cercado', 'CALLAO', 12.00),
('070102', 'Callao', 'Callao', 'Bellavista', 'CALLAO', 12.00),
('070104', 'Callao', 'Callao', 'La Perla', 'CALLAO', 12.00),
('070105', 'Callao', 'Callao', 'La Punta', 'CALLAO', 12.00),
('070106', 'Callao', 'Callao', 'Ventanilla', 'CALLAO', 12.00),
('040101', 'Arequipa', 'Arequipa', 'Arequipa Cercado', 'PROVINCIAS', 18.00),
('040103', 'Arequipa', 'Arequipa', 'Cayma', 'PROVINCIAS', 18.00),
('040126', 'Arequipa', 'Arequipa', 'Yanahuara', 'PROVINCIAS', 18.00),
('080101', 'Cusco', 'Cusco', 'Cusco Cercado', 'PROVINCIAS', 18.00),
('080108', 'Cusco', 'Cusco', 'Wanchaq', 'PROVINCIAS', 18.00),
('130101', 'La Libertad', 'Trujillo', 'Trujillo Cercado', 'PROVINCIAS', 18.00),
('130111', 'La Libertad', 'Trujillo', 'Víctor Larco Herrera', 'PROVINCIAS', 18.00),
('200101', 'Piura', 'Piura', 'Piura Cercado', 'PROVINCIAS', 18.00),
('200104', 'Piura', 'Piura', 'Castilla', 'PROVINCIAS', 18.00),
('140101', 'Lambayeque', 'Chiclayo', 'Chiclayo Cercado', 'PROVINCIAS', 18.00),
('140114', 'Lambayeque', 'Chiclayo', 'La Victoria', 'PROVINCIAS', 18.00),
('120101', 'Junín', 'Huancayo', 'Huancayo Cercado', 'PROVINCIAS', 18.00),
('120114', 'Junín', 'Huancayo', 'El Tambo', 'PROVINCIAS', 18.00);

-- ZONAS DE ENVIO BASE
INSERT IGNORE INTO zona_envio (id, nombre, costo_base, umbral_gratis, dias_entrega) VALUES
(1, 'Lima Metropolitana', 10.00, 300.00, '24 a 48 horas'),
(2, 'Callao', 12.00, 300.00, '24 a 48 horas'),
(3, 'Provincias / Nacional', 18.00, 300.00, '48 a 72 horas');

-- TIENDAS FISICAS
INSERT IGNORE INTO tienda (id, nombre, direccion, distrito, departamento, telefono, horario, activo) VALUES
(1, 'Tienda San Isidro (Principal)', 'Av. Javier Prado Este 2465, Urb. Corpac', 'San Isidro', 'Lima', '+51 987 654 321', 'Lun a Sáb: 9:00 AM - 8:00 PM', 1),
(2, 'Tienda Miraflores', 'Av. José Larco 812', 'Miraflores', 'Lima', '+51 987 654 322', 'Lun a Dom: 10:00 AM - 9:00 PM', 1);

-- AGENCIAS SHALOM
INSERT IGNORE INTO agencia_envio (id, empresa, nombre, direccion, departamento, provincia, distrito, telefono, activo) VALUES
(1, 'Shalom', 'Agencia Central La Victoria', 'Av. Nicolás Arriola 1450', 'Lima', 'Lima', 'La Victoria', '(01) 500-7878', 1),
(2, 'Shalom', 'Agencia Miraflores Benavides', 'Av. Alfredo Benavides 2150', 'Lima', 'Lima', 'Miraflores', '(01) 500-7878', 1),
(3, 'Shalom', 'Agencia Los Olivos Angélica Gamarra', 'Av. Angélica Gamarra 450', 'Lima', 'Lima', 'Los Olivos', '(01) 500-7878', 1),
(4, 'Shalom', 'Agencia Arequipa Pizarro', 'Calle Francisco Pizarro 310', 'Arequipa', 'Arequipa', 'Arequipa', '(054) 223-456', 1),
(5, 'Shalom', 'Agencia Trujillo Centro', 'Av. España 1820', 'La Libertad', 'Trujillo', 'Trujillo', '(044) 289-012', 1),
(6, 'Shalom', 'Agencia Cusco Santiago', 'Av. Antonio Lorena 410', 'Cusco', 'Cusco', 'Santiago', '(084) 234-567', 1),
(7, 'Shalom', 'Agencia Chiclayo Salaverry', 'Av. Salaverry 650', 'Lambayeque', 'Chiclayo', 'Chiclayo', '(074) 205-678', 1),
(8, 'Shalom', 'Agencia Piura Sánchez Cerro', 'Av. Sánchez Cerro 1280', 'Piura', 'Piura', 'Piura', '(073) 304-512', 1),
(9, 'Shalom', 'Agencia Huancayo Ferrocarril', 'Av. Ferrocarril 1120', 'Junín', 'Huancayo', 'Huancayo', '(064) 211-900', 1);

-- POLITICAS DE LA TIENDA
INSERT INTO politica (slug, titulo, contenido) VALUES
('terminos-y-condiciones', 'Términos y Condiciones de Uso', '<h3>1. Introducción</h3><p>Bienvenido a nuestra tienda en línea. Al acceder y realizar compras en este sitio web, aceptas nuestros términos y condiciones generales de contratación y entrega.</p><h3>2. Precios y Stock</h3><p>Todos los precios se expresan en Nuevos Soles (S/) e incluyen el Impuesto General a las Ventas (IGV). Las compras están sujetas a disponibilidad de stock verificada al momento de procesar la orden.</p><h3>3. Envíos y Entregas</h3><p>Los envíos en Lima Metropolitana y Callao se entregan entre 24 y 48 horas hábiles. Los envíos a provincias mediante Shalom o envío a domicilio demoran de 48 a 72 horas hábiles. El beneficio de <strong>Envío Gratis</strong> aplica para pedidos cuyo subtotal sea igual o superior a S/ 300.00.</p><h3>4. Medios de Pago</h3><p>Aceptamos transferencias BCP, Yape, Plin y tarjetas de crédito/débito a través de pasarelas seguras (Culqi y Flow).</p>'),
('politicas-de-privacidad', 'Políticas de Privacidad y Protección de Datos', '<h3>1. Tratamiento de Datos Personales</h3><p>De conformidad con la Ley N° 29733 (Ley de Protección de Datos Personales de Perú), tus datos personales serán utilizados exclusivamente para la gestión, despacho y facturación de tus pedidos.</p><h3>2. Seguridad de la Información</h3><p>Tus datos no serán comercializados ni transferidos a terceros no vinculados con la operación logística y de pagos de la compra.</p><h3>3. Derechos ARCO</h3><p>Puedes ejercer tus derechos de Acceso, Rectificación, Cancelación y Oposición escribiendo a nuestro correo oficial de atención al cliente.</p>'),
('cambios-y-devoluciones', 'Políticas de Cambios y Devoluciones', '<h3>1. Plazo de Solicitud</h3><p>Dispones de hasta siete (7) días calendario posteriores a la recepción del pedido para solicitar un cambio de talla o devolución.</p><h3>2. Condiciones del Producto</h3><p>El producto debe encontrarse nuevo, sin uso, en su empaque original y con todas sus etiquetas y accesorios completos.</p><h3>3. Proceso</h3><p>Comunícate a nuestra línea de WhatsApp o presenta tu producto en nuestra sede de San Isidro con tu boleta o factura de compra.</p>'),
('libro-de-reclamaciones', 'Libro de Reclamaciones Virtual', '<h3>Libro de Reclamaciones Virtual</h3><p>Conforme a lo establecido en el Código de Protección y Defensa del Consumidor (Ley N° 29571), ponemos a tu disposición nuestro Libro de Reclamaciones Virtual.</p><p>Razón Social: Mi Tienda Online S.A.C.<br>RUC: 20608912345<br>Dirección: Av. Javier Prado Este 2465, San Isidro, Lima, Perú.</p><p>Para registrar un reclamo o queja formal, puedes comunicarte a <strong>atencion@mitienda.pe</strong> indicando tus datos, código de pedido y el detalle del reclamo. Se te responderá en un plazo máximo de quince (15) días hábiles.</p>')
ON DUPLICATE KEY UPDATE titulo = VALUES(titulo), contenido = VALUES(contenido);
