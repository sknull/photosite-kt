package de.visualdigits.photosite.presentation.handler

import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus

@ControllerAdvice
class UiExceptionHandler {

    private val log = LoggerFactory.getLogger(javaClass)

    @ExceptionHandler(Exception::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleUiException(ex: Exception, request: HttpServletRequest, model: Model): String {
        log.error("Unexpected error while calling UI: ${ex.message}", ex)

        model.addAttribute("language", "de")
        model.addAttribute("errortitle", "An error occurred")
        model.addAttribute("errormessage", "An unexpected internal error occurred.")
        model.addAttribute("path", request.requestURI)

        return "error"
    }
}
