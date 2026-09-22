import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { API_URL } from '../config';
import { Compra, CompraRequest } from '../models/compra';

@Injectable({ providedIn: 'root' })
export class CompraService {
  private http = inject(HttpClient);
  private url = `${API_URL}/compras`;

  listar(desde?: string, hasta?: string): Observable<Compra[]> {
    let params = new HttpParams();
    if (desde) params = params.set('desde', desde);
    if (hasta) params = params.set('hasta', hasta);
    return this.http.get<Compra[]>(this.url, { params });
  }

  registrar(req: CompraRequest): Observable<Compra> {
    return this.http.post<Compra>(this.url, req);
  }
}
