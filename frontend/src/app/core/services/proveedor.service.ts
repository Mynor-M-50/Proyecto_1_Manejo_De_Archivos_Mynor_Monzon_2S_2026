import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { API_URL } from '../config';
import { Proveedor, ProveedorRequest } from '../models/catalogo';

@Injectable({ providedIn: 'root' })
export class ProveedorService {
  private http = inject(HttpClient);
  private url = `${API_URL}/proveedores`;

  listar(soloActivos = false): Observable<Proveedor[]> {
    const params = new HttpParams().set('activos', soloActivos);
    return this.http.get<Proveedor[]>(this.url, { params });
  }

  crear(req: ProveedorRequest): Observable<Proveedor> {
    return this.http.post<Proveedor>(this.url, req);
  }

  actualizar(id: number, req: ProveedorRequest): Observable<Proveedor> {
    return this.http.put<Proveedor>(`${this.url}/${id}`, req);
  }

  cambiarEstado(id: number, activo: boolean): Observable<Proveedor> {
    const params = new HttpParams().set('activo', activo);
    return this.http.patch<Proveedor>(`${this.url}/${id}/estado`, null, { params });
  }
}
