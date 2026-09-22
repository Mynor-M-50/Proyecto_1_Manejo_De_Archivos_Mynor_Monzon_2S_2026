import { Component, computed, inject } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { AREAS } from '../core/config';
import { AuthService } from '../core/services/auth.service';

interface OpcionMenu {
  ruta: string;
  texto: string;
  areas: string[];
}

const { COMPRAS, INVENTARIO, VENTAS } = AREAS;

// El menu se filtra segun el area del usuario.
// Una lista vacia de areas significa "visible para todos".
const MENU: OpcionMenu[] = [
  { ruta: '/inicio', texto: 'Inicio', areas: [] },
  { ruta: '/categorias', texto: 'Categorias', areas: [INVENTARIO] },
  { ruta: '/productos', texto: 'Productos', areas: [INVENTARIO] },
  { ruta: '/inventario', texto: 'Inventario', areas: [INVENTARIO] },
  { ruta: '/proveedores', texto: 'Proveedores', areas: [COMPRAS] },
  { ruta: '/compras', texto: 'Compras', areas: [COMPRAS] },
  { ruta: '/clientes', texto: 'Clientes', areas: [VENTAS] },
  { ruta: '/ventas', texto: 'Ventas', areas: [VENTAS] },
  { ruta: '/reportes', texto: 'Reportes', areas: [] },
  { ruta: '/usuarios', texto: 'Usuarios', areas: [AREAS.ADMIN] },
  { ruta: '/bitacora', texto: 'Bitacora', areas: [AREAS.ADMIN] }
];

@Component({
  selector: 'app-layout',
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  templateUrl: './layout.html',
  styleUrl: './layout.css'
})
export class Layout {
  protected auth = inject(AuthService);

  protected opciones = computed(() =>
    MENU.filter((o) => o.areas.length === 0 || this.auth.tieneAcceso(o.areas))
  );

  protected salir(): void {
    this.auth.logout();
  }
}
