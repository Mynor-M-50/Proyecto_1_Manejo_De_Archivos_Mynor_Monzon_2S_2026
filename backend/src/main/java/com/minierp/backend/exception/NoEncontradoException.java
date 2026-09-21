package com.minierp.backend.exception;

// Se lanza cuando se busca un registro que no existe.
// Ejemplo: producto con id 999.
public class NoEncontradoException extends RuntimeException {

    public NoEncontradoException(String mensaje) {
        super(mensaje);
    }

    public NoEncontradoException(String entidad, Object id) {
        super(entidad + " con id " + id + " no existe");
    }
}
