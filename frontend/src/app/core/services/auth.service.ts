import { HttpClient } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';
import { API_URL, AREAS } from '../config';
import { LoginRequest, Sesion } from '../models/auth';

const CLAVE_SESION = 'minierp_sesion';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private http = inject(HttpClient);
  private router = inject(Router);

  // La sesion se guarda en localStorage para sobrevivir a una recarga de pagina
  private sesionActual = signal<Sesion | null>(this.leerSesion());

  readonly sesion = this.sesionActual.asReadonly();
  readonly autenticado = computed(() => this.sesionActual() !== null);
  readonly area = computed(() => this.sesionActual()?.area ?? '');

  login(req: LoginRequest): Observable<Sesion> {
    return this.http.post<Sesion>(`${API_URL}/auth/login`, req).pipe(
      tap((sesion) => {
        localStorage.setItem(CLAVE_SESION, JSON.stringify(sesion));
        this.sesionActual.set(sesion);
      })
    );
  }

  logout(): void {
    localStorage.removeItem(CLAVE_SESION);
    this.sesionActual.set(null);
    this.router.navigate(['/login']);
  }

  token(): string | null {
    return this.sesionActual()?.token ?? null;
  }

  // Administracion tiene acceso a todo; las demas areas solo a lo suyo
  tieneAcceso(areas: string[]): boolean {
    const area = this.area();
    return area === AREAS.ADMIN || areas.includes(area);
  }

  private leerSesion(): Sesion | null {
    const guardada = localStorage.getItem(CLAVE_SESION);
    if (!guardada) {
      return null;
    }
    try {
      return JSON.parse(guardada) as Sesion;
    } catch {
      localStorage.removeItem(CLAVE_SESION);
      return null;
    }
  }
}
