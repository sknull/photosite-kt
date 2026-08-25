package de.visualdigits.photosite.presentation.handler

import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException

@RestControllerAdvice
class ApiExceptionHandler {

    private val log = LoggerFactory.getLogger(javaClass)

    @ExceptionHandler(
        IllegalStateException::class,
        IllegalArgumentException::class,
        MethodArgumentNotValidException::class,
        MethodArgumentTypeMismatchException::class
    )
    fun handleApiValidationErrors(
        ex: Exception,
        request: HttpServletRequest
    ): ResponseEntity<Any> {
        log.error("API-Validation error (${ex.javaClass.simpleName}): ${ex.message}")

        val userFriendlyMessage = when (ex) {
            is MethodArgumentTypeMismatchException -> "Invalid type for parameter '${ex.name}'. Expected was: ${ex.requiredType?.simpleName}"
            is MethodArgumentNotValidException -> "Validation failed: " + ex.bindingResult.fieldErrors.joinToString { "${it.field}: ${it.defaultMessage}" }
            else -> ex.message ?: "Invalid parameter value"
        }

        val errorBody = mapOf(
            "timestamp" to System.currentTimeMillis(),
            "status" to HttpStatus.BAD_REQUEST.value(),
            "error" to "Bad Request",
            "message" to userFriendlyMessage,
            "path" to request.requestURI
        )
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorBody)
    }
}
