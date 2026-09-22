import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { API_URL } from '../config';
import { FiltroReporte, FilaReporte } from '../models/reporte';

// Todos los reportes se consultan igual: una ruta y unos filtros opcionales.
// La ruta define a que area pertenece (inventario, compras, ventas o logs).
@Injectable({ providedIn: 'root' })
export class ReporteService {
  private http = inject(HttpClient);
  private url = `${API_URL}/reportes`;

  consultar(ruta: string, filtro: FiltroReporte = {}): Observable<FilaReporte[]> {
    let params = new HttpParams();
    if (filtro.limite) params = params.set('limite', filtro.limite);
    if (filtro.desde) params = params.set('desde', filtro.desde);
    if (filtro.hasta) params = params.set('hasta', filtro.hasta);
    if (filtro.agrupacion) params = params.set('agrupacion', filtro.agrupacion);
    return this.http.get<FilaReporte[]>(`${this.url}/${ruta}`, { params });
  }
}
