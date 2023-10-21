package de.visualdigits.kotlin.photosite.controller

import de.visualdigits.kotlin.photosite.model.siteconfig.SiteConfig
import de.visualdigits.kotlin.photosite.model.siteconfig.SiteConfigHolder
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.apache.commons.io.IOUtils
import org.apache.tika.metadata.Metadata
import org.apache.tika.metadata.TikaCoreProperties
import org.apache.tika.parser.AutoDetectParser
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import java.io.*
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets


abstract class AbstractBaseController {

    private val log = LoggerFactory.getLogger(AbstractBaseController::class.java)

    @Autowired
    protected lateinit var siteConfigHolder: SiteConfigHolder


    companion object {
        @JvmStatic protected val MIMETYPE_XML = "application/xml"
        @JvmStatic protected val MIMETYPE_XSL = "text/xsl"
        @JvmStatic protected val HEADER_ACCEPT_RSS = "application/rss+xml, application/rdf+xml;q=0.8, application/atom+xml;q=0.6, application/xml;q=0.4, text/xml;q=0.4"
    }

    protected fun getRequestUri(request: HttpServletRequest): String {
        var uri = ""
        try {
            uri = URLDecoder.decode(request.requestURI, request.characterEncoding)
        } catch (e: UnsupportedEncodingException) {
            // ignore
        }
        return uri
    }

    protected fun encodeUrl(response: HttpServletResponse, pagePath: String): String {
        var pp = pagePath
        val url = pp
        try {
            pp = URLEncoder.encode(pp, response.characterEncoding)
        } catch (e: UnsupportedEncodingException) {
            // ignore
        }
        return pp
    }

    protected fun readFile(xslFile: File): String? {
        var content: String? = null
        try {
            FileInputStream(xslFile).use { ins ->
                ByteArrayOutputStream().use { baos ->
                    IOUtils.copy(ins, baos)
                    content = baos.toString()
                }
            }
        } catch (e: IOException) {
            log.error("Could not read xsl file: $xslFile", e)
        }
        return content
    }

    protected fun sendContent(
        content: String,
        mimeType: String,
        headers: Map<String, String> = mapOf(),
        response: HttpServletResponse
    ) {
        try {
            ByteArrayInputStream(content.toByteArray(StandardCharsets.UTF_8)).use { ins ->
                response.outputStream.use { outs ->
                    response.contentType = mimeType
                    headers.forEach(response::addHeader)
                    IOUtils.copy(ins, outs)
                }
            }
        } catch (e: IOException) {
            throw IllegalStateException("Could not hand out rss stream", e)
        }
    }

    protected fun getResource(
        siteConfig: SiteConfig,
        response: HttpServletResponse,
        resourceFolder: String,
        src: String
    ) {
        val file = getResourceFile(siteConfig, resourceFolder, src)
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
            throw IllegalStateException("Could not hand out resource: $src", e)
        }
    }

    protected fun resourceFileExists(siteConfig: SiteConfig, resourceFolder: String, src: String): Boolean {
        val file = getResourceFile(siteConfig, resourceFolder, src)
        return file?.isFile() == true && file.exists()
    }

    protected fun getResourceFile(siteConfig: SiteConfig, resourceFolder: String, src: String): File? {
        return siteConfig.getAbsoluteResource(resourceFolder, src)
    }

    protected fun detectMimeType(file: File): String {
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
        } catch (e: IOException) {
            // ignore
        }
        return mimeType
    }
}

