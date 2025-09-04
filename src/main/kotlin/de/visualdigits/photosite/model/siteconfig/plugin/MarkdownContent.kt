package de.visualdigits.photosite.model.siteconfig.plugin

import com.github.rjeschke.txtmark.Processor
import de.visualdigits.photosite.model.page.Page
import de.visualdigits.photosite.model.pagemodern.ContentType
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component
import java.util.Locale

@Component
@ConfigurationProperties(prefix = "photosite.plugins.markdown")
class MarkdownContent : HtmlContent(
    contentType = ContentType.Markdown
) {

    override fun getHtml(page: Page, language: Locale): String {
        var html = page.content.mdContent?.let{ mdc -> Processor.process(mdc) } ?: ""
        html = obfuscateText(html)
        html = obfuscateEmail(html)

        return html
    }
}
