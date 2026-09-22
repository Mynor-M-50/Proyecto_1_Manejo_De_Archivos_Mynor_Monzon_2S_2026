import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { API_URL } from '../config';
import { Venta, VentaRequest } from '../models/venta';

@Injectable({ providedIn: 'root' })
export class VentaService {
  private http = inject(HttpClient);
  private url = `${API_URL}/ventas`;

  listar(desde?: string, hasta?: string, idCliente?: number | null): Observable<Venta[]> {
    let params = new HttpParams();
    if (desde) params = params.set('desde', desde);
    if (hasta) params = params.set('hasta', hasta);
    if (idCliente) params = params.set('idCliente', idCliente);
    return this.http.get<Venta[]>(this.url, { params });
  }

  registrar(req: VentaRequest): Observable<Venta> {
    return this.http.post<Venta>(this.url, req);
  }

  // Se descarga como blob porque la peticion necesita el token;
  // un enlace normal no enviaria el header Authorization.
  abrirFactura(idVenta: number): void {
    this.http.get(`${this.url}/${idVenta}/factura`, { responseType: 'blob' }).subscribe((pdf) => {
      const url = URL.createObjectURL(pdf);
      window.open(url, '_blank');
      setTimeout(() => URL.revokeObjectURL(url), 60000);
    });
  }
}
