import { CurrencyPipe, DatePipe } from '@angular/common';
import { Component, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { mensajeError } from '../../core/error';
import { Compra } from '../../core/models/compra';
import { CompraService } from '../../core/services/compra.service';

@Component({
  selector: 'app-compras',
  imports: [RouterLink, CurrencyPipe, DatePipe],
  templateUrl: './compras.html',
  styleUrl: './compras.css'
})
export class Compras {
  private service = inject(CompraService);

  protected lista = signal<Compra[]>([]);
  protected seleccionada = signal<Compra | null>(null);
  protected desde = signal('');
  protected hasta = signal('');
  protected error = signal('');

  constructor() {
    this.buscar();
  }

  protected buscar(): void {
    this.seleccionada.set(null);
    this.service.listar(this.desde(), this.hasta()).subscribe({
      next: (l) => {
        this.lista.set(l);
        this.error.set('');
      },
      error: (e) => this.error.set(mensajeError(e))
    });
  }

  protected limpiar(): void {
    this.desde.set('');
    this.hasta.set('');
    this.buscar();
  }

  protected valor(evento: Event): string {
    return (evento.target as HTMLInputElement).value;
  }

  protected seleccionar(c: Compra): void {
    this.seleccionada.set(this.seleccionada()?.idCompra === c.idCompra ? null : c);
  }
}
