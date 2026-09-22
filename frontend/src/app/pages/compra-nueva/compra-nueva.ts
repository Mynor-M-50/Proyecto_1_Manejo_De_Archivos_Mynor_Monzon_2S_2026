import { CurrencyPipe } from '@angular/common';
import { Component, inject, signal } from '@angular/core';
import { FormArray, FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { mensajeError } from '../../core/error';
import { Producto, Proveedor } from '../../core/models/catalogo';
import { CompraService } from '../../core/services/compra.service';
import { ProductoService } from '../../core/services/producto.service';
import { ProveedorService } from '../../core/services/proveedor.service';

@Component({
  selector: 'app-compra-nueva',
  imports: [ReactiveFormsModule, RouterLink, CurrencyPipe],
  templateUrl: './compra-nueva.html',
  styleUrl: './compra-nueva.css'
})
export class CompraNueva {
  private fb = inject(FormBuilder);
  private router = inject(Router);
  private compraService = inject(CompraService);

  protected proveedores = signal<Proveedor[]>([]);
  protected productos = signal<Producto[]>([]);
  protected guardando = signal(false);
  protected error = signal('');

  protected form = this.fb.nonNullable.group({
    idProveedor: [0, [Validators.required, Validators.min(1)]],
    numeroDoc: ['', [Validators.required, Validators.maxLength(30)]],
    lineas: this.fb.array([this.nuevaLinea()])
  });

  constructor() {
    // Solo proveedores y productos activos pueden usarse en una compra nueva
    inject(ProveedorService).listar(true).subscribe((p) => this.proveedores.set(p));
    inject(ProductoService).listar(true).subscribe((p) => this.productos.set(p));
  }

  protected get lineas(): FormArray {
    return this.form.controls.lineas;
  }

  private nuevaLinea() {
    return this.fb.nonNullable.group({
      idProducto: [0, [Validators.required, Validators.min(1)]],
      cantidad: [1, [Validators.required, Validators.min(1)]],
      costoUnitario: [0, [Validators.required, Validators.min(0.01)]]
    });
  }

  protected agregarLinea(): void {
    this.lineas.push(this.nuevaLinea());
  }

  protected quitarLinea(i: number): void {
    if (this.lineas.length > 1) {
      this.lineas.removeAt(i);
    }
  }

  protected subtotal(i: number): number {
    const v = this.lineas.at(i).value;
    return (Number(v.cantidad) || 0) * (Number(v.costoUnitario) || 0);
  }

  protected total(): number {
    return this.lineas.controls.reduce((suma, _, i) => suma + this.subtotal(i), 0);
  }

  protected guardar(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      this.error.set('Complete todos los campos: proveedor, documento y cada producto con cantidad y costo');
      return;
    }
    const v = this.form.getRawValue();
    const ids = v.lineas.map((l) => l.idProducto);
    if (new Set(ids).size !== ids.length) {
      this.error.set('Un producto no puede aparecer dos veces en la misma compra');
      return;
    }

    this.guardando.set(true);
    this.error.set('');
    this.compraService
      .registrar({ idProveedor: v.idProveedor, numeroDoc: v.numeroDoc, detalles: v.lineas })
      .subscribe({
        next: () => this.router.navigate(['/compras']),
        error: (e) => {
          this.error.set(mensajeError(e));
          this.guardando.set(false);
        }
      });
  }
}
