package de.visualdigits.photosite.service

import de.visualdigits.photosite.model.photosite.Photosite
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.apache.commons.io.IOUtils
import org.apache.tika.metadata.Metadata
import org.apache.tika.metadata.TikaCoreProperties
import org.apache.tika.parser.AutoDetectParser
import org.springframework.stereotype.Service
import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.UnsupportedEncodingException
import java.net.URLDecoder
import java.nio.file.Paths

@Service
class ResourceService() {

    fun getResource(
        request: HttpServletRequest,
        response: HttpServletResponse
    ) {
        val requestUri = getRequestUri(request)
        if (resourceFileExists(requestUri)) {
            if (requestUri.startsWith("/resources")) {
                getResource(requestUri, response)
            } else {
                response.sendError(404)
            }
        }
    }

    private fun getRequestUri(request: HttpServletRequest): String {
        var uri = ""
        try {
            uri = URLDecoder.decode(request.requestURI, request.characterEncoding)
        } catch (_: UnsupportedEncodingException) {
            // ignore
        }
        return uri
    }

    private fun resourceFileExists(resourcePath: String): Boolean {
        val file = getResourceFile(resourcePath)
        return file?.isFile() == true && file.exists()
    }

    private fun getResourceFile(resourcePath: String): File? {
        return Paths.get(Photosite.rootDirectory.canonicalPath, resourcePath).toFile()
    }

    private fun getResource(
        resourcePath: String,
        response: HttpServletResponse
    ) {
        val file = getResourceFile(resourcePath)
        try {
            file?.let { f -> FileInputStream(f).use { ins ->
                response.outputStream.use { outs ->
                    val mimeType = detectMimeType(file)
                    response.contentType = mimeType
                    IOUtils.copy(ins, outs)
                }
            }
            }
        } catch (e: IOException) {
            throw kotlin.IllegalStateException("Could not hand out resource: $resourcePath", e)
        }
    }

    private fun detectMimeType(file: File): String {
        var mimeType = "text/plain"
        try {
            FileInputStream(file).use { `is` ->
                BufferedInputStream(`is`).use { bis ->
                    val parser = AutoDetectParser()
                    val detector = parser.detector
                    val md = Metadata()
                    md.add(TikaCoreProperties.RESOURCE_NAME_KEY, file.getName())
                    val mediaType = detector.detect(bis, md)
                    mimeType = mediaType.toString()
                }
            }
        } catch (_: IOException) {
            // ignore
        }
        return mimeType
    }
}
