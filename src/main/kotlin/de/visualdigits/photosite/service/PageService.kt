package de.visualdigits.photosite.service

import de.visualdigits.photosite.Application
import de.visualdigits.photosite.model.page.Page
import de.visualdigits.photosite.model.photosite.Photosite
import jakarta.annotation.PostConstruct
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.apache.commons.io.IOUtils
import org.apache.tika.metadata.Metadata
import org.apache.tika.metadata.TikaCoreProperties
import org.apache.tika.parser.AutoDetectParser
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.ui.Model
import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.UnsupportedEncodingException
import java.net.URLDecoder
import java.nio.file.Paths
import java.time.LocalDateTime
import java.util.Locale

@Service
class PageService(
    private val photosite: Photosite,
    private val imageService: ImageService,
    private val domainCertificatesService: DomainCertificatesService
) {

    @Value("\${photosite.ssl.certbot-uri}")
    private lateinit var certbotUri: String

    @Value("\${photosite.ssl.key-alias}")
    private lateinit var certbotAlias: String

    @Value("\${photosite.ssl.key-store-password}")
    private lateinit var certbotPassword: String

    private lateinit var expiryDate: LocalDateTime


    @PostConstruct
    fun initialize() {
        expiryDate = domainCertificatesService.determineExpiryDate(certbotAlias, certbotPassword)
    }


    fun renderPage(
        lang: Locale,
        model: Model,
        request: HttpServletRequest,
        response: HttpServletResponse
    ): String? {
        refreshCertIfNeeded()

        var requestUri = getRequestUri(request)
        return if (resourceFileExists(requestUri)) {
            if (requestUri.startsWith("/resources") || requestUri.startsWith("/.well-known/acme-challenge")) {
                getResource(requestUri, response)
            } else {
                response.sendError(404)
            }
            null
        } else {
            requestUri = requestUri.replace("/pagetree", "")
            if (requestUri.startsWith("/")) requestUri = requestUri.drop(1)
            val currentPage = photosite.pageTree.page(requestUri) ?: photosite.pageTree
            val currentPagePath = currentPage.path()
            val language = Locale.forLanguageTag(lang.language)
            model.addAttribute("language", language.language)
            model.addAttribute("theme", photosite.theme)
            model.addAttribute("siteUrl", photosite.siteUrl)
            model.addAttribute("language", language)
            model.addAttribute("title", photosite.siteTitle)
            model.addAttribute(
                "naviMain",
                photosite.mainTree.mainNaviHtml(
                    photosite.naviMain ?: error("No main navi"),
                    language,
                    currentPage,
                    photosite.theme
                )
            )
            model.addAttribute(
                "naviSub",
                photosite.subTrees
                    .mapIndexed { index, (naviName, pages) ->
                        Page.subNaviHtml(
                            naviName,
                            language,
                            currentPage,
                            pages,
                            photosite.theme,
                            0,
                            "sub-navigation_${index + 1}"
                        )
                    }.joinToString("")
            )
            model.addAttribute(
                "naviStatic", Page.subNaviHtml(
                    photosite.naviStatic ?: error("No static navi"),
                    language,
                    currentPage,
                    photosite.staticTree.children,
                    photosite.theme,
                    0,
                    "static-navigation"
                )
            )

            val languageSelector = photosite.languages.joinToString("") { l ->
                val current = if (lang == l) " class=\"current\"" else ""
                "<li><a${current} href=\"/?lang=${l.language}\">${l.language}</a></li>"
            }

            model.addAttribute("languageSelector", "<ol>$languageSelector</ol>")

            listOf(photosite.mainTree, photosite.staticTree)
                .firstNotNullOfOrNull { pageTree -> pageTree.page(currentPagePath) }
                ?.let { page ->
                    val keywords = page.content.keywords.toMutableList()
                    val path = page.path()
                    keywords.addAll(path.split("/"))
                    model.addAttribute("breadcrumb", path)
                    model.addAttribute("metaKeywords", keywords.joinToString(", "))
                    model.addAttribute("metaDescription", keywords.joinToString(" "))
                    val pluginConfig = photosite.pluginsMap[page.content.contentType]
                    model.addAttribute("head", pluginConfig?.getHead(photosite.theme))
                    model.addAttribute("content", pluginConfig?.renderHtml(page, language, imageService))
                }

            "pagetemplate"
        }
    }

    private fun refreshCertIfNeeded() {
        if (photosite.isProfileActive("prod")) {
            val newExpiryDate = domainCertificatesService.maintainServerCertificate(
                certbotUri = certbotUri,
                certbotAlias = certbotAlias,
                certbotPassword = certbotPassword,
                expiryDate = expiryDate
            )
            if (newExpiryDate.isAfter(expiryDate)) {
                Application.restart("ssl")
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
        } catch (_: IOException) {
            // ignore
        }
        return mimeType
    }
}
