import { CurrencyPipe } from '@angular/common';
import { Component, computed, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { mensajeError } from '../../core/error';
import { Categoria, Producto } from '../../core/models/catalogo';
import { CategoriaService } from '../../core/services/categoria.service';
import { ProductoService } from '../../core/services/producto.service';

@Component({
  selector: 'app-productos',
  imports: [ReactiveFormsModule, CurrencyPipe],
  templateUrl: './productos.html',
  styleUrl: './productos.css'
})
export class Productos {
  private fb = inject(FormBuilder);
  private service = inject(ProductoService);
  private categoriaService = inject(CategoriaService);

  protected lista = signal<Producto[]>([]);
  protected categorias = signal<Categoria[]>([]);
  protected editando = signal<Producto | null>(null);
  protected mostrarForm = signal(false);
  protected filtro = signal('');
  protected error = signal('');
  protected exito = signal('');

  // Busqueda por codigo, nombre o categoria sin volver a llamar al backend
  protected filtrados = computed(() => {
    const texto = this.filtro().trim().toLowerCase();
    if (!texto) {
      return this.lista();
    }
    return this.lista().filter((p) =>
      `${p.codigo} ${p.nombre} ${p.categoria}`.toLowerCase().includes(texto)
    );
  });

  protected form = this.fb.nonNullable.group({
    codigo: ['', [Validators.required, Validators.maxLength(30)]],
    nombre: ['', [Validators.required, Validators.maxLength(120)]],
    descripcion: ['', Validators.maxLength(250)],
    unidadMedida: ['UNIDAD', Validators.maxLength(20)],
    precioVenta: [0, [Validators.required, Validators.min(0.01)]],
    stockMinimo: [0, [Validators.required, Validators.min(0)]],
    idCategoria: [0, [Validators.required, Validators.min(1)]]
  });

  constructor() {
    this.cargar();
    this.categoriaService.listar().subscribe((c) => this.categorias.set(c));
  }

  protected cargar(): void {
    this.service.listar().subscribe({
      next: (l) => this.lista.set(l),
      error: (e) => this.error.set(mensajeError(e))
    });
  }

  protected buscar(evento: Event): void {
    this.filtro.set((evento.target as HTMLInputElement).value);
  }

  protected nuevo(): void {
    this.editando.set(null);
    this.form.reset();
    this.mostrarForm.set(true);
    this.limpiarMensajes();
  }

  protected editar(p: Producto): void {
    this.editando.set(p);
    this.form.setValue({
      codigo: p.codigo,
      nombre: p.nombre,
      descripcion: p.descripcion ?? '',
      unidadMedida: p.unidadMedida,
      precioVenta: p.precioVenta,
      stockMinimo: p.stockMinimo,
      idCategoria: p.idCategoria
    });
    this.mostrarForm.set(true);
    this.limpiarMensajes();
  }

  protected cancelar(): void {
    this.mostrarForm.set(false);
  }

  protected guardar(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    const v = this.form.getRawValue();
    const req = { ...v, descripcion: v.descripcion || null };
    const actual = this.editando();
    const operacion = actual ? this.service.actualizar(actual.idProducto, req) : this.service.crear(req);

    operacion.subscribe({
      next: () => {
        this.exito.set(actual ? 'Producto actualizado' : 'Producto creado con existencia 0');
        this.error.set('');
        this.mostrarForm.set(false);
        this.cargar();
      },
      error: (e) => this.error.set(mensajeError(e))
    });
  }

  // Borrado logico: el producto deja de venderse pero su historial se conserva
  protected cambiarEstado(p: Producto): void {
    this.service.cambiarEstado(p.idProducto, !p.estado).subscribe({
      next: () => {
        this.exito.set(`Producto ${p.estado ? 'desactivado' : 'activado'}`);
        this.cargar();
      },
      error: (e) => this.error.set(mensajeError(e))
    });
  }

  private limpiarMensajes(): void {
    this.error.set('');
    this.exito.set('');
  }
}
