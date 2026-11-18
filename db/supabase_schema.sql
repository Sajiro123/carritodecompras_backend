-- =====================================================================
--  CARRITO DE COMPRAS - Esquema de base de datos (PostgreSQL / Supabase)
--  Tablas en español, snake_case.
--  Ejecutar en: Supabase Dashboard > SQL Editor > New query > Run
-- =====================================================================

-- ---------------------------------------------------------------------
-- 0. Función utilitaria: actualiza la columna actualizado_en
-- ---------------------------------------------------------------------
create or replace function public.fn_actualizar_fecha()
returns trigger
language plpgsql
set search_path = ''
as $$
begin
  new.actualizado_en = now();
  return new;
end;
$$;

-- ---------------------------------------------------------------------
-- 1. CATÁLOGOS (tablas pequeñas de referencia)
-- ---------------------------------------------------------------------
create table public.estado_pedido (
  id          smallint generated always as identity primary key,
  codigo      varchar(20)  not null unique,
  nombre      varchar(60)  not null,
  orden       smallint     not null default 0
);
comment on table public.estado_pedido is 'Estados por los que pasa un pedido (PENDIENTE, PAGADO, ENVIADO...)';

create table public.metodo_pago (
  id          smallint generated always as identity primary key,
  codigo      varchar(20)  not null unique,
  nombre      varchar(60)  not null,
  activo      boolean      not null default true
);
comment on table public.metodo_pago is 'Formas de pago disponibles (EFECTIVO, TARJETA, YAPE...)';

-- ---------------------------------------------------------------------
-- 2. PERSONAS Y DIRECCIONES
-- ---------------------------------------------------------------------
create table public.persona (
  id                bigint generated always as identity primary key,
  auth_usuario_id   uuid unique references auth.users (id) on delete set null,
  tipo_documento    varchar(15)  not null default 'DNI'
                    check (tipo_documento in ('DNI','CE','RUC','PASAPORTE')),
  numero_documento  varchar(20),
  nombres           varchar(100) not null,
  apellidos         varchar(100) not null,
  correo            varchar(150) not null unique,
  telefono          varchar(20),
  fecha_nacimiento  date,
  activo            boolean      not null default true,
  creado_en         timestamptz  not null default now(),
  actualizado_en    timestamptz  not null default now(),
  unique (tipo_documento, numero_documento)
);
comment on table public.persona is 'Clientes del carrito. auth_usuario_id enlaza con el login de Supabase (opcional).';

create trigger trg_persona_actualizado
  before update on public.persona
  for each row execute function public.fn_actualizar_fecha();

create table public.direccion (
  id            bigint generated always as identity primary key,
  persona_id    bigint       not null references public.persona (id) on delete cascade,
  alias         varchar(50)  not null default 'Casa',
  departamento  varchar(60)  not null,
  provincia     varchar(60)  not null,
  distrito      varchar(60)  not null,
  direccion     varchar(255) not null,
  referencia    varchar(255),
  es_principal  boolean      not null default false,
  creado_en     timestamptz  not null default now()
);
comment on table public.direccion is 'Direcciones de envío de cada persona.';
create index idx_direccion_persona on public.direccion (persona_id);
-- Solo una dirección principal por persona
create unique index uq_direccion_principal on public.direccion (persona_id) where es_principal;

-- ---------------------------------------------------------------------
-- 3. CATÁLOGO DE PRODUCTOS
-- ---------------------------------------------------------------------
create table public.categoria (
  id                 bigint generated always as identity primary key,
  categoria_padre_id bigint       references public.categoria (id) on delete set null,
  nombre             varchar(100) not null,
  slug               varchar(120) not null unique,
  descripcion        varchar(300),
  imagen_url         varchar(255),
  activo             boolean      not null default true,
  creado_en          timestamptz  not null default now()
);
comment on table public.categoria is 'Categorías de productos (admite subcategorías con categoria_padre_id).';
create index idx_categoria_padre on public.categoria (categoria_padre_id);

create table public.marca (
  id         bigint generated always as identity primary key,
  nombre     varchar(100) not null unique,
  activo     boolean      not null default true
);

create table public.producto (
  id              bigint generated always as identity primary key,
  categoria_id    bigint        not null references public.categoria (id),
  marca_id        bigint        references public.marca (id) on delete set null,
  sku             varchar(40)   not null unique,
  nombre          varchar(150)  not null,
  slug            varchar(170)  not null unique,
  descripcion     text,
  precio          numeric(10,2) not null check (precio >= 0),
  precio_oferta   numeric(10,2) check (precio_oferta >= 0 and precio_oferta <= precio),
  stock           integer       not null default 0 check (stock >= 0),
  imagen_url      varchar(255),
  activo          boolean       not null default true,
  creado_en       timestamptz   not null default now(),
  actualizado_en  timestamptz   not null default now()
);
comment on table public.producto is 'Productos a la venta.';
create index idx_producto_categoria on public.producto (categoria_id);
create index idx_producto_marca     on public.producto (marca_id);

create trigger trg_producto_actualizado
  before update on public.producto
  for each row execute function public.fn_actualizar_fecha();

create table public.producto_imagen (
  id           bigint generated always as identity primary key,
  producto_id  bigint       not null references public.producto (id) on delete cascade,
  url          varchar(255) not null,
  orden        smallint     not null default 0
);
create index idx_producto_imagen_producto on public.producto_imagen (producto_id);

-- ---------------------------------------------------------------------
-- 4. PEDIDOS
-- ---------------------------------------------------------------------
create table public.pedido (
  id                  bigint generated always as identity primary key,
  persona_id          bigint        references public.persona (id) on delete set null,
  estado_pedido_id    smallint      not null references public.estado_pedido (id),
  metodo_pago_id      smallint      references public.metodo_pago (id),
  -- Datos del cliente "congelados" al momento de la compra
  cliente_nombre      varchar(200)  not null,
  cliente_correo      varchar(150)  not null,
  cliente_telefono    varchar(20),
  direccion_envio     varchar(300)  not null,
  -- Importes
  subtotal            numeric(10,2) not null check (subtotal >= 0),
  descuento           numeric(10,2) not null default 0 check (descuento >= 0),
  costo_envio         numeric(10,2) not null default 0 check (costo_envio >= 0),
  total               numeric(10,2) not null check (total >= 0),
  observacion         varchar(300),
  creado_en           timestamptz   not null default now(),
  actualizado_en      timestamptz   not null default now()
);
comment on table public.pedido is 'Cabecera de la compra. Guarda copia de los datos del cliente porque pueden cambiar después.';
create index idx_pedido_persona on public.pedido (persona_id);
create index idx_pedido_estado  on public.pedido (estado_pedido_id);
create index idx_pedido_metodo_pago on public.pedido (metodo_pago_id);
create index idx_pedido_fecha   on public.pedido (creado_en desc);

create trigger trg_pedido_actualizado
  before update on public.pedido
  for each row execute function public.fn_actualizar_fecha();

create table public.pedido_detalle (
  id               bigint generated always as identity primary key,
  pedido_id        bigint        not null references public.pedido (id) on delete cascade,
  producto_id      bigint        not null references public.producto (id),
  nombre_producto  varchar(150)  not null,
  cantidad         integer       not null check (cantidad > 0),
  precio_unitario  numeric(10,2) not null check (precio_unitario >= 0),
  subtotal         numeric(10,2) not null check (subtotal >= 0)
);
comment on table public.pedido_detalle is 'Líneas del pedido. precio_unitario y nombre_producto se copian del producto al comprar.';
create index idx_pedido_detalle_pedido   on public.pedido_detalle (pedido_id);
create index idx_pedido_detalle_producto on public.pedido_detalle (producto_id);

create table public.pago (
  id              bigint generated always as identity primary key,
  pedido_id       bigint        not null references public.pedido (id) on delete cascade,
  metodo_pago_id  smallint      not null references public.metodo_pago (id),
  monto           numeric(10,2) not null check (monto > 0),
  estado          varchar(15)   not null default 'PENDIENTE'
                  check (estado in ('PENDIENTE','APROBADO','RECHAZADO','REEMBOLSADO')),
  referencia      varchar(100),
  pagado_en       timestamptz,
  creado_en       timestamptz   not null default now()
);
comment on table public.pago is 'Intentos/registros de pago de un pedido (para la pasarela de pago).';
create index idx_pago_pedido on public.pago (pedido_id);
create index idx_pago_metodo_pago on public.pago (metodo_pago_id);

-- ---------------------------------------------------------------------
-- 5. SEGURIDAD (Row Level Security)
--    Si el backend Java se conecta con el usuario "postgres" (cadena JDBC),
--    RLS no le afecta. RLS protege el acceso directo vía API REST de Supabase.
-- ---------------------------------------------------------------------
alter table public.estado_pedido   enable row level security;
alter table public.metodo_pago     enable row level security;
alter table public.persona         enable row level security;
alter table public.direccion       enable row level security;
alter table public.categoria       enable row level security;
alter table public.marca           enable row level security;
alter table public.producto        enable row level security;
alter table public.producto_imagen enable row level security;
alter table public.pedido          enable row level security;
alter table public.pedido_detalle  enable row level security;
alter table public.pago            enable row level security;

-- Catálogo público (solo lectura)
create policy "catalogo_publico_categoria" on public.categoria
  for select to anon, authenticated using (activo);
create policy "catalogo_publico_marca" on public.marca
  for select to anon, authenticated using (activo);
create policy "catalogo_publico_producto" on public.producto
  for select to anon, authenticated using (activo);
create policy "catalogo_publico_producto_imagen" on public.producto_imagen
  for select to anon, authenticated using (true);
create policy "catalogo_publico_metodo_pago" on public.metodo_pago
  for select to anon, authenticated using (activo);
create policy "catalogo_publico_estado_pedido" on public.estado_pedido
  for select to anon, authenticated using (true);

-- Datos personales: cada usuario autenticado ve solo lo suyo
create policy "persona_propia" on public.persona
  for select to authenticated using (auth_usuario_id = (select auth.uid()));
create policy "direccion_propia" on public.direccion
  for select to authenticated using (
    persona_id in (select id from public.persona where auth_usuario_id = (select auth.uid()))
  );
create policy "pedido_propio" on public.pedido
  for select to authenticated using (
    persona_id in (select id from public.persona where auth_usuario_id = (select auth.uid()))
  );
create policy "pedido_detalle_propio" on public.pedido_detalle
  for select to authenticated using (
    pedido_id in (
      select p.id from public.pedido p
      join public.persona pe on pe.id = p.persona_id
      where pe.auth_usuario_id = (select auth.uid())
    )
  );
create policy "pago_propio" on public.pago
  for select to authenticated using (
    pedido_id in (
      select p.id from public.pedido p
      join public.persona pe on pe.id = p.persona_id
      where pe.auth_usuario_id = (select auth.uid())
    )
  );

-- ---------------------------------------------------------------------
-- 6. DATOS INICIALES
-- ---------------------------------------------------------------------
insert into public.estado_pedido (codigo, nombre, orden) values
  ('PENDIENTE', 'Pendiente de pago', 1),
  ('PAGADO',    'Pagado',            2),
  ('PREPARANDO','En preparación',    3),
  ('ENVIADO',   'Enviado',           4),
  ('ENTREGADO', 'Entregado',         5),
  ('CANCELADO', 'Cancelado',         6);

insert into public.metodo_pago (codigo, nombre) values
  ('EFECTIVO',      'Pago contra entrega'),
  ('TARJETA',       'Tarjeta de crédito/débito'),
  ('YAPE',          'Yape'),
  ('PLIN',          'Plin'),
  ('TRANSFERENCIA', 'Transferencia bancaria');

insert into public.categoria (nombre, slug, descripcion) values
  ('Ropa',        'ropa',        'Polos, casacas y más'),
  ('Calzado',     'calzado',     'Zapatillas y zapatos'),
  ('Accesorios',  'accesorios',  'Mochilas, gorras y relojes');

insert into public.marca (nombre) values ('Genérica'), ('Urban Style');

insert into public.producto (categoria_id, marca_id, sku, nombre, slug, descripcion, precio, stock, imagen_url)
select c.id, m.id, v.sku, v.nombre, v.slug, v.descripcion, v.precio, v.stock, v.imagen_url
from (values
  ('ropa',       'POL-001', 'Polo básico',        'polo-basico',        'Algodón 100%, color negro', 39.90, 50, 'https://picsum.photos/seed/polo/400/300'),
  ('calzado',    'ZAP-001', 'Zapatillas urbanas', 'zapatillas-urbanas', 'Cómodas para el día a día', 149.00, 20, 'https://picsum.photos/seed/shoes/400/300'),
  ('accesorios', 'MOC-001', 'Mochila 20L',        'mochila-20l',        'Resistente al agua',        89.50, 15, 'https://picsum.photos/seed/bag/400/300'),
  ('accesorios', 'GOR-001', 'Gorra deportiva',    'gorra-deportiva',    'Ajustable',                 29.00, 40, 'https://picsum.photos/seed/cap/400/300'),
  ('accesorios', 'REL-001', 'Reloj digital',      'reloj-digital',      'Sumergible hasta 50m',     119.90, 10, 'https://picsum.photos/seed/watch/400/300')
) as v(cat_slug, sku, nombre, slug, descripcion, precio, stock, imagen_url)
join public.categoria c on c.slug = v.cat_slug
cross join (select id from public.marca where nombre = 'Genérica') m;
