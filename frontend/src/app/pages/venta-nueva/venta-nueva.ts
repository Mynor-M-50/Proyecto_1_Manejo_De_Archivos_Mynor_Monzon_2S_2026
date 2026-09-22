import { CurrencyPipe } from '@angular/common';
import { Component, computed, inject, signal } from '@angular/core';
import { FormArray, FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { mensajeError } from '../../core/error';
import { Cliente, Producto } from '../../core/models/catalogo';
import { Venta } from '../../core/models/venta';
import { ClienteService } from '../../core/services/cliente.service';
import { ProductoService } from '../../core/services/producto.service';
import { VentaService } from '../../core/services/venta.service';

const TASA_IVA = 0.12;

@Component({
  selector: 'app-venta-nueva',
  imports: [ReactiveFormsModule, RouterLink, CurrencyPipe],
  templateUrl: './venta-nueva.html',
  styleUrl: './venta-nueva.css'
})
export class VentaNueva {
  private fb = inject(FormBuilder);
  private ventaService = inject(VentaService);
  private productoService = inject(ProductoService);

  protected clientes = signal<Cliente[]>([]);
  protected productos = signal<Producto[]>([]);
  protected guardando = signal(false);
  protected error = signal('');
  protected registrada = signal<Venta | null>(null);

  // Solo se ofrecen productos activos que tienen existencia
  protected disponibles = computed(() => this.productos().filter((p) => p.existenciaActual > 0));

  protected form = this.fb.nonNullable.group({
    idCliente: [0, [Validators.required, Validators.min(1)]],
    lineas: this.fb.array([this.nuevaLinea()])
  });

  constructor() {
    inject(ClienteService).listar(true).subscribe((c) => this.clientes.set(c));
    this.cargarProductos();
  }

  protected get lineas(): FormArray {
    return this.form.controls.lineas;
  }

  private nuevaLinea() {
    return this.fb.nonNullable.group({
      idProducto: [0, [Validators.required, Validators.min(1)]],
      cantidad: [1, [Validators.required, Validators.min(1)]]
    });
  }

  private cargarProductos(): void {
    this.productoService.listar(true).subscribe((p) => this.productos.set(p));
  }

  protected agregarLinea(): void {
    this.lineas.push(this.nuevaLinea());
  }

  protected quitarLinea(i: number): void {
    if (this.lineas.length > 1) {
      this.lineas.removeAt(i);
    }
  }

  protected producto(i: number): Producto | undefined {
    const id = this.lineas.at(i).value.idProducto;
    return this.productos().find((p) => p.idProducto === id);
  }

  // Aviso visual; la validacion definitiva la hace el backend dentro de la transaccion
  protected excede(i: number): boolean {
    const p = this.producto(i);
    return !!p && Number(this.lineas.at(i).value.cantidad) > p.existenciaActual;
  }

  protected subtotalLinea(i: number): number {
    const p = this.producto(i);
    return p ? p.precioVenta * (Number(this.lineas.at(i).value.cantidad) || 0) : 0;
  }

  protected subtotal(): number {
    return this.lineas.controls.reduce((suma, _, i) => suma + this.subtotalLinea(i), 0);
  }

  protected iva(): number {
    return Math.round(this.subtotal() * TASA_IVA * 100) / 100;
  }

  protected total(): number {
    return this.subtotal() + this.iva();
  }

  protected guardar(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      this.error.set('Seleccione el cliente y complete cada producto con su cantidad');
      return;
    }
    const v = this.form.getRawValue();
    const ids = v.lineas.map((l) => l.idProducto);
    if (new Set(ids).size !== ids.length) {
      this.error.set('Un producto no puede aparecer dos veces en la misma venta');
      return;
    }
    if (this.lineas.controls.some((_, i) => this.excede(i))) {
      this.error.set('Hay productos con cantidad mayor a la existencia disponible');
      return;
    }

    this.guardando.set(true);
    this.error.set('');
    this.ventaService.registrar({ idCliente: v.idCliente, detalles: v.lineas }).subscribe({
      next: (venta) => {
        this.registrada.set(venta);
        this.guardando.set(false);
      },
      error: (e) => {
        this.error.set(mensajeError(e));
        this.guardando.set(false);
        // Las existencias pudieron cambiar mientras se llenaba el formulario
        this.cargarProductos();
      }
    });
  }

  protected verFactura(): void {
    const venta = this.registrada();
    if (venta) {
      this.ventaService.abrirFactura(venta.idVenta);
    }
  }

  protected otraVenta(): void {
    this.registrada.set(null);
    this.form.reset();
    this.lineas.clear();
    this.lineas.push(this.nuevaLinea());
    this.cargarProductos();
  }
}
