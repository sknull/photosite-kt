package de.visualdigits.photosite.domain.data.model.plugin

import de.visualdigits.photosite.domain.data.model.common.Language
import de.visualdigits.photosite.domain.data.model.page.Page
import de.visualdigits.photosite.domain.data.model.page.content.ContentType
import de.visualdigits.photosite.domain.service.ImageService
import de.visualdigits.photosite.domain.util.getRelativeResourcePath
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component
import java.io.File

@Component
@ConfigurationProperties(prefix = "photosite.plugins.lightgallery")
class LightGallery(
    val mode: String? = null,
    val speed: Long = 0,
    val pause: Long = 0,
    val showThumbByDefault: Boolean = false,
    val animateThumb: Boolean = false,
    val progressBar: Boolean = false,
    val download: Boolean = false
) : Plugin(
    contentType = ContentType.LightGallery
) {

    override fun getHead(theme: String): String {
        return """<link href="/resources/themes/$theme/plugins/lightgallery/css/lightgallery.css" rel="stylesheet" type="text/css"/>
        <link href="/resources/themes/$theme/css/plugin-photostory.css" rel="stylesheet" type="text/css"/>
        <link href="/resources/themes/$theme/css/plugin-lightgallery.css" rel="stylesheet" type="text/css"/>
        <script src="/resources/themes/$theme/plugins/lightgallery/js/lightgallery.js" type="text/javascript"></script>
        <script src="/resources/themes/$theme/plugins/lightgallery/js/lg-thumbnail.js" type="text/javascript"></script>
        <script src="/resources/themes/$theme/plugins/lightgallery/js/lg-fullscreen.js" type="text/javascript"></script>
        <script src="/resources/themes/$theme/plugins/lightgallery/js/lg-autoplay.js" type="text/javascript"></script>"""
    }

    override fun renderHtml(page: Page, language: Language, imageService: ImageService): String {
        val sb = StringBuilder()
        sb.append("          <h1>${page.path}</h1>\n")
        page.content.teaser?.also { teaser ->
            sb.append("          <div id=\"teaser\">\n")
            sb.append(teaser.getHtml(language))
            sb.append("          </div>\n")
        }
        sb.append("          <div id=\"lightgallery\" itemscope=\"itemscope\" itemtype=\"http://schema.org/ImageGallery\">\n")
        page.content.images
            .forEach { imageFile ->
                val image: File = imageFile.file
                val imagePath = getRelativeResourcePath(image)
                val thumbPath = imageService.getThumbnail(imageFile)
                var imageName = image.getName()
                imageName = imageName.substring(0, imageName.indexOf('.'))
                sb.append("            <a class=\"item\" href=\"/")
                    .append(imagePath)
                    .append("\"")
                imageName += "&nbsp;(" + (imageFile.lastModified?.format("yyy-MM-dd HH:mm:ss")?:"") + ")"
                sb.append(" data-sub-html=\"")
                    .append("<div class='camera-infos camera-infos-grid'>")
                    .append("<div id='camera-infos-caption' class='info-box'>")
                    .append(imageName)
                    .append("</div>")
                    .append("<div id='camera-infos-exposure' class='info-box'>")
                    .append(imageFile.apertureValue?:"")
                    .append("&nbsp;|&nbsp;")
                    .append(imageFile.exposureTime?:"")
                    .append("&nbsp;")
                    .append(imageFile.exposureBias?:"")
                    .append("&nbsp;|&nbsp;ISO&nbsp;")
                    .append(imageFile.isoEquivalent?:"")
                    .append("&nbsp;|&nbsp;")
                    .append(imageFile.focalLength?:"")
                    .append("</div>")
                    .append("<div id='camera-infos-lens' class='info-box'>")
                    .append(imageFile.make?:"")
                    .append("&nbsp;")
                    .append(imageFile.model?:"")
                    .append("&nbsp;-&nbsp;")
                    .append(imageFile.lensModel?:"")
                    .append("</div>")
                    .append("</div>\"")
                sb.append(">\n")
                sb.append("              <img class=\"thumb\" src=\"/")
                    .append(thumbPath)
                    .append("\" alt=\"")
                    .append(imageName)
                    .append("\" title=\"")
                    .append(imageName)
                    .append("\" itemscope=\"itemscope\" itemtype=\"http://schema.org/Photograph\"/>\n")
                    .append("            </a>\n")
            }
        sb.append("          </div><!-- lightgallery -->\n")
            .append("          <script type=\"text/javascript\">\n")
            .append("            lightGallery(document.getElementById('lightgallery'), {\n")
            .append("                selector: '.item',\n")
            .append("                pause: ")
            .append(pause)
            .append(",\n")
            .append("                animateThumb: ")
            .append(animateThumb)
            .append(",\n")
            .append("                mode: ")
            .append(mode)
            .append(",\n")
            .append("                showThumbByDefault: ")
            .append(showThumbByDefault)
            .append(",\n")
            .append("                download: ")
            .append(download)
            .append(",\n")
            .append("                speed: ")
            .append(speed)
            .append(",\n")
            .append("                progressBar: ")
            .append(progressBar)
            .append("\n")
            .append("            })\n")
            .append("          </script>\n")
        return "\n$sb"
    }

}
