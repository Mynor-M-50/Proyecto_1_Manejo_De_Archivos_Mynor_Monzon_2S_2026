import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { API_URL } from '../config';
import { Cliente, ClienteRequest } from '../models/catalogo';

@Injectable({ providedIn: 'root' })
export class ClienteService {
  private http = inject(HttpClient);
  private url = `${API_URL}/clientes`;

  listar(soloActivos = false): Observable<Cliente[]> {
    const params = new HttpParams().set('activos', soloActivos);
    return this.http.get<Cliente[]>(this.url, { params });
  }

  crear(req: ClienteRequest): Observable<Cliente> {
    return this.http.post<Cliente>(this.url, req);
  }

  actualizar(id: number, req: ClienteRequest): Observable<Cliente> {
    return this.http.put<Cliente>(`${this.url}/${id}`, req);
  }

  cambiarEstado(id: number, activo: boolean): Observable<Cliente> {
    const params = new HttpParams().set('activo', activo);
    return this.http.patch<Cliente>(`${this.url}/${id}/estado`, null, { params });
  }
}
