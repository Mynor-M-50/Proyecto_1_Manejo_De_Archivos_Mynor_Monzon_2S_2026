export interface DetalleCompraRequest {
  idProducto: number;
  cantidad: number;
  costoUnitario: number;
}

export interface CompraRequest {
  idProveedor: number;
  numeroDoc: string;
  detalles: DetalleCompraRequest[];
}

export interface LineaCompra {
  idProducto: number;
  codigo: string;
  producto: string;
  cantidad: number;
  costoUnitario: number;
  subtotal: number;
}

export interface Compra {
  idCompra: number;
  numeroDoc: string;
  fecha: string;
  total: number;
  estado: string;
  idProveedor: number;
  proveedor: string;
  usuario: string;
  detalles: LineaCompra[];
}
