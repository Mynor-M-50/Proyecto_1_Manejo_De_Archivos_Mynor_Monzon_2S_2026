import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { mensajeError } from '../../core/error';
import { Cliente } from '../../core/models/catalogo';
import { ClienteService } from '../../core/services/cliente.service';

@Component({
  selector: 'app-clientes',
  imports: [ReactiveFormsModule],
  templateUrl: './clientes.html',
  styleUrl: './clientes.css'
})
export class Clientes {
  private fb = inject(FormBuilder);
  private service = inject(ClienteService);
  private router = inject(Router);

  protected lista = signal<Cliente[]>([]);
  protected editando = signal<Cliente | null>(null);
  protected mostrarForm = signal(false);
  protected error = signal('');
  protected exito = signal('');

  protected form = this.fb.nonNullable.group({
    nit: ['', [Validators.required, Validators.maxLength(20)]],
    nombre: ['', [Validators.required, Validators.maxLength(120)]],
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

  protected editar(c: Cliente): void {
    this.editando.set(c);
    this.form.setValue({
      nit: c.nit,
      nombre: c.nombre,
      telefono: c.telefono ?? '',
      email: c.email ?? '',
      direccion: c.direccion ?? ''
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
      telefono: v.telefono || null,
      email: v.email || null,
      direccion: v.direccion || null
    };
    const actual = this.editando();
    const operacion = actual ? this.service.actualizar(actual.idCliente, req) : this.service.crear(req);

    operacion.subscribe({
      next: () => {
        this.exito.set(actual ? 'Cliente actualizado' : 'Cliente creado');
        this.error.set('');
        this.mostrarForm.set(false);
        this.cargar();
      },
      error: (e) => this.error.set(mensajeError(e))
    });
  }

  protected cambiarEstado(c: Cliente): void {
    this.service.cambiarEstado(c.idCliente, !c.estado).subscribe({
      next: () => {
        this.exito.set(`Cliente ${c.estado ? 'desactivado' : 'activado'}`);
        this.cargar();
      },
      error: (e) => this.error.set(mensajeError(e))
    });
  }

  // Historial de operaciones del cliente
  protected verVentas(c: Cliente): void {
    this.router.navigate(['/ventas'], { queryParams: { idCliente: c.idCliente } });
  }

  private limpiarMensajes(): void {
    this.error.set('');
    this.exito.set('');
  }
}
