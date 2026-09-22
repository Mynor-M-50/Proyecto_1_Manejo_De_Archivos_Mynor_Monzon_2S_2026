import { Routes } from '@angular/router';
import { AREAS } from './core/config';
import { authGuard } from './core/guards/auth.guard';
import { rolGuard } from './core/guards/rol.guard';
import { Layout } from './layout/layout';

const { ADMIN, COMPRAS, INVENTARIO, VENTAS } = AREAS;

// Cada pagina se carga bajo demanda (loadComponent) y declara que areas pueden entrar
export const routes: Routes = [
  {
    path: 'login',
    loadComponent: () => import('./pages/login/login').then((m) => m.Login)
  },
  {
    path: '',
    component: Layout,
    canActivate: [authGuard],
    children: [
      { path: '', pathMatch: 'full', redirectTo: 'inicio' },
      {
        path: 'inicio',
        loadComponent: () => import('./pages/inicio/inicio').then((m) => m.Inicio)
      },

      // Inventario
      {
        path: 'categorias',
        canActivate: [rolGuard],
        data: { areas: [INVENTARIO] },
        loadComponent: () => import('./pages/categorias/categorias').then((m) => m.Categorias)
      },
      {
        path: 'productos',
        canActivate: [rolGuard],
        data: { areas: [INVENTARIO] },
        loadComponent: () => import('./pages/productos/productos').then((m) => m.Productos)
      },
      {
        path: 'inventario',
        canActivate: [rolGuard],
        data: { areas: [INVENTARIO] },
        loadComponent: () => import('./pages/inventario/inventario').then((m) => m.Inventario)
      },

      // Compras
      {
        path: 'proveedores',
        canActivate: [rolGuard],
        data: { areas: [COMPRAS] },
        loadComponent: () => import('./pages/proveedores/proveedores').then((m) => m.Proveedores)
      },
      {
        path: 'compras',
        canActivate: [rolGuard],
        data: { areas: [COMPRAS] },
        loadComponent: () => import('./pages/compras/compras').then((m) => m.Compras)
      },
      {
        path: 'compras/nueva',
        canActivate: [rolGuard],
        data: { areas: [COMPRAS] },
        loadComponent: () => import('./pages/compra-nueva/compra-nueva').then((m) => m.CompraNueva)
      },

      // Ventas
      {
        path: 'clientes',
        canActivate: [rolGuard],
        data: { areas: [VENTAS] },
        loadComponent: () => import('./pages/clientes/clientes').then((m) => m.Clientes)
      },
      {
        path: 'ventas',
        canActivate: [rolGuard],
        data: { areas: [VENTAS] },
        loadComponent: () => import('./pages/ventas/ventas').then((m) => m.Ventas)
      },
      {
        path: 'ventas/nueva',
        canActivate: [rolGuard],
        data: { areas: [VENTAS] },
        loadComponent: () => import('./pages/venta-nueva/venta-nueva').then((m) => m.VentaNueva)
      },

      // Todas las areas (cada una ve solo sus pestanas)
      {
        path: 'reportes',
        loadComponent: () => import('./pages/reportes/reportes').then((m) => m.Reportes)
      },

      // Solo administracion
      {
        path: 'usuarios',
        canActivate: [rolGuard],
        data: { areas: [ADMIN] },
        loadComponent: () => import('./pages/usuarios/usuarios').then((m) => m.Usuarios)
      },
      {
        path: 'bitacora',
        canActivate: [rolGuard],
        data: { areas: [ADMIN] },
        loadComponent: () => import('./pages/bitacora/bitacora').then((m) => m.Bitacora)
      }
    ]
  },
  { path: '**', redirectTo: 'inicio' }
];
