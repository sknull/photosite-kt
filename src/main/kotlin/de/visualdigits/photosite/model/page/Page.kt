package de.visualdigits.photosite.model.page

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.module.kotlin.kotlinModule
import com.github.rjeschke.txtmark.Processor
import de.visualdigits.photosite.model.common.ImageFile
import de.visualdigits.photosite.model.common.Language
import de.visualdigits.photosite.model.common.LanguageProvider
import de.visualdigits.photosite.model.common.sort.Sort
import de.visualdigits.photosite.model.common.sort.SortDir
import de.visualdigits.photosite.model.siteconfig.Photosite
import org.slf4j.LoggerFactory
import java.io.File
import java.io.IOException
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.Locale
import java.util.TreeSet
import java.util.function.Consumer
import java.util.regex.Pattern

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
    i18n: List<Language> = listOf(),
    val content: Content = Content(),
) : LanguageProvider(i18n) {

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
                page.content.loadExternalContent(directory)
                page.content.loadImages(directory)
                page
            } catch (e: IOException) {
                throw IllegalArgumentException("Could not parse page file: $descriptorFile", e)
            }
        }

        private fun obfuscateText(html: String): String {
            var h = html
            val matcher = Pattern.compile("\\{obfuscate-text:(.*?)}").matcher(h)
            while (matcher.find()) {
                h = h.replace(matcher.group(0), obfuscate(matcher.group(1), null, ObfuscateType.TEXT))
            }
            return h
        }

        private fun obfuscateEmail(html: String): String {
            var h = html
            val matcher = Pattern.compile("\\{obfuscate-email:(.*?)}").matcher(h)
            while (matcher.find()) {
                h = h.replace(matcher.group(0), obfuscate(matcher.group(1), null, ObfuscateType.EMAIL))
            }
            return h
        }

        private fun obfuscate(text: String, linktext: String?, obfuscateType: ObfuscateType): String {
            var lt = linktext
            val unmixedkey = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+-() "
            if (lt?.isEmpty() == true) {
                lt = "\"+link+\""
            }
            var inprogresskey = unmixedkey
            val mixedkey = StringBuilder()
            for (i in unmixedkey.length downTo 1) {
                val ranpos = (Math.random() * i).toInt()
                mixedkey.append(inprogresskey[ranpos])
                inprogresskey = inprogresskey.substring(0, ranpos) + inprogresskey.substring(ranpos + 1, i)
            }
            val cipher = mixedkey.toString()
            val shift = text.length
            val coded = StringBuilder()
            var script = """<script type="text/javascript" language="javascript">
<!--
// Email obfuscator script 2.1 by Tim Williams, University of Arizona
// Random encryption key feature coded by Andrew Moulden
// This code is freeware provided these four comment lines remain intact
// A wizard to generate this code is at http://www.jottings.com/obfuscator
-->
"""
            for (element in text) {
                val chr = element
                val pos = cipher.indexOf(chr)
                if (pos == -1) {
                    coded.append(chr)
                } else {
                    coded.append(cipher[(pos + shift) % cipher.length])
                }
            }
            script += "{\n"
            script += "  coded = \"$coded\"\n  key = \"$cipher\"\n"
            script += "  shift = coded.length\n"
            script += "  link = \"\"\n"
            script += "  linktext = \"$lt\"\n"
            script += "  for (i=0; i<coded.length; i++) {\n"
            script += "    if (key.indexOf(coded.charAt(i))==-1) {\n"
            script += "      ltr = coded.charAt(i)\n"
            script += "      link += (ltr)\n"
            script += "    }\n"
            script += "    else {     \n"
            script += "      ltr = (key.indexOf(coded.charAt(i))-shift+key.length) % key.length\n"
            script += """      link += (key.charAt(ltr))
    }
  }
"""
            script += when (obfuscateType) {
                ObfuscateType.EMAIL -> "document.write(\"<a href='mailto:\"+link+\"'>\"+link+\"</a>\")\n"
                ObfuscateType.LINK -> "document.write(\"<a href='\"+link+\"' target='_blank'>\"+linktext+\"</a>\")\n"
                ObfuscateType.TEXT -> "document.write(link)\n"
            }
            script += "}\n"
            script += "</script>\n"
            return script
        }
    }

    override fun toString(): String {
        return path?:"UNSET"
    }

    fun getTitle(language: String): String? {
        var title = name
        val lang = languageMap[language]
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
