import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { API_URL } from '../config';
import { Producto, ProductoRequest } from '../models/catalogo';

@Injectable({ providedIn: 'root' })
export class ProductoService {
  private http = inject(HttpClient);
  private url = `${API_URL}/productos`;

  listar(soloActivos = false): Observable<Producto[]> {
    const params = new HttpParams().set('activos', soloActivos);
    return this.http.get<Producto[]>(this.url, { params });
  }

  stockBajo(): Observable<Producto[]> {
    return this.http.get<Producto[]>(`${this.url}/stock-bajo`);
  }

  crear(req: ProductoRequest): Observable<Producto> {
    return this.http.post<Producto>(this.url, req);
  }

  actualizar(id: number, req: ProductoRequest): Observable<Producto> {
    return this.http.put<Producto>(`${this.url}/${id}`, req);
  }

  cambiarEstado(id: number, activo: boolean): Observable<Producto> {
    const params = new HttpParams().set('activo', activo);
    return this.http.patch<Producto>(`${this.url}/${id}/estado`, null, { params });
  }
}
