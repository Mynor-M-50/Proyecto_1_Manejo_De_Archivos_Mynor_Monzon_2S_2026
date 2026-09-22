import { CurrencyPipe, DatePipe } from '@angular/common';
import { Component, inject, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { mensajeError } from '../../core/error';
import { Cliente } from '../../core/models/catalogo';
import { Venta } from '../../core/models/venta';
import { ClienteService } from '../../core/services/cliente.service';
import { VentaService } from '../../core/services/venta.service';

@Component({
  selector: 'app-ventas',
  imports: [RouterLink, CurrencyPipe, DatePipe],
  templateUrl: './ventas.html',
  styleUrl: './ventas.css'
})
export class Ventas {
  private service = inject(VentaService);

  protected lista = signal<Venta[]>([]);
  protected clientes = signal<Cliente[]>([]);
  protected seleccionada = signal<Venta | null>(null);
  protected desde = signal('');
  protected hasta = signal('');
  protected idCliente = signal<number | null>(null);
  protected error = signal('');

  constructor() {
    // Si se llega desde la pantalla de clientes, se filtra por ese cliente
    const param = inject(ActivatedRoute).snapshot.queryParamMap.get('idCliente');
    if (param) {
      this.idCliente.set(Number(param));
    }
    inject(ClienteService).listar().subscribe((c) => this.clientes.set(c));
    this.buscar();
  }

  protected buscar(): void {
    this.seleccionada.set(null);
    this.service.listar(this.desde(), this.hasta(), this.idCliente()).subscribe({
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
    this.idCliente.set(null);
    this.buscar();
  }

  protected valor(evento: Event): string {
    return (evento.target as HTMLInputElement).value;
  }

  protected cambiarCliente(evento: Event): void {
    const v = (evento.target as HTMLSelectElement).value;
    this.idCliente.set(v ? Number(v) : null);
  }

  protected seleccionar(v: Venta): void {
    this.seleccionada.set(this.seleccionada()?.idVenta === v.idVenta ? null : v);
  }

  protected factura(v: Venta, evento?: Event): void {
    evento?.stopPropagation();
    this.service.abrirFactura(v.idVenta);
  }
}
