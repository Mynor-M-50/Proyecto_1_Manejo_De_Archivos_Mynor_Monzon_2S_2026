import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { API_URL } from '../config';
import { Lote, Movimiento } from '../models/inventario';

@Injectable({ providedIn: 'root' })
export class InventarioService {
  private http = inject(HttpClient);
  private url = `${API_URL}/inventario/productos`;

  movimientos(idProducto: number): Observable<Movimiento[]> {
    return this.http.get<Movimiento[]>(`${this.url}/${idProducto}/movimientos`);
  }

  lotes(idProducto: number): Observable<Lote[]> {
    return this.http.get<Lote[]>(`${this.url}/${idProducto}/lotes`);
  }
}
