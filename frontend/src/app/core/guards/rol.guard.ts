import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

// Lee las areas permitidas desde data.areas de la ruta.
// Es solo una comodidad de navegacion: la seguridad real la aplica el backend.
export const rolGuard: CanActivateFn = (route) => {
  const auth = inject(AuthService);
  const areas: string[] = route.data['areas'] ?? [];
  return auth.tieneAcceso(areas) ? true : inject(Router).createUrlTree(['/inicio']);
};
