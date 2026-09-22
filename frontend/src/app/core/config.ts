// Direccion del backend Spring Boot
export const API_URL = 'http://localhost:8080/api';

// Areas del sistema (coinciden con la tabla area y con los roles del backend)
export const AREAS = {
  ADMIN: 'ADMINISTRACION',
  COMPRAS: 'COMPRAS',
  INVENTARIO: 'INVENTARIO',
  VENTAS: 'VENTAS',
} as const;
