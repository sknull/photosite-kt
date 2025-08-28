package de.visualdigits.photosite.controller

import de.visualdigits.photosite.Application
import de.visualdigits.photosite.model.siteconfig.Photosite
import de.visualdigits.photosite.model.siteconfig.navi.PageTree
import de.visualdigits.photosite.util.DomainCertificatesHelper
import de.visualdigits.photosite.util.DomainCertificatesHelper.determineExpiryDate
import de.visualdigits.photosite.util.PageHelper
import jakarta.annotation.PostConstruct
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.apache.commons.io.IOUtils
import org.apache.tika.metadata.Metadata
import org.apache.tika.metadata.TikaCoreProperties
import org.apache.tika.parser.AutoDetectParser
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.UnsupportedEncodingException
import java.net.URLDecoder
import java.nio.file.Paths
import java.time.LocalDateTime


@Controller("PageController")
class PageController(
    private val photosite: Photosite
) {

    @Value("\${certbot.uri}")
    private lateinit var certbotUri: String

    @Value("\${certbot.alias}")
    private lateinit var certbotAlias: String

    @Value("\${certbot.password}")
    private lateinit var certbotPassword: String

    private lateinit var expiryDate: LocalDateTime

    @PostConstruct
    fun initialize() {
        expiryDate = determineExpiryDate(photosite, certbotAlias, certbotPassword)
    }

    @GetMapping(value = ["/**"], produces = ["application/xhtml+xml"])
    fun page(
        @RequestParam(name = "lang", required = false) lang: String?,
        model: Model,
        request: HttpServletRequest,
        response: HttpServletResponse
    ): String? {
        if (photosite.isProfileActive("prod")) {
            val newExpiryDate = DomainCertificatesHelper.maintainServerCertificate(
                photosite = photosite,
                certbotUri = certbotUri,
                certbotAlias = certbotAlias,
                certbotPassword = certbotPassword,
                expiryDate = expiryDate
            )
            if (newExpiryDate.isAfter(expiryDate)) {
                Application.restart("ssl")
            }
        }

        val requestUri = getRequestUri(request)
        return if (resourceFileExists(requestUri)) {
            if (requestUri.startsWith("/resources") || requestUri.startsWith("/.well-known/acme-challenge")) {
                getResource(requestUri, response)
            } else {
                response.sendError(404)
            }
            null
        } else {
            val currentPage = PageHelper.determinePage(requestUri)
            val language = lang?:photosite.languageDefault
            model.addAttribute("theme", photosite.theme)
            model.addAttribute("siteUrl", photosite.siteUrl)
            model.addAttribute("language", language)
            model.addAttribute("title", photosite.siteTitle)
            model.addAttribute("naviMain", PageHelper.createMainNavigation(photosite, currentPage, language))
            model.addAttribute("naviSub", PageHelper.createSubNavigation(photosite, language))
            val fullPageTreeStatic = PageTree(
                pageDirectory = Paths.get(photosite.rootDirectory.canonicalPath, "resources", "pagetree").toFile(),
                nameFilter = { name -> "pagetree" == name || name.startsWith("-") },
                dump = false
            )
            val naviStatic = PageHelper.createStaticNavigation(photosite, fullPageTreeStatic, language)
            model.addAttribute("naviStatic", naviStatic)
            PageHelper.createContent(photosite, currentPage, model, language, photosite.pageTree, fullPageTreeStatic)
            "pagetemplate"
        }
    }

    private fun getRequestUri(request: HttpServletRequest): String {
        var uri = ""
        try {
            uri = URLDecoder.decode(request.requestURI, request.characterEncoding)
        } catch (e: UnsupportedEncodingException) {
            // ignore
        }
        return uri
    }

    private fun resourceFileExists(resourcePath: String): Boolean {
        val file = getResourceFile(resourcePath)
        return file?.isFile() == true && file.exists()
    }

    private fun getResourceFile(resourcePath: String): File? {
        return Paths.get(photosite.rootDirectory.canonicalPath, resourcePath).toFile()
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
            throw IllegalStateException("Could not hand out resource: $resourcePath", e)
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
        } catch (e: IOException) {
            // ignore
        }
        return mimeType
    }
}

