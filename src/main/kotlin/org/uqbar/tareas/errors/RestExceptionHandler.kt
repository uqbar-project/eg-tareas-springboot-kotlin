package org.uqbar.tareas.errors

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class RestExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(e: MethodArgumentNotValidException): ResponseEntity<Map<String, Any>> {
        val message = e.bindingResult.fieldErrors
            .mapNotNull { it.defaultMessage }
            .joinToString("; ")

        return ResponseEntity.badRequest().body(
            mapOf("error" to message)
        )
    }
}
