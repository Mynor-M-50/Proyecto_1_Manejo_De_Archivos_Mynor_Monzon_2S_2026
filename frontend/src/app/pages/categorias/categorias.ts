import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { mensajeError } from '../../core/error';
import { Categoria } from '../../core/models/catalogo';
import { CategoriaService } from '../../core/services/categoria.service';

@Component({
  selector: 'app-categorias',
  imports: [ReactiveFormsModule],
  templateUrl: './categorias.html',
  styleUrl: './categorias.css'
})
export class Categorias {
  private fb = inject(FormBuilder);
  private service = inject(CategoriaService);

  protected lista = signal<Categoria[]>([]);
  protected editando = signal<Categoria | null>(null);
  protected mostrarForm = signal(false);
  protected error = signal('');
  protected exito = signal('');

  protected form = this.fb.nonNullable.group({
    nombre: ['', [Validators.required, Validators.maxLength(80)]],
    descripcion: ['', Validators.maxLength(200)]
  });

  constructor() {
    this.cargar();
  }

  protected cargar(): void {
    this.service.listar().subscribe({
      next: (l) => this.lista.set(l),
      error: (e) => this.error.set(mensajeError(e))
    });
  }

  protected nuevo(): void {
    this.editando.set(null);
    this.form.reset();
    this.mostrarForm.set(true);
    this.limpiarMensajes();
  }

  protected editar(c: Categoria): void {
    this.editando.set(c);
    this.form.setValue({ nombre: c.nombre, descripcion: c.descripcion ?? '' });
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
    const req = { nombre: v.nombre, descripcion: v.descripcion || null };
    const actual = this.editando();
    const operacion = actual ? this.service.actualizar(actual.idCategoria, req) : this.service.crear(req);

    operacion.subscribe({
      next: () => {
        this.exito.set(actual ? 'Categoria actualizada' : 'Categoria creada');
        this.error.set('');
        this.mostrarForm.set(false);
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
