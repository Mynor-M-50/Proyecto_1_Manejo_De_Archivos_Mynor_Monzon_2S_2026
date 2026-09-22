import { CurrencyPipe, DatePipe, DecimalPipe } from '@angular/common';
import { Component, computed, inject, signal } from '@angular/core';
import { AREAS } from '../../core/config';
import { mensajeError } from '../../core/error';
import { Producto } from '../../core/models/catalogo';
import { Columna, FilaReporte } from '../../core/models/reporte';
import { AuthService } from '../../core/services/auth.service';
import { ProductoService } from '../../core/services/producto.service';
import { ReporteService } from '../../core/services/reporte.service';

// Describe un reporte: a que ruta llama, que filtros usa y que columnas muestra
interface DefReporte {
  id: string;
  titulo: string;
  ruta: string;
  limite?: number;
  fechas?: boolean;
  agrupacion?: boolean;
  producto?: boolean;
  columnas: Columna[];
}

interface Grupo {
  id: string;
  titulo: string;
  area: string;
  reportes: DefReporte[];
}

const COLS_PRODUCTO_VENDIDO: Columna[] = [
  { campo: 'codigo', titulo: 'Codigo' },
  { campo: 'nombre', titulo: 'Producto' },
  { campo: 'cantidadVendida', titulo: 'Unidades vendidas', tipo: 'numero' },
  { campo: 'ingresos', titulo: 'Ingresos', tipo: 'moneda' }
];

const COLS_CONTEO = (titulo: string): Columna[] => [
  { campo: 'codigo', titulo: 'Codigo' },
  { campo: 'nombre', titulo: 'Producto' },
  { campo: 'cantidad', titulo: titulo, tipo: 'numero' }
];

const GRUPOS: Grupo[] = [
  {
    id: 'inventario',
    titulo: 'Productos e inventario',
    area: AREAS.INVENTARIO,
    reportes: [
      { id: 'vendidos', titulo: 'Top productos mas vendidos', ruta: 'inventario/mas-vendidos', limite: 10, fechas: true, columnas: COLS_PRODUCTO_VENDIDO },
      {
        id: 'existencia', titulo: 'Productos con menor existencia', ruta: 'inventario/menor-existencia', limite: 10,
        columnas: [
          { campo: 'codigo', titulo: 'Codigo' },
          { campo: 'nombre', titulo: 'Producto' },
          { campo: 'categoria', titulo: 'Categoria' },
          { campo: 'existenciaActual', titulo: 'Existencia', tipo: 'numero' },
          { campo: 'stockMinimo', titulo: 'Stock minimo', tipo: 'numero' }
        ]
      },
      { id: 'movimientos', titulo: 'Productos con mas movimientos', ruta: 'inventario/mas-movimientos', limite: 10, fechas: true, columnas: COLS_CONTEO('Movimientos') },
      {
        id: 'historial', titulo: 'Historial de movimientos de un producto', ruta: 'inventario/historial', producto: true,
        columnas: [
          { campo: 'fecha', titulo: 'Fecha', tipo: 'fecha' },
          { campo: 'tipo', titulo: 'Tipo' },
          { campo: 'origenTipo', titulo: 'Origen' },
          { campo: 'origenId', titulo: 'No. operacion' },
          { campo: 'cantidad', titulo: 'Cantidad', tipo: 'numero' },
          { campo: 'costoUnitario', titulo: 'Costo', tipo: 'moneda' },
          { campo: 'existenciaAnterior', titulo: 'Anterior', tipo: 'numero' },
          { campo: 'existenciaNueva', titulo: 'Nueva', tipo: 'numero' },
          { campo: 'usuario', titulo: 'Usuario' }
        ]
      }
    ]
  },
  {
    id: 'compras',
    titulo: 'Compras y proveedores',
    area: AREAS.COMPRAS,
    reportes: [
      {
        id: 'rango-compras', titulo: 'Compras en un rango de fechas', ruta: 'compras/rango', fechas: true,
        columnas: [
          { campo: 'numeroDoc', titulo: 'Documento' },
          { campo: 'fecha', titulo: 'Fecha', tipo: 'fecha' },
          { campo: 'proveedor', titulo: 'Proveedor' },
          { campo: 'usuario', titulo: 'Registro' },
          { campo: 'total', titulo: 'Total', tipo: 'moneda' }
        ]
      },
      {
        id: 'proveedores', titulo: 'Top proveedores por monto comprado', ruta: 'compras/top-proveedores', limite: 5, fechas: true,
        columnas: [
          { campo: 'nit', titulo: 'NIT' },
          { campo: 'nombre', titulo: 'Proveedor' },
          { campo: 'numeroCompras', titulo: 'Compras', tipo: 'numero' },
          { campo: 'montoTotal', titulo: 'Monto acumulado', tipo: 'moneda' }
        ]
      },
      { id: 'adquiridos', titulo: 'Productos adquiridos con mayor frecuencia', ruta: 'compras/mas-adquiridos', limite: 10, fechas: true, columnas: COLS_CONTEO('Veces comprado') }
    ]
  },
  {
    id: 'ventas',
    titulo: 'Ventas y clientes',
    area: AREAS.VENTAS,
    reportes: [
      {
        id: 'rango-ventas', titulo: 'Ventas en un rango de fechas', ruta: 'ventas/rango', fechas: true,
        columnas: [
          { campo: 'numeroFactura', titulo: 'Factura' },
          { campo: 'fecha', titulo: 'Fecha', tipo: 'fecha' },
          { campo: 'cliente', titulo: 'Cliente' },
          { campo: 'usuario', titulo: 'Vendedor' },
          { campo: 'subtotal', titulo: 'Subtotal', tipo: 'moneda' },
          { campo: 'iva', titulo: 'IVA', tipo: 'moneda' },
          { campo: 'total', titulo: 'Total', tipo: 'moneda' }
        ]
      },
      {
        id: 'clientes', titulo: 'Top clientes por monto comprado', ruta: 'ventas/top-clientes', limite: 10, fechas: true,
        columnas: [
          { campo: 'nit', titulo: 'NIT' },
          { campo: 'nombre', titulo: 'Cliente' },
          { campo: 'numeroCompras', titulo: 'Compras', tipo: 'numero' },
          { campo: 'montoTotal', titulo: 'Monto acumulado', tipo: 'moneda' }
        ]
      },
      { id: 'ingresos', titulo: 'Top productos por ingresos', ruta: 'ventas/top-ingresos', limite: 10, fechas: true, columnas: COLS_PRODUCTO_VENDIDO },
      {
        id: 'resumen', titulo: 'Resumen de ventas por periodo', ruta: 'ventas/resumen', fechas: true, agrupacion: true,
        columnas: [
          { campo: 'periodo', titulo: 'Periodo' },
          { campo: 'numeroVentas', titulo: 'Ventas', tipo: 'numero' },
          { campo: 'subtotal', titulo: 'Subtotal', tipo: 'moneda' },
          { campo: 'iva', titulo: 'IVA', tipo: 'moneda' },
          { campo: 'total', titulo: 'Total', tipo: 'moneda' }
        ]
      }
    ]
  }
];

@Component({
  selector: 'app-reportes',
  imports: [CurrencyPipe, DatePipe, DecimalPipe],
  templateUrl: './reportes.html',
  styleUrl: './reportes.css'
})
export class Reportes {
  private auth = inject(AuthService);
  private service = inject(ReporteService);

  // Cada area solo ve su pestana; administracion ve todas
  protected grupos = GRUPOS.filter((g) => this.auth.tieneAcceso([g.area]));
  protected grupo = signal<Grupo | undefined>(this.grupos[0]);
  protected reporte = signal<DefReporte | undefined>(this.grupos[0]?.reportes[0]);

  protected desde = signal('');
  protected hasta = signal('');
  protected limite = signal(10);
  protected agrupacion = signal('MES');
  protected idProducto = signal<number | null>(null);

  protected productos = signal<Producto[]>([]);
  protected filas = signal<FilaReporte[]>([]);
  protected consultado = signal(false);
  protected cargando = signal(false);
  protected error = signal('');

  protected columnas = computed(() => this.reporte()?.columnas ?? []);

  constructor() {
    inject(ProductoService).listar().subscribe((p) => this.productos.set(p));
    this.prepararFiltros();
  }

  protected elegirGrupo(g: Grupo): void {
    this.grupo.set(g);
    this.elegirReporte(g.reportes[0]);
  }

  protected elegirReporte(r: DefReporte): void {
    this.reporte.set(r);
    this.prepararFiltros();
  }

  protected cambiarReporte(evento: Event): void {
    const id = (evento.target as HTMLSelectElement).value;
    const r = this.grupo()?.reportes.find((x) => x.id === id);
    if (r) {
      this.elegirReporte(r);
    }
  }

  protected valor(evento: Event): string {
    return (evento.target as HTMLInputElement).value;
  }

  protected consultar(): void {
    const r = this.reporte();
    if (!r) {
      return;
    }
    let ruta = r.ruta;
    if (r.producto) {
      const id = this.idProducto();
      if (!id) {
        this.error.set('Seleccione un producto');
        return;
      }
      ruta = `${r.ruta}/${id}`;
    }

    this.cargando.set(true);
    this.error.set('');
    this.service
      .consultar(ruta, {
        limite: r.limite ? this.limite() : undefined,
        desde: r.fechas ? this.desde() : undefined,
        hasta: r.fechas ? this.hasta() : undefined,
        agrupacion: r.agrupacion ? this.agrupacion() : undefined
      })
      .subscribe({
        next: (filas) => {
          this.filas.set(filas);
          this.consultado.set(true);
          this.cargando.set(false);
        },
        error: (e) => {
          this.error.set(mensajeError(e));
          this.cargando.set(false);
        }
      });
  }

  private prepararFiltros(): void {
    const r = this.reporte();
    this.limite.set(r?.limite ?? 10);
    this.filas.set([]);
    this.consultado.set(false);
    this.error.set('');
  }
}
