package org.uqbar.tareas.errors

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class RestExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(e: MethodArgumentNotValidException): ResponseEntity<String> {
        val message = e.bindingResult.fieldErrors
            .joinToString("; ") { it.defaultMessage ?: "Valor inválido en '${it.field}'" }
        return ResponseEntity.badRequest().body(message)
    }
}
