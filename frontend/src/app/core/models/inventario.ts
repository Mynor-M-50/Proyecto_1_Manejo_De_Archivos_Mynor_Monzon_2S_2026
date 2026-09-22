export interface Movimiento {
  idMovimiento: number;
  fecha: string;
  tipo: 'ENTRADA' | 'SALIDA';
  cantidad: number;
  costoUnitario: number;
  existenciaAnterior: number;
  existenciaNueva: number;
  origenTipo: string;
  origenId: number | null;
  usuario: string;
}

export interface Lote {
  idLote: number;
  fechaIngreso: string;
  cantidadInicial: number;
  cantidadDisponible: number;
  costoUnitario: number;
}
