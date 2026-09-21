-- Mynor Monzon 202230884
-- Proyecto 1 Archivos Mini ERP
-- Metodo de inventario: PEPS

-- ==== Tablas sin dependencia ===

CREATE TABLE area (
    id_area      SERIAL PRIMARY KEY,
    nombre       VARCHAR(50) NOT NULL UNIQUE,
    descripcion  VARCHAR(200)
);
 
CREATE TABLE categoria (
    id_categoria SERIAL PRIMARY KEY,
    nombre       VARCHAR(80) NOT NULL UNIQUE,
    descripcion  VARCHAR(200)
);
 
CREATE TABLE proveedor (
    id_proveedor SERIAL PRIMARY KEY,
    nit          VARCHAR(20) NOT NULL UNIQUE,
    nombre       VARCHAR(120) NOT NULL,
    contacto     VARCHAR(120),
    telefono     VARCHAR(20),
    email        VARCHAR(120),
    direccion    VARCHAR(200),
    estado       BOOLEAN NOT NULL DEFAULT TRUE
);
 
CREATE TABLE cliente (
    id_cliente SERIAL PRIMARY KEY,
    nit        VARCHAR(20) NOT NULL UNIQUE,
    nombre     VARCHAR(120) NOT NULL,
    telefono   VARCHAR(20),
    email      VARCHAR(120),
    direccion  VARCHAR(200),
    estado     BOOLEAN NOT NULL DEFAULT TRUE
);

-- === Tablas dependientes de las pasadas ===

CREATE TABLE usuario (
    id_usuario      SERIAL PRIMARY KEY,
    nombre_completo VARCHAR(120) NOT NULL,
    username        VARCHAR(40) NOT NULL UNIQUE,
    password_hash   VARCHAR(200) NOT NULL,
    estado          BOOLEAN NOT NULL DEFAULT TRUE,
    fecha_creacion  TIMESTAMP NOT NULL DEFAULT NOW(),
    id_area         INTEGER NOT NULL,
    CONSTRAINT fk_usuario_area FOREIGN KEY (id_area)
        REFERENCES area (id_area)
);
 
CREATE TABLE producto (
    id_producto       SERIAL PRIMARY KEY,
    codigo            VARCHAR(30) NOT NULL UNIQUE,
    nombre            VARCHAR(120) NOT NULL,
    descripcion       VARCHAR(250),
    unidad_medida     VARCHAR(20) NOT NULL DEFAULT 'UNIDAD',
    precio_venta      NUMERIC(12,2) NOT NULL,
    stock_minimo      INTEGER NOT NULL DEFAULT 0,
    existencia_actual INTEGER NOT NULL DEFAULT 0,
    estado            BOOLEAN NOT NULL DEFAULT TRUE,
    id_categoria      INTEGER NOT NULL,
    CONSTRAINT fk_producto_categoria FOREIGN KEY (id_categoria)
        REFERENCES categoria (id_categoria),
    CONSTRAINT ck_producto_precio CHECK (precio_venta > 0),
    CONSTRAINT ck_producto_stock_min CHECK (stock_minimo >= 0),
    CONSTRAINT ck_producto_existencia CHECK (existencia_actual >= 0)
);

-- === Compras ===

CREATE TABLE compra (
    id_compra    SERIAL PRIMARY KEY,
    numero_doc   VARCHAR(30) NOT NULL,
    fecha        TIMESTAMP NOT NULL DEFAULT NOW(),
    total        NUMERIC(14,2) NOT NULL DEFAULT 0,
    estado       VARCHAR(20) NOT NULL DEFAULT 'REGISTRADA',
    id_proveedor INTEGER NOT NULL,
    id_usuario   INTEGER NOT NULL,
    CONSTRAINT fk_compra_proveedor FOREIGN KEY (id_proveedor)
        REFERENCES proveedor (id_proveedor),
    CONSTRAINT fk_compra_usuario FOREIGN KEY (id_usuario)
        REFERENCES usuario (id_usuario),
    CONSTRAINT ck_compra_total CHECK (total >= 0)
);
 
CREATE TABLE detalle_compra (
    id_detalle_compra SERIAL PRIMARY KEY,
    cantidad          INTEGER NOT NULL,
    costo_unitario    NUMERIC(12,2) NOT NULL,
    subtotal          NUMERIC(14,2) NOT NULL,
    id_compra         INTEGER NOT NULL,
    id_producto       INTEGER NOT NULL,
    CONSTRAINT fk_detcompra_compra FOREIGN KEY (id_compra)
        REFERENCES compra (id_compra),
    CONSTRAINT fk_detcompra_producto FOREIGN KEY (id_producto)
        REFERENCES producto (id_producto),
    CONSTRAINT ck_detcompra_cantidad CHECK (cantidad > 0),
    CONSTRAINT ck_detcompra_costo CHECK (costo_unitario > 0),
    CONSTRAINT uq_detcompra UNIQUE (id_compra, id_producto)
);

-- === Lotes (PEPS) ===

CREATE TABLE lote (
    id_lote             SERIAL PRIMARY KEY,
    fecha_ingreso       TIMESTAMP NOT NULL DEFAULT NOW(),
    cantidad_inicial    INTEGER NOT NULL,
    cantidad_disponible INTEGER NOT NULL,
    costo_unitario      NUMERIC(12,2) NOT NULL,
    id_producto         INTEGER NOT NULL,
    id_detalle_compra   INTEGER,
    CONSTRAINT fk_lote_producto FOREIGN KEY (id_producto)
        REFERENCES producto (id_producto),
    CONSTRAINT fk_lote_detcompra FOREIGN KEY (id_detalle_compra)
        REFERENCES detalle_compra (id_detalle_compra),
    CONSTRAINT ck_lote_inicial CHECK (cantidad_inicial > 0),
    CONSTRAINT ck_lote_disponible CHECK (cantidad_disponible >= 0),
    CONSTRAINT ck_lote_costo CHECK (costo_unitario > 0)
);


-- === Ventas ===

CREATE TABLE venta (
    id_venta       SERIAL PRIMARY KEY,
    numero_factura VARCHAR(30) NOT NULL UNIQUE,
    fecha          TIMESTAMP NOT NULL DEFAULT NOW(),
    subtotal       NUMERIC(14,2) NOT NULL DEFAULT 0,
    iva            NUMERIC(14,2) NOT NULL DEFAULT 0,
    total          NUMERIC(14,2) NOT NULL DEFAULT 0,
    estado         VARCHAR(20) NOT NULL DEFAULT 'EMITIDA',
    id_cliente     INTEGER NOT NULL,
    id_usuario     INTEGER NOT NULL,
    CONSTRAINT fk_venta_cliente FOREIGN KEY (id_cliente)
        REFERENCES cliente (id_cliente),
    CONSTRAINT fk_venta_usuario FOREIGN KEY (id_usuario)
        REFERENCES usuario (id_usuario),
    CONSTRAINT ck_venta_montos CHECK (subtotal >= 0 AND iva >= 0 AND total >= 0)
);
 
CREATE TABLE detalle_venta (
    id_detalle_venta SERIAL PRIMARY KEY,
    cantidad         INTEGER NOT NULL,
    precio_unitario  NUMERIC(12,2) NOT NULL,
    costo_unitario   NUMERIC(12,2) NOT NULL DEFAULT 0,
    subtotal         NUMERIC(14,2) NOT NULL,
    id_venta         INTEGER NOT NULL,
    id_producto      INTEGER NOT NULL,
    CONSTRAINT fk_detventa_venta FOREIGN KEY (id_venta)
        REFERENCES venta (id_venta),
    CONSTRAINT fk_detventa_producto FOREIGN KEY (id_producto)
        REFERENCES producto (id_producto),
    CONSTRAINT ck_detventa_cantidad CHECK (cantidad > 0),
    CONSTRAINT ck_detventa_precio CHECK (precio_unitario > 0),
    CONSTRAINT uq_detventa UNIQUE (id_venta, id_producto)
);


-- === Movimiento de inventario ==

CREATE TABLE movimiento_inv (
    id_movimiento       SERIAL PRIMARY KEY,
    tipo                VARCHAR(10) NOT NULL,
    cantidad            INTEGER NOT NULL,
    costo_unitario      NUMERIC(12,2) NOT NULL DEFAULT 0,
    existencia_anterior INTEGER NOT NULL,
    existencia_nueva    INTEGER NOT NULL,
    fecha               TIMESTAMP NOT NULL DEFAULT NOW(),
    origen_tipo         VARCHAR(20) NOT NULL,
    origen_id           INTEGER,
    id_producto         INTEGER NOT NULL,
    id_usuario          INTEGER NOT NULL,
    CONSTRAINT fk_mov_producto FOREIGN KEY (id_producto)
        REFERENCES producto (id_producto),
    CONSTRAINT fk_mov_usuario FOREIGN KEY (id_usuario)
        REFERENCES usuario (id_usuario),
    CONSTRAINT ck_mov_tipo CHECK (tipo IN ('ENTRADA', 'SALIDA')),
    CONSTRAINT ck_mov_origen CHECK (origen_tipo IN ('COMPRA', 'VENTA', 'AJUSTE')),
    CONSTRAINT ck_mov_cantidad CHECK (cantidad > 0),
    CONSTRAINT ck_mov_existencias CHECK (existencia_anterior >= 0 AND existencia_nueva >= 0)
);


-- Bitacora de acciones ===

CREATE TABLE bitacora (
    id_log      SERIAL PRIMARY KEY,
    modulo      VARCHAR(40) NOT NULL,
    accion      VARCHAR(30) NOT NULL,
    descripcion VARCHAR(300),
    fecha_hora  TIMESTAMP NOT NULL DEFAULT NOW(),
    id_usuario  INTEGER NOT NULL,
    CONSTRAINT fk_bitacora_usuario FOREIGN KEY (id_usuario)
        REFERENCES usuario (id_usuario)
);


-- === Indices para reportes ===

CREATE INDEX idx_compra_fecha ON compra (fecha);
CREATE INDEX idx_venta_fecha ON venta (fecha);
CREATE INDEX idx_mov_producto_fecha ON movimiento_inv (id_producto, fecha);
CREATE INDEX idx_lote_peps ON lote (id_producto, fecha_ingreso);
CREATE INDEX idx_bitacora_fecha ON bitacora (fecha_hora);









