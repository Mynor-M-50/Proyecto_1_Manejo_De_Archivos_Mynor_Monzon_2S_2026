import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { API_URL } from '../config';
import { Area, Usuario, UsuarioRequest } from '../models/catalogo';

@Injectable({ providedIn: 'root' })
export class UsuarioService {
  private http = inject(HttpClient);
  private url = `${API_URL}/usuarios`;

  listar(): Observable<Usuario[]> {
    return this.http.get<Usuario[]>(this.url);
  }

  areas(): Observable<Area[]> {
    return this.http.get<Area[]>(`${this.url}/areas`);
  }

  crear(req: UsuarioRequest): Observable<Usuario> {
    return this.http.post<Usuario>(this.url, req);
  }

  cambiarEstado(id: number, activo: boolean): Observable<Usuario> {
    const params = new HttpParams().set('activo', activo);
    return this.http.patch<Usuario>(`${this.url}/${id}/estado`, null, { params });
  }
}
