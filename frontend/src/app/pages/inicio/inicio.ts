import { Component, computed, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { AREAS } from '../../core/config';
import { mensajeError } from '../../core/error';
import { Producto } from '../../core/models/catalogo';
import { AuthService } from '../../core/services/auth.service';
import { ProductoService } from '../../core/services/producto.service';

@Component({
  selector: 'app-inicio',
  imports: [RouterLink],
  templateUrl: './inicio.html',
  styleUrl: './inicio.css'
})
export class Inicio {
  protected auth = inject(AuthService);
  private productoService = inject(ProductoService);

  protected stockBajo = signal<Producto[]>([]);
  protected error = signal('');

  protected agotados = computed(() => this.stockBajo().filter((p) => p.existenciaActual === 0).length);
  protected puedeComprar = computed(() => this.auth.tieneAcceso([AREAS.COMPRAS]));
  protected puedeVender = computed(() => this.auth.tieneAcceso([AREAS.VENTAS]));
  protected puedeInventario = computed(() => this.auth.tieneAcceso([AREAS.INVENTARIO]));

  constructor() {
    // Alertas de existencias que requieren atencion
    this.productoService.stockBajo().subscribe({
      next: (lista) => this.stockBajo.set(lista),
      error: (e) => this.error.set(mensajeError(e))
    });
  }
}
