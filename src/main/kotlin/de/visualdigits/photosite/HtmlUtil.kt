package de.visualdigits.photosite

import jakarta.servlet.http.HttpServletRequest
import java.io.UnsupportedEncodingException
import java.net.URLDecoder

object HtmlUtil {

    fun HttpServletRequest.getRequestUri(): String {
        var uri = ""
        try {
            uri = URLDecoder.decode(this.requestURI, this.characterEncoding)
        } catch (_: UnsupportedEncodingException) {
            // ignore
        }
        return uri
    }
}