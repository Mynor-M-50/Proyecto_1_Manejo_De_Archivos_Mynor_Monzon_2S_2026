export interface Categoria {
  idCategoria: number;
  nombre: string;
  descripcion: string | null;
}

export interface CategoriaRequest {
  nombre: string;
  descripcion: string | null;
}

export interface Producto {
  idProducto: number;
  codigo: string;
  nombre: string;
  descripcion: string | null;
  unidadMedida: string;
  precioVenta: number;
  stockMinimo: number;
  existenciaActual: number;
  estado: boolean;
  idCategoria: number;
  categoria: string;
  stockBajo: boolean;
}

export interface ProductoRequest {
  codigo: string;
  nombre: string;
  descripcion: string | null;
  unidadMedida: string | null;
  precioVenta: number;
  stockMinimo: number;
  idCategoria: number;
}

export interface Proveedor {
  idProveedor: number;
  nit: string;
  nombre: string;
  contacto: string | null;
  telefono: string | null;
  email: string | null;
  direccion: string | null;
  estado: boolean;
}

export interface ProveedorRequest {
  nit: string;
  nombre: string;
  contacto: string | null;
  telefono: string | null;
  email: string | null;
  direccion: string | null;
}

export interface Cliente {
  idCliente: number;
  nit: string;
  nombre: string;
  telefono: string | null;
  email: string | null;
  direccion: string | null;
  estado: boolean;
}

export interface ClienteRequest {
  nit: string;
  nombre: string;
  telefono: string | null;
  email: string | null;
  direccion: string | null;
}

export interface Area {
  idArea: number;
  nombre: string;
  descripcion: string | null;
}

export interface Usuario {
  idUsuario: number;
  nombreCompleto: string;
  username: string;
  estado: boolean;
  fechaCreacion: string;
  idArea: number;
  area: string;
}

export interface UsuarioRequest {
  nombreCompleto: string;
  username: string;
  password: string;
  idArea: number;
}
