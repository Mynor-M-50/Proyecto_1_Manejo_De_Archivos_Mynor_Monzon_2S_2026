export interface LoginRequest {
  username: string;
  password: string;
}

export interface Sesion {
  token: string;
  username: string;
  nombreCompleto: string;
  area: string;
}
