package de.visualdigits.photosite.configuration

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.web.filter.OncePerRequestFilter

class InternalApiAccessFilterBean(
    private val internalPort: Int,
    private val internalEndpoints: List<String>
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        chain: FilterChain
    ) {
        val requestUri = request.requestURI
        val requestPort = request.serverPort
        if (internalEndpoints.any { ep -> requestUri.startsWith(ep) } && requestPort != internalPort) {
            response.sendError(HttpStatus.UNAUTHORIZED.value())
        } else {
            chain.doFilter(request, response)
        }
    }
}