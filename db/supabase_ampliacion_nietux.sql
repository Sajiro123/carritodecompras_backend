-- =====================================================================
--  AMPLIACIÓN DEL CARRITO DE COMPRAS (basado en demo.nietux.shop)
--  Ejecutar DESPUÉS de supabase_schema.sql (sobre la base ya creada).
--  Donde ejecutar: Supabase Dashboard > SQL Editor > New query > Run
-- =====================================================================

-- =====================================================================
-- BLOQUE 1: Catálogos de documentos, comprobantes, envío, tiendas y pago
-- =====================================================================

create table public.tipo_documento (
  codigo           varchar(15) primary key,
  nombre           varchar(60) not null,
  longitud_minima  smallint    not null,
  longitud_maxima  smallint    not null,
  solo_numeros     boolean     not null default true,
  activo           boolean     not null default true
);
comment on table public.tipo_documento is 'DNI, Carnet de extranjería, Pasaporte, RUC y reglas de longitud.';

insert into public.tipo_documento (codigo, nombre, longitud_minima, longitud_maxima, solo_numeros) values
  ('DNI',       'DNI',                   8,  8, true),
  ('CE',        'Carnet de extranjería', 9, 12, true),
  ('PASAPORTE', 'Pasaporte',             6, 15, false),
  ('RUC',       'RUC',                  11, 11, true);

alter table public.persona drop constraint persona_tipo_documento_check;
alter table public.persona add constraint fk_persona_tipo_documento
  foreign key (tipo_documento) references public.tipo_documento (codigo);
alter table public.persona alter column apellidos drop not null;

create table public.tipo_comprobante (
  codigo       varchar(10) primary key,
  nombre       varchar(40) not null,
  descripcion  varchar(120)
);
insert into public.tipo_comprobante (codigo, nombre, descripcion) values
  ('BOLETA',  'Boleta',  'Para compras personales'),
  ('FACTURA', 'Factura', 'Para empresas, con RUC y razón social');

create table public.zona_envio (
  id                 smallint generated always as identity primary key,
  codigo             varchar(20)   not null unique,
  nombre             varchar(60)   not null,
  costo              numeric(10,2) not null check (costo >= 0),
  monto_envio_gratis numeric(10,2) check (monto_envio_gratis >= 0),
  tiempo_estimado    varchar(40)
);
comment on table public.zona_envio is 'Tarifa de envío por zona. monto_envio_gratis: umbral desde el que el envío es gratis.';

insert into public.zona_envio (codigo, nombre, costo, monto_envio_gratis, tiempo_estimado) values
  ('LIMA_METRO', 'Lima Metropolitana', 12.00, null,   '24 horas'),
  ('PROVINCIAS', 'Provincias',         25.00, 300.00, null);

create table public.ubigeo (
  codigo        char(6)     primary key,
  departamento  varchar(60) not null,
  provincia     varchar(60) not null,
  distrito      varchar(60) not null,
  zona_envio_id smallint    not null references public.zona_envio (id)
);
comment on table public.ubigeo is 'Distritos del Perú (código INEI de 6 dígitos). Cargar catálogo completo del INEI.';
create index idx_ubigeo_zona on public.ubigeo (zona_envio_id);

insert into public.ubigeo (codigo, departamento, provincia, distrito, zona_envio_id)
select v.codigo, v.dep, v.prov, v.dist, z.id
from (values
  ('150122', 'Lima',     'Lima',     'Miraflores', 'LIMA_METRO'),
  ('150131', 'Lima',     'Lima',     'San Isidro', 'LIMA_METRO'),
  ('040110', 'Arequipa', 'Arequipa', 'Miraflores', 'PROVINCIAS'),
  ('020101', 'Ancash',   'Huaraz',   'Huaraz',     'PROVINCIAS')
) as v(codigo, dep, prov, dist, zona)
join public.zona_envio z on z.codigo = v.zona;

create table public.metodo_entrega (
  id            smallint generated always as identity primary key,
  codigo        varchar(25) not null unique,
  nombre        varchar(60) not null,
  descripcion   varchar(150),
  tipo_destino  varchar(10) not null check (tipo_destino in ('DIRECCION','AGENCIA','TIENDA')),
  activo        boolean     not null default true
);
comment on table public.metodo_entrega is 'Domicilio, recojo en agencia Shalom o recojo en tienda.';

insert into public.metodo_entrega (codigo, nombre, descripcion, tipo_destino) values
  ('ENVIO_DOMICILIO', 'Envío a domicilio',        'Tarifa según distrito',        'DIRECCION'),
  ('RECOJO_AGENCIA',  'Recojo en agencia Shalom', 'Pagas el envío al recoger',    'AGENCIA'),
  ('RECOJO_TIENDA',   'Recojo en tienda',         'Gratis',                       'TIENDA');

create table public.tienda (
  id            bigint generated always as identity primary key,
  nombre        varchar(80)  not null,
  direccion     varchar(255) not null,
  ubigeo_codigo char(6)      not null references public.ubigeo (codigo),
  telefono      varchar(20),
  activo        boolean      not null default true
);
create index idx_tienda_ubigeo on public.tienda (ubigeo_codigo);

insert into public.tienda (nombre, direccion, ubigeo_codigo) values
  ('San Isidro', 'Calle Augusto Tamayo 180', '150131');

create table public.agencia_envio (
  id            bigint generated always as identity primary key,
  empresa       varchar(30)  not null default 'SHALOM',
  nombre        varchar(100) not null,
  ubigeo_codigo char(6)      not null references public.ubigeo (codigo),
  direccion     varchar(255) not null,
  activo        boolean      not null default true
);
comment on table public.agencia_envio is 'Agencias de recojo (ej. Shalom).';
create index idx_agencia_envio_ubigeo on public.agencia_envio (ubigeo_codigo);

insert into public.agencia_envio (nombre, ubigeo_codigo, direccion) values
  ('HUARAZ CO', '020101', 'AV. 27 DE NOVIEMBRE CDRA. 20 S/N - VILLON BAJO');

-- Actualizar tabla direccion para usar ubigeo
alter table public.direccion
  drop column departamento,
  drop column provincia,
  drop column distrito;
alter table public.direccion
  add column ubigeo_codigo char(6) not null references public.ubigeo (codigo);
create index idx_direccion_ubigeo on public.direccion (ubigeo_codigo);

-- Reemplazar métodos de pago genéricos por los de la tienda
alter table public.metodo_pago
  add column es_pasarela boolean not null default false,
  add column descripcion varchar(200);

delete from public.metodo_pago;
insert into public.metodo_pago (codigo, nombre, es_pasarela, descripcion) values
  ('BCP',   'BCP',   false, 'Te damos los datos para pagar al confirmar el pedido'),
  ('FLOW',  'Flow',  true,  'Te llevaremos a Flow para completar el pago'),
  ('CULQI', 'Culqi', true,  'Pagas aquí mismo, sin salir de la tienda');


-- =====================================================================
-- BLOQUE 2: Variantes de producto (Talla, Color, stock y precio por variante)
-- =====================================================================

alter table public.producto
  add column es_destacado boolean not null default false;

create table public.atributo (
  id      smallint generated always as identity primary key,
  nombre  varchar(50) not null unique,
  slug    varchar(60) not null unique
);
comment on table public.atributo is 'Ejes de variación de producto: Talla, Color, etc.';

insert into public.atributo (nombre, slug) values
  ('Color', 'color'),
  ('Talla', 'talla');

create table public.atributo_valor (
  id           bigint generated always as identity primary key,
  atributo_id  smallint    not null references public.atributo (id) on delete cascade,
  valor        varchar(60) not null,
  codigo_hex   char(7),
  orden        smallint    not null default 0,
  unique (atributo_id, valor)
);
comment on table public.atributo_valor is 'Valores posibles: Rojo, Negro, S, M, L, 40, 42...';
create index idx_atributo_valor_atributo on public.atributo_valor (atributo_id);

insert into public.atributo_valor (atributo_id, valor, codigo_hex, orden)
select a.id, v.valor, v.hex, v.orden
from (values
  ('color', 'Negro',        '#111827', 1),
  ('color', 'Blanco',       '#F9FAFB', 2),
  ('color', 'Verde bosque', '#14532D', 3),
  ('talla', 'S',            null,      1),
  ('talla', 'M',            null,      2),
  ('talla', 'L',            null,      3),
  ('talla', 'XL',           null,      4)
) as v(slug, valor, hex, orden)
join public.atributo a on a.slug = v.slug;

create table public.producto_variante (
  id              uuid primary key default gen_random_uuid(),
  producto_id     bigint        not null references public.producto (id) on delete cascade,
  sku             varchar(50)   unique,
  precio          numeric(10,2) not null check (precio >= 0),
  precio_oferta   numeric(10,2) check (precio_oferta >= 0 and precio_oferta <= precio),
  stock           integer       not null default 0 check (stock >= 0),
  imagen_url      varchar(255),
  activo          boolean       not null default true,
  creado_en       timestamptz   not null default now(),
  actualizado_en  timestamptz   not null default now()
);
comment on table public.producto_variante is 'Cada combinación comprable (ej. Casaca M / Negro). El UUID coincide con ?variant= de la URL.';
create index idx_producto_variante_producto on public.producto_variante (producto_id);

create trigger trg_producto_variante_actualizado
  before update on public.producto_variante
  for each row execute function public.fn_actualizar_fecha();

create table public.variante_valor (
  variante_id        uuid   not null references public.producto_variante (id) on delete cascade,
  atributo_valor_id  bigint not null references public.atributo_valor (id) on delete restrict,
  primary key (variante_id, atributo_valor_id)
);
create index idx_variante_valor_attr on public.variante_valor (atributo_valor_id);

-- Migrar los 5 productos existentes a una variante por defecto y mover el stock a variante
insert into public.producto_variante (producto_id, sku, precio, precio_oferta, stock, imagen_url, activo)
select id, sku || '-DEF', precio, precio_oferta, stock, imagen_url, activo
from public.producto;

alter table public.producto drop column stock;


-- =====================================================================
-- BLOQUE 3: Reseñas de productos (con hasta 4 fotos)
-- =====================================================================

create table public.resena_producto (
  id              bigint generated always as identity primary key,
  producto_id     bigint       not null references public.producto (id) on delete cascade,
  calificacion    smallint     not null check (calificacion between 1 and 5),
  nombre_autor    varchar(100) not null,
  titulo          varchar(150),
  comentario      text         not null,
  destacada_home  boolean      not null default false,
  aprobada        boolean      not null default true,
  creado_en       timestamptz  not null default now()
);
comment on table public.resena_producto is 'Opiniones de clientes sobre productos; destacada_home = true aparece en el Home.';
create index idx_resena_producto on public.resena_producto (producto_id);

create table public.resena_imagen (
  id         bigint generated always as identity primary key,
  resena_id  bigint       not null references public.resena_producto (id) on delete cascade,
  url        varchar(255) not null,
  orden      smallint     not null default 0
);
create index idx_resena_imagen_resena on public.resena_imagen (resena_id);


-- =====================================================================
-- BLOQUE 4: Carrito persistente, pedidos ampliados y facturación
-- =====================================================================

create table public.carrito (
  id              uuid primary key default gen_random_uuid(),
  persona_id      bigint      references public.persona (id) on delete set null,
  creado_en       timestamptz not null default now(),
  actualizado_en  timestamptz not null default now()
);
create index idx_carrito_persona on public.carrito (persona_id);

create trigger trg_carrito_actualizado
  before update on public.carrito
  for each row execute function public.fn_actualizar_fecha();

create table public.carrito_item (
  id           bigint generated always as identity primary key,
  carrito_id   uuid    not null references public.carrito (id) on delete cascade,
  variante_id  uuid    not null references public.producto_variante (id) on delete cascade,
  cantidad     integer not null check (cantidad > 0),
  unique (carrito_id, variante_id)
);
create index idx_carrito_item_variante on public.carrito_item (variante_id);

-- Ampliar cabecera del pedido para soportar /orders, entrega por distrito/tienda/agencia y código de país
alter table public.pedido
  alter column direccion_envio drop not null,
  add column codigo_seguimiento   varchar(20) not null unique,
  add column cliente_codigo_pais  varchar(8)  not null default '+51',
  add column metodo_entrega_id    smallint    not null references public.metodo_entrega (id),
  add column ubigeo_codigo        char(6)     references public.ubigeo (codigo),
  add column referencia_envio     varchar(200),
  add column tienda_id            bigint      references public.tienda (id),
  add column agencia_envio_id     bigint      references public.agencia_envio (id),
  add column recoge_dni           varchar(8),
  add column clave_recojo         varchar(20);

create index idx_pedido_metodo_entrega on public.pedido (metodo_entrega_id);
create index idx_pedido_ubigeo         on public.pedido (ubigeo_codigo);
create index idx_pedido_tienda         on public.pedido (tienda_id);
create index idx_pedido_agencia        on public.pedido (agencia_envio_id);

-- Ampliar detalle de pedido con referencia a la variante y snapshot de talla/color
alter table public.pedido_detalle
  add column variante_id          uuid references public.producto_variante (id),
  add column descripcion_variante varchar(120);
create index idx_pedido_detalle_variante on public.pedido_detalle (variante_id);

-- Datos fiscales / comprobante del pedido (Boleta o Factura)
create table public.pedido_facturacion (
  id                       bigint generated always as identity primary key,
  pedido_id                bigint       not null unique references public.pedido (id) on delete cascade,
  tipo_comprobante_codigo  varchar(10)  not null references public.tipo_comprobante (codigo),
  tipo_documento_codigo    varchar(15)  not null references public.tipo_documento (codigo),
  numero_documento         varchar(20)  not null,
  nombre_o_razon_social    varchar(200) not null,
  mismos_datos_envio       boolean      not null default true,
  direccion_fiscal         varchar(255),
  ubigeo_codigo            char(6)      references public.ubigeo (codigo),
  codigo_pais_telefono     varchar(8),
  telefono                 varchar(20)
);
comment on table public.pedido_facturacion is 'Boleta (DNI/CE/Pasaporte y datos iguales o distintos) o Factura (RUC + Razón social).';
create index idx_pedido_facturacion_ubigeo on public.pedido_facturacion (ubigeo_codigo);

alter table public.pago
  add column datos_respuesta jsonb;


-- =====================================================================
-- BLOQUE 5: Contenido de tienda (Banners, FAQs, Políticas, Newsletter)
-- =====================================================================

create table public.banner (
  id         bigint generated always as identity primary key,
  ubicacion  varchar(30)  not null check (ubicacion in ('BARRA_ANUNCIO','HERO','MARQUEE')),
  titulo     varchar(150) not null,
  subtitulo  varchar(255),
  media_url  varchar(255),
  texto_cta  varchar(60),
  url_cta    varchar(200),
  orden      smallint     not null default 0,
  activo     boolean      not null default true
);

create table public.pregunta_frecuente (
  id         smallint generated always as identity primary key,
  pregunta   varchar(200) not null,
  respuesta  text         not null,
  orden      smallint     not null default 0,
  activo     boolean      not null default true
);

create table public.politica_tienda (
  slug            varchar(40)  primary key,
  titulo          varchar(120) not null,
  contenido       text         not null,
  actualizado_en  timestamptz  not null default now()
);

create table public.suscriptor_newsletter (
  id                 bigint generated always as identity primary key,
  correo             varchar(150) not null unique,
  acepto_privacidad  boolean      not null default true,
  creado_en          timestamptz  not null default now()
);

create table public.configuracion_tienda (
  clave  varchar(60) primary key,
  valor  text        not null
);

insert into public.configuracion_tienda (clave, valor) values
  ('nombre_tienda',      'Mi Tienda'),
  ('telefono_contacto',  '+51931133631'),
  ('correo_contacto',    'team@mitienda.com'),
  ('libro_reclamaciones','https://respondo.pe/libro/demo');


-- =====================================================================
-- BLOQUE 6: Seguridad (Row Level Security en todas las tablas nuevas)
-- =====================================================================

alter table public.tipo_documento       enable row level security;
alter table public.tipo_comprobante     enable row level security;
alter table public.zona_envio           enable row level security;
alter table public.ubigeo               enable row level security;
alter table public.metodo_entrega       enable row level security;
alter table public.tienda               enable row level security;
alter table public.agencia_envio        enable row level security;
alter table public.atributo             enable row level security;
alter table public.atributo_valor       enable row level security;
alter table public.producto_variante    enable row level security;
alter table public.variante_valor       enable row level security;
alter table public.resena_producto      enable row level security;
alter table public.resena_imagen        enable row level security;
alter table public.carrito              enable row level security;
alter table public.carrito_item         enable row level security;
alter table public.pedido_facturacion   enable row level security;
alter table public.banner               enable row level security;
alter table public.pregunta_frecuente   enable row level security;
alter table public.politica_tienda      enable row level security;
alter table public.suscriptor_newsletter enable row level security;
alter table public.configuracion_tienda enable row level security;

-- Lectura pública de catálogos y contenido de tienda
create policy "publico_tipo_documento"     on public.tipo_documento     for select to anon, authenticated using (activo);
create policy "publico_tipo_comprobante"   on public.tipo_comprobante   for select to anon, authenticated using (true);
create policy "publico_zona_envio"         on public.zona_envio         for select to anon, authenticated using (true);
create policy "publico_ubigeo"             on public.ubigeo             for select to anon, authenticated using (true);
create policy "publico_metodo_entrega"     on public.metodo_entrega     for select to anon, authenticated using (activo);
create policy "publico_tienda"             on public.tienda             for select to anon, authenticated using (activo);
create policy "publico_agencia_envio"      on public.agencia_envio      for select to anon, authenticated using (activo);
create policy "publico_atributo"           on public.atributo           for select to anon, authenticated using (true);
create policy "publico_atributo_valor"     on public.atributo_valor     for select to anon, authenticated using (true);
create policy "publico_producto_variante"  on public.producto_variante  for select to anon, authenticated using (activo);
create policy "publico_variante_valor"     on public.variante_valor     for select to anon, authenticated using (true);
create policy "publico_resena_producto"    on public.resena_producto    for select to anon, authenticated using (aprobada);
create policy "publico_resena_imagen"      on public.resena_imagen      for select to anon, authenticated using (
  resena_id in (select id from public.resena_producto where aprobada)
);
create policy "publico_banner"             on public.banner             for select to anon, authenticated using (activo);
create policy "publico_pregunta_frecuente" on public.pregunta_frecuente for select to anon, authenticated using (activo);
create policy "publico_politica_tienda"    on public.politica_tienda    for select to anon, authenticated using (true);
create policy "publico_config_tienda"      on public.configuracion_tienda for select to anon, authenticated using (true);

-- Datos propios (cuando el cliente esté autenticado; el backend Java usa rol postgres)
create policy "carrito_propio" on public.carrito for select to authenticated using (
  persona_id in (select id from public.persona where auth_usuario_id = (select auth.uid()))
);
create policy "carrito_item_propio" on public.carrito_item for select to authenticated using (
  carrito_id in (
    select c.id from public.carrito c
    join public.persona pe on pe.id = c.persona_id
    where pe.auth_usuario_id = (select auth.uid())
  )
);
create policy "pedido_facturacion_propia" on public.pedido_facturacion for select to authenticated using (
  pedido_id in (
    select p.id from public.pedido p
    join public.persona pe on pe.id = p.persona_id
    where pe.auth_usuario_id = (select auth.uid())
  )
);
