package de.visualdigits.photosite.model.page

import com.fasterxml.jackson.annotation.JsonAlias
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.module.kotlin.kotlinModule
import de.visualdigits.photosite.model.common.Translation
import de.visualdigits.photosite.model.common.LanguageProvider
import org.slf4j.LoggerFactory
import java.io.File
import java.io.IOException
import java.util.Locale
import java.util.TreeSet

@JsonIgnoreProperties(
    "descriptorFile",
    "path",
    "name",
    "mdContent",
    "htmlContent",
    "images",
    "parent",
    "childs",
    "lastModifiedTimestamp",
    "languageMap",

    "head",
    "html",
    "keywords",
    "title"
)
class Page(
    @JacksonXmlProperty(isAttribute = true) val icon: String? = null,
    @JacksonXmlProperty(localName = "tocname") val tocName: String? = null,
    @JsonAlias("i18n", "translations") translations: List<Translation> = listOf(),
    val content: Content = Content(),
) : LanguageProvider(translations) {

    private val log = LoggerFactory.getLogger(javaClass)

    var descriptorFile: File? = null
    var path: String? = null
    var name: String? = null

    var parent: Page? = null
    var childs: MutableList<Page> = mutableListOf()

    companion object {
        val mapper = XmlMapper.builder()
            .addModule(kotlinModule())
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
            .build()

        fun readValue(descriptorFile: File): Page? {
            val directory = descriptorFile.parentFile
            return try {
                val page = mapper.readValue(
                    descriptorFile,
                    Page::class.java
                )
                page.descriptorFile = descriptorFile
                page.name = directory.getName()
                page.content.loadContent(directory)
                page.content.loadImages(directory)
                page
            } catch (e: IOException) {
                throw IllegalArgumentException("Could not parse page file: $descriptorFile", e)
            }
        }
    }

    override fun toString(): String {
        return path?:"UNSET"
    }

    fun getTitle(language: Locale): String? {
        var title = name
        val lang = translationsMap[language]
        if (lang != null) {
            var l: String? = lang.name
            if (l?.isNotBlank() == true) {
                title = l
            }
            l = lang.title
            if (l?.isNotBlank() == true) {
                title = l
            }
        }
        return title
    }

    fun getKeywords(): Set<String> {
        val keywords: MutableSet<String> = TreeSet()
        getKeywords(keywords)
        return keywords
    }

    private fun getKeywords(keywords: MutableSet<String>) {
        keywords.add(name!!.lowercase(Locale.getDefault()))
        if (content != null) {
            val skeywords: String = content.keywords?:""
            if (skeywords.isNotEmpty()) {
                keywords.addAll(skeywords.split(",").dropLastWhile { it.isEmpty() }
                    .map { s: String ->
                        s.trim { it <= ' ' }
                            .lowercase(Locale.getDefault())
                    }
                )
            }
        }
        childs.forEach { c -> c.getKeywords(keywords) }
    }

    fun addChild(child: Page) {
        if (!childs.contains(child)) {
            child.parent = this
            child.path = path + "/" + child.name
            childs.add(child)
        }
    }

    fun normalizedPath(): String {
        val parts = path
            ?.split("/")
            ?.dropLastWhile { it.isEmpty() }
        return if ("pagetree" == parts?.firstOrNull()) {
            parts.map { p ->
                if (p.startsWith("-")) {
                    p.substring(1)
                } else p
            }.drop(1).joinToString("/")
        } else path!!
    }
}
