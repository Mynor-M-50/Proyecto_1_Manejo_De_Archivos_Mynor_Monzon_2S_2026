import { HttpErrorResponse } from '@angular/common/http';

// Convierte la respuesta de error del backend (ErrorResponse) en un texto legible
export function mensajeError(err: unknown): string {
  if (err instanceof HttpErrorResponse) {
    if (err.status === 0) {
      return 'No se pudo conectar con el servidor. Verifique que el backend este corriendo.';
    }
    const cuerpo = err.error;
    if (cuerpo && typeof cuerpo === 'object' && cuerpo.mensaje) {
      const detalles: string[] = cuerpo.detalles ?? [];
      return detalles.length ? `${cuerpo.mensaje}: ${detalles.join(', ')}` : cuerpo.mensaje;
    }
    return `Error ${err.status}`;
  }
  return 'Ocurrio un error inesperado';
}
