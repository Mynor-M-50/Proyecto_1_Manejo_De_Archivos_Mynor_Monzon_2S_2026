import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { mensajeError } from '../../core/error';
import { Proveedor } from '../../core/models/catalogo';
import { ProveedorService } from '../../core/services/proveedor.service';

@Component({
  selector: 'app-proveedores',
  imports: [ReactiveFormsModule],
  templateUrl: './proveedores.html',
  styleUrl: './proveedores.css'
})
export class Proveedores {
  private fb = inject(FormBuilder);
  private service = inject(ProveedorService);

  protected lista = signal<Proveedor[]>([]);
  protected editando = signal<Proveedor | null>(null);
  protected mostrarForm = signal(false);
  protected error = signal('');
  protected exito = signal('');

  protected form = this.fb.nonNullable.group({
    nit: ['', [Validators.required, Validators.maxLength(20)]],
    nombre: ['', [Validators.required, Validators.maxLength(120)]],
    contacto: ['', Validators.maxLength(120)],
    telefono: ['', Validators.maxLength(20)],
    email: ['', [Validators.email, Validators.maxLength(120)]],
    direccion: ['', Validators.maxLength(200)]
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

  protected editar(p: Proveedor): void {
    this.editando.set(p);
    this.form.setValue({
      nit: p.nit,
      nombre: p.nombre,
      contacto: p.contacto ?? '',
      telefono: p.telefono ?? '',
      email: p.email ?? '',
      direccion: p.direccion ?? ''
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
    const req = {
      nit: v.nit,
      nombre: v.nombre,
      contacto: v.contacto || null,
      telefono: v.telefono || null,
      email: v.email || null,
      direccion: v.direccion || null
    };
    const actual = this.editando();
    const operacion = actual ? this.service.actualizar(actual.idProveedor, req) : this.service.crear(req);

    operacion.subscribe({
      next: () => {
        this.exito.set(actual ? 'Proveedor actualizado' : 'Proveedor creado');
        this.error.set('');
        this.mostrarForm.set(false);
        this.cargar();
      },
      error: (e) => this.error.set(mensajeError(e))
    });
  }

  protected cambiarEstado(p: Proveedor): void {
    this.service.cambiarEstado(p.idProveedor, !p.estado).subscribe({
      next: () => {
        this.exito.set(`Proveedor ${p.estado ? 'desactivado' : 'activado'}`);
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
