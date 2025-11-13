package de.visualdigits.photosite.model.plugin

import com.github.rjeschke.txtmark.Processor
import de.visualdigits.photosite.model.page.Page
import de.visualdigits.photosite.model.page.content.ContentType
import de.visualdigits.photosite.service.ImageService
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component
import java.util.Locale

@Component
@ConfigurationProperties(prefix = "photosite.plugins.markdown")
class MarkdownContent : HtmlContent(
    contentType = ContentType.Markdown
) {

    override fun renderHtml(page: Page, language: Locale, imageService: ImageService): String {
        var html = page.content.mdContent?.let{ mdc -> Processor.process(mdc) } ?: ""
        html = obfuscateText(html)
        html = obfuscateEmail(html)

        return "\n$html"
    }
}
