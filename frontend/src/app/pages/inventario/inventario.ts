import { CurrencyPipe, DatePipe } from '@angular/common';
import { Component, inject, signal } from '@angular/core';
import { forkJoin } from 'rxjs';
import { mensajeError } from '../../core/error';
import { Producto } from '../../core/models/catalogo';
import { Lote, Movimiento } from '../../core/models/inventario';
import { InventarioService } from '../../core/services/inventario.service';
import { ProductoService } from '../../core/services/producto.service';

@Component({
  selector: 'app-inventario',
  imports: [CurrencyPipe, DatePipe],
  templateUrl: './inventario.html',
  styleUrl: './inventario.css'
})
export class Inventario {
  private service = inject(InventarioService);

  protected productos = signal<Producto[]>([]);
  protected producto = signal<Producto | null>(null);
  protected lotes = signal<Lote[]>([]);
  protected movimientos = signal<Movimiento[]>([]);
  protected error = signal('');

  constructor() {
    inject(ProductoService).listar().subscribe((p) => this.productos.set(p));
  }

  protected seleccionar(evento: Event): void {
    const id = Number((evento.target as HTMLSelectElement).value);
    const p = this.productos().find((x) => x.idProducto === id) ?? null;
    this.producto.set(p);
    if (!p) {
      return;
    }
    // Lotes y movimientos se piden en paralelo
    forkJoin({
      lotes: this.service.lotes(id),
      movimientos: this.service.movimientos(id)
    }).subscribe({
      next: (r) => {
        this.lotes.set(r.lotes);
        // Del mas reciente al mas antiguo para leer el historial facilmente
        this.movimientos.set([...r.movimientos].reverse());
        this.error.set('');
      },
      error: (e) => this.error.set(mensajeError(e))
    });
  }
}
