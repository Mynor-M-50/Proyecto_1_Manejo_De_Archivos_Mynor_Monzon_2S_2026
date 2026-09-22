import { DatePipe } from '@angular/common';
import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { mensajeError } from '../../core/error';
import { Area, Usuario } from '../../core/models/catalogo';
import { UsuarioService } from '../../core/services/usuario.service';

@Component({
  selector: 'app-usuarios',
  imports: [ReactiveFormsModule, DatePipe],
  templateUrl: './usuarios.html',
  styleUrl: './usuarios.css'
})
export class Usuarios {
  private fb = inject(FormBuilder);
  private service = inject(UsuarioService);

  protected lista = signal<Usuario[]>([]);
  protected areas = signal<Area[]>([]);
  protected mostrarForm = signal(false);
  protected error = signal('');
  protected exito = signal('');

  protected form = this.fb.nonNullable.group({
    nombreCompleto: ['', [Validators.required, Validators.maxLength(120)]],
    username: ['', [Validators.required, Validators.minLength(4), Validators.maxLength(40)]],
    password: ['', [Validators.required, Validators.minLength(6), Validators.maxLength(60)]],
    idArea: [0, [Validators.required, Validators.min(1)]]
  });

  constructor() {
    this.cargar();
    this.service.areas().subscribe((a) => this.areas.set(a));
  }

  protected cargar(): void {
    this.service.listar().subscribe({
      next: (l) => this.lista.set(l),
      error: (e) => this.error.set(mensajeError(e))
    });
  }

  protected nuevo(): void {
    this.form.reset();
    this.mostrarForm.set(true);
    this.error.set('');
    this.exito.set('');
  }

  protected cancelar(): void {
    this.mostrarForm.set(false);
  }

  protected guardar(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.service.crear(this.form.getRawValue()).subscribe({
      next: (u) => {
        this.exito.set(`Usuario ${u.username} creado`);
        this.error.set('');
        this.mostrarForm.set(false);
        this.cargar();
      },
      error: (e) => this.error.set(mensajeError(e))
    });
  }

  protected cambiarEstado(u: Usuario): void {
    this.service.cambiarEstado(u.idUsuario, !u.estado).subscribe({
      next: () => {
        this.exito.set(`Usuario ${u.estado ? 'desactivado' : 'activado'}`);
        this.cargar();
      },
      error: (e) => this.error.set(mensajeError(e))
    });
  }
}
