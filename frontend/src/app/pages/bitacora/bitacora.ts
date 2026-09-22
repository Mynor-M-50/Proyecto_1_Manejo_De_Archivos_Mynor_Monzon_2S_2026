import { DatePipe } from '@angular/common';
import { Component, computed, inject, signal } from '@angular/core';
import { mensajeError } from '../../core/error';
import { FilaReporte } from '../../core/models/reporte';
import { ReporteService } from '../../core/services/reporte.service';

interface Registro {
  idLog: number;
  fechaHora: string;
  modulo: string;
  accion: string;
  descripcion: string;
  usuario: string;
}

@Component({
  selector: 'app-bitacora',
  imports: [DatePipe],
  templateUrl: './bitacora.html',
  styleUrl: './bitacora.css'
})
export class Bitacora {
  private service = inject(ReporteService);

  protected registros = signal<Registro[]>([]);
  protected desde = signal('');
  protected hasta = signal('');
  protected modulo = signal('');
  protected error = signal('');

  protected modulos = computed(() => [...new Set(this.registros().map((r) => r.modulo))].sort());

  protected filtrados = computed(() => {
    const m = this.modulo();
    return m ? this.registros().filter((r) => r.modulo === m) : this.registros();
  });

  constructor() {
    this.buscar();
  }

  protected buscar(): void {
    this.service.consultar('logs', { desde: this.desde(), hasta: this.hasta() }).subscribe({
      next: (filas: FilaReporte[]) => {
        this.registros.set(filas as unknown as Registro[]);
        this.error.set('');
      },
      error: (e) => this.error.set(mensajeError(e))
    });
  }

  protected valor(evento: Event): string {
    return (evento.target as HTMLInputElement).value;
  }
}
