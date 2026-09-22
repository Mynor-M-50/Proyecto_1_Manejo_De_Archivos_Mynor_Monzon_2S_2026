// Filtros comunes que aceptan los endpoints de reportes
export interface FiltroReporte {
  limite?: number;
  desde?: string;
  hasta?: string;
  agrupacion?: string;
}

// Describe una columna de la tabla de resultados de un reporte
export interface Columna {
  campo: string;
  titulo: string;
  tipo?: 'texto' | 'numero' | 'moneda' | 'fecha';
}

// Una fila de reporte es un objeto con los campos que devuelve el backend
export type FilaReporte = Record<string, unknown>;
