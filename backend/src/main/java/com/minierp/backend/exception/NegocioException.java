package com.minierp.backend.exception;

// Se lanza cuando una operacion viola una regla del negocio.
// Ejemplos: stock insuficiente, NIT duplicado, cantidad invalida.
public class NegocioException extends RuntimeException {

    public NegocioException(String mensaje) {
        super(mensaje);
    }
}
