package com.changeguard.change.api;

import com.changeguard.change.error.ChangeNotFoundException;
import com.changeguard.change.error.ChangeRejectedException;
import com.changeguard.change.error.DependencyUnavailableException;
import com.changeguard.change.error.InvalidStateException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import java.time.Instant;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ApiError> invalid(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(f -> f.getField() + " : " + f.getDefaultMessage())
                .collect(Collectors.joining(" ; "));
        return build(HttpStatus.BAD_REQUEST, message);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    ResponseEntity<ApiError> unreadable(HttpMessageNotReadableException e) {
        return build(HttpStatus.BAD_REQUEST, "Corps JSON invalide");
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    ResponseEntity<ApiError> badParameter(MethodArgumentTypeMismatchException e) {
        return build(HttpStatus.BAD_REQUEST, "Paramètre invalide : " + e.getName());
    }

    @ExceptionHandler(ChangeNotFoundException.class)
    ResponseEntity<ApiError> notFound(ChangeNotFoundException e) {
        return build(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler({InvalidStateException.class, ChangeRejectedException.class})
    ResponseEntity<ApiError> conflict(RuntimeException e) {
        return build(HttpStatus.CONFLICT, e.getMessage());
    }

    @ExceptionHandler(OptimisticLockingFailureException.class)
    ResponseEntity<ApiError> concurrent(OptimisticLockingFailureException e) {
        return build(HttpStatus.CONFLICT, "Le changement a été modifié par une autre requête");
    }

    @ExceptionHandler(DependencyUnavailableException.class)
    ResponseEntity<ApiError> unavailable(DependencyUnavailableException e) {
        return build(HttpStatus.SERVICE_UNAVAILABLE, e.getMessage());
    }

    private ResponseEntity<ApiError> build(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(
                new ApiError(status.value(), status.getReasonPhrase(), message, Instant.now()));
    }
}