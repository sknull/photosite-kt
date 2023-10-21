package de.visualdigits.kotlin.photosite.model.page

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.module.kotlin.kotlinModule
import com.github.rjeschke.txtmark.Processor
import de.visualdigits.kotlin.photosite.model.common.HtmlSnippet
import de.visualdigits.kotlin.photosite.model.common.I18nProvider
import de.visualdigits.kotlin.photosite.model.common.ImageFile
import de.visualdigits.kotlin.photosite.model.common.Language
import de.visualdigits.kotlin.photosite.model.common.sort.Sort
import de.visualdigits.kotlin.photosite.model.common.sort.SortDir
import de.visualdigits.kotlin.photosite.model.siteconfig.SiteConfig
import org.slf4j.LoggerFactory
import java.io.File
import java.io.IOException
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.*
import java.util.function.Consumer
import java.util.regex.Pattern

@JsonIgnoreProperties(
    "log",
    "file",
    "path",
    "name",
    "mdContent",
    "htmlContent",
    "images",
    "parent",
    "childs",
    "lastModifiedTimestamp"
)
class Page(
    @JacksonXmlProperty(isAttribute = true) val icon: String? = null,
    @JacksonXmlProperty(localName = "tocname") val tocName: String? = null,
    i18n: List<Language> = listOf(),
    val content: Content? = null,
) : I18nProvider(i18n), HtmlSnippet {

    private val log = LoggerFactory.getLogger(Page::class.java)

    var file: File? = null
    var path: String? = null
    var name: String? = null
    var mdContent: String? = null
    var htmlContent: String? = null
    var images: MutableList<ImageFile> = mutableListOf()
    var parent: Page? = null
    var childs: MutableList<Page> = mutableListOf()
    var lastModifiedTimestamp: OffsetDateTime = OffsetDateTime.MIN

    companion object {
        val MAPPER = XmlMapper.builder()
            .addModule(kotlinModule())
            .disable(DeserializationFeature.FAIL_ON_NULL_CREATOR_PROPERTIES)
            .disable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)
            .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
            .build()

        fun load(descriptorFile: File): Page? {
            val directory = descriptorFile.parentFile
            return try {
                val page = MAPPER.readValue(
                    descriptorFile,
                    Page::class.java
                )
                page.file = descriptorFile
                page.name = directory.getName()
                page.loadExternalContent(directory)
                page.loadImages(directory)
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

        private fun obfuscateLink(html: String): String {
            var h = html
            val matcher = Pattern.compile("<a href=\"(.*?)\">(.*?)</a>").matcher(h)
            while (matcher.find()) {
                h = h.replace(matcher.group(0), obfuscate(matcher.group(1), matcher.group(2), ObfuscateType.LINK))
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
//        return try {
//            MAPPER.writeValueAsString(this)
//        } catch (e: JsonProcessingException) {
//            throw IllegalStateException("Could not render page descriptor", e)
//        }
    }

    fun loadExternalContent(directory: File) {
        log.debug("Loading external content for directory: {}", directory)
        if (mdContent == null) {
            val mdFile = File(directory, "page.md")
            mdContent = readFile(mdFile)
        }
        if (htmlContent == null) {
            val htmlFile = File(directory, "page.html")
            htmlContent = readFile(htmlFile)
        }
    }

    private fun loadImages(directory: File) {
        if (images.isEmpty()) {
            directory
                .listFiles { file: File ->
                    file.isFile() && file.getName().lowercase(Locale.getDefault()).endsWith(".jpg")
                }?.map { image ->
                    ImageFile(image)
                }?.toMutableList()
                ?.let { images.addAll(it) }
            sortImages()
            determineLastModifiedTimestamp()
        }
    }

    fun getTitle(language: String?): String? {
        var title = name
        val lang = i18nMap[language]
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

    fun clear() {
        childs.clear()
    }

    override fun getHead(siteConfig: SiteConfig): String {
        var head: String = ""
        if (content != null) {
            head = content.getHead(siteConfig)
        }
        return head
    }

    override fun getHtml(
        siteConfig: SiteConfig,
        page: Page,
        language: String
    ): String {
        val sb = StringBuilder()
        sb
            .append("\n          <div id=\"")
            .append(page.name)
            .append("\">\n")
            .append("          <h1>")
            .append(page.getTitle(language))
            .append("</h1>\n")
        if (content != null) {
            val teaser = content.teaser
            if (teaser != null) {
                sb.append(teaser.getHtml(siteConfig, page, language))
            }
            sb.append(content.getHtml(siteConfig, page, language))
        } else if (htmlContent?.isNotEmpty() == true) {
            sb.append(htmlContent)
        } else if (mdContent?.isNotEmpty() == true) {
            val html = Processor.process(mdContent)
            sb.append(html)
        }
        sb.append("        </div><!-- ")
            .append(page.name)
            .append(" -->\n")
        sb.append("      ")
        var html = sb.toString()

//        html = obfuscateLink(html);
        html = obfuscateEmail(html)
        html = obfuscateText(html)
        return html
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

    fun determineLastModifiedTimestamp() {
        lastModifiedTimestamp = OffsetDateTime.MIN
        images.forEach { image ->
            val time = image.date?.toInstant()?.atOffset(ZoneOffset.UTC)?:OffsetDateTime.MIN
            if (time > lastModifiedTimestamp) {
                lastModifiedTimestamp = time
            }
        }
    }

    private fun sortImages() {
        val sort = determineSort()
        if (sort.by == "manual") {
            val map = mutableMapOf<String, ImageFile>()
            images.forEach(Consumer { p: ImageFile -> map[p.file.getName()] = p })
            val sortedPages: MutableList<ImageFile> = ArrayList()
            sort.orderList.forEach { p ->
                val imageFile = map[p]
                if (imageFile != null) {
                    sortedPages.add(imageFile)
                    images.remove(imageFile)
                }
            }
            images.let { sortedPages.addAll(it) }
            images.clear()
            images.addAll(sortedPages)
        } else {
            when (sort.by) {
                "name" -> images.sortBy { it.name }
                "mtime" -> images.sortBy { it.date }
                else -> {}
            }
            if (sort.dir == SortDir.desc) {
                images.reverse()
            }
        }
    }

    private fun determineSort(): Sort {
        return content?.sort?:Sort(by = "name", dir = SortDir.asc)
    }

    fun addChild(child: Page) {
        if (!childs.contains(child)) {
            child.parent = this
            child.path = path + "/" + child.name
            childs.add(child)
        }
    }

    private fun readFile(file: File): String {
        var contents = ""
        if (file.exists()) {
            contents = try {
                file.readText()
            } catch (e: IOException) {
                throw IllegalStateException("Could not read file contents: ", e)
            }
        }
        return contents
    }

    fun getNormalizedPath(): String {
        var pagePath = path!!
        val parts = pagePath.split("/").dropLastWhile { it.isEmpty() }
        if (parts.size > 0 && "pagetree" == parts[0]) {
            val lparts = parts.map { p: String ->
                var s = p
                if (p.startsWith("-")) {
                    s = p.substring(1)
                }
                s
            }
            pagePath = lparts.subList(1, lparts.size).joinToString("/")
        }
        return pagePath
    }
}
