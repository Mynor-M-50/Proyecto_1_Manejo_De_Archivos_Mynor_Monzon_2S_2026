export interface DetalleVentaRequest {
  idProducto: number;
  cantidad: number;
}

export interface VentaRequest {
  idCliente: number;
  detalles: DetalleVentaRequest[];
}

export interface LineaVenta {
  idProducto: number;
  codigo: string;
  producto: string;
  cantidad: number;
  precioUnitario: number;
  subtotal: number;
}

export interface Venta {
  idVenta: number;
  numeroFactura: string;
  fecha: string;
  subtotal: number;
  iva: number;
  total: number;
  estado: string;
  idCliente: number;
  cliente: string;
  nitCliente: string;
  usuario: string;
  detalles: LineaVenta[];
}
