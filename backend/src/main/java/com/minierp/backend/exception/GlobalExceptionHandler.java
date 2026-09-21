package com.minierp.backend.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

// Captura las excepciones de todos los controllers y las convierte en JSON
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // 404: el recurso no existe
    @ExceptionHandler(NoEncontradoException.class)
    public ResponseEntity<ErrorResponse> noEncontrado(NoEncontradoException ex) {
        return responder(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    // 409: se violo una regla del negocio
    @ExceptionHandler(NegocioException.class)
    public ResponseEntity<ErrorResponse> negocio(NegocioException ex) {
        return responder(HttpStatus.CONFLICT, ex.getMessage());
    }

    // 400: fallaron las validaciones de un DTO (@NotBlank, @Positive, etc.)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> validacion(MethodArgumentNotValidException ex) {
        List<String> detalles = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .toList();
        ErrorResponse cuerpo = ErrorResponse.of(400, "Bad Request", "Datos invalidos", detalles);
        return ResponseEntity.badRequest().body(cuerpo);
    }

    // 400: el JSON enviado esta mal formado o tiene tipos incorrectos
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> jsonInvalido(HttpMessageNotReadableException ex) {
        return responder(HttpStatus.BAD_REQUEST, "El cuerpo de la peticion no es valido");
    }

    // 409: la base rechazo la operacion (UNIQUE, FOREIGN KEY o CHECK)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> integridad(DataIntegrityViolationException ex) {
        log.warn("Violacion de integridad: {}", ex.getMostSpecificCause().getMessage());
        return responder(HttpStatus.CONFLICT, "La operacion viola una restriccion de la base de datos");
    }

    // 401: usuario o contrasena incorrectos
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> credenciales(BadCredentialsException ex) {
        return responder(HttpStatus.UNAUTHORIZED, "Usuario o contrasena incorrectos");
    }

    // 403: el rol del usuario no tiene permiso para esta operacion
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> accesoDenegado(AccessDeniedException ex) {
        return responder(HttpStatus.FORBIDDEN, "No tiene permiso para realizar esta operacion");
    }

    // 500: cualquier error no previsto. Se registra en consola y no se expone el detalle
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> general(Exception ex) {
        log.error("Error no controlado", ex);
        return responder(HttpStatus.INTERNAL_SERVER_ERROR, "Ocurrio un error interno");
    }

    private ResponseEntity<ErrorResponse> responder(HttpStatus estado, String mensaje) {
        ErrorResponse cuerpo = ErrorResponse.of(estado.value(), estado.getReasonPhrase(), mensaje);
        return ResponseEntity.status(estado).body(cuerpo);
    }
}
