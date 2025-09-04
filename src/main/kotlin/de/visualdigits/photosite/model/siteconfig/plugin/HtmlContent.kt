package de.visualdigits.photosite.model.siteconfig.plugin

import de.visualdigits.photosite.model.page.ContentType
import de.visualdigits.photosite.model.page.Page
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component
import java.util.Locale
import java.util.regex.Pattern

@Component
@ConfigurationProperties(prefix = "photosite.plugins.html")
open class HtmlContent(
    contentType: ContentType = ContentType.Html
) : Plugin(
    contentType = contentType
) {

    override fun getHtml(page: Page, language: Locale): String {
        var html = page.content.htmlContent?:""
        html = obfuscateText(html)
        html = obfuscateEmail(html)

        return html
    }

    protected fun obfuscateText(html: String): String {
        var h = html
        val matcher = Pattern.compile("\\{obfuscate-text:(.*?)}").matcher(h)
        while (matcher.find()) {
            h = h.replace(matcher.group(0), obfuscate(matcher.group(1), null, ObfuscateType.TEXT))
        }

        return h
    }

    protected fun obfuscateEmail(html: String): String {
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
