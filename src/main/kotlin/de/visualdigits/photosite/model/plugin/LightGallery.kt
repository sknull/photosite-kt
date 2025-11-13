package de.visualdigits.photosite.model.plugin

import com.drew.metadata.exif.ExifIFD0Directory
import com.drew.metadata.exif.ExifSubIFDDescriptor
import com.drew.metadata.exif.ExifSubIFDDirectory
import de.visualdigits.photosite.model.page.Page
import de.visualdigits.photosite.model.page.content.ContentType
import de.visualdigits.photosite.model.photosite.Photosite
import de.visualdigits.photosite.service.ImageService
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component
import java.io.File
import java.time.format.DateTimeFormatter
import java.util.Locale

@Component
@ConfigurationProperties(prefix = "photosite.plugins.lightgallery")
class LightGallery(
    var mode: String? = null,
    var speed: Long = 0,
    var pause: Long = 0,
    var showThumbByDefault: Boolean = false,
    var animateThumb: Boolean = false,
    var progressBar: Boolean = false,
    var download: Boolean = false
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

    override fun renderHtml(page: Page, language: Locale, imageService: ImageService): String {
        val sb =
            StringBuilder("          <div id=\"lightgallery\" itemscope=\"itemscope\" itemtype=\"http://schema.org/ImageGallery\">\n")
        page.content.images
            .forEach { imageFile ->
                val image: File = imageFile.file
                val metadata = imageFile.metadata
                val imagePath = Photosite.getRelativeResourcePath(image)
                val thumbPath = imageService.getThumbnail(imageFile)
                val exifDir =
                    metadata?.getFirstDirectoryOfType(ExifIFD0Directory::class.java)
                val exifSubDir =
                    metadata?.getFirstDirectoryOfType(ExifSubIFDDirectory::class.java)
                val exifSub = ExifSubIFDDescriptor(exifSubDir)
                var imageName = image.getName()
                imageName = imageName.substring(0, imageName.indexOf('.'))
                sb.append("            <a class=\"item\" href=\"/")
                    .append(imagePath)
                    .append("\"")
                if (exifDir != null && exifSubDir != null) {
                    imageName += "&nbsp;(" + DateTimeFormatter.ofPattern("yyy-MM-dd HH:mm:ss").format(imageFile.lastModified) + ")"
                    sb.append(" data-sub-html=\"")
                        .append("<div class='camera-infos camera-infos-grid'>")
                        .append("<div id='camera-infos-caption' class='info-box'>")
                        .append(imageName)
                        .append("</div>")
                        .append("<div id='camera-infos-exposure' class='info-box'>")
                        .append(exifSub.getApertureValueDescription())
                        .append("&nbsp;|&nbsp;")
                        .append(exifSub.getExposureTimeDescription())
                        .append("&nbsp;")
                        .append(exifSub.getExposureBiasDescription())
                        .append("&nbsp;|&nbsp;ISO&nbsp;")
                        .append(exifSub.getIsoEquivalentDescription())
                        .append("&nbsp;|&nbsp;")
                        .append(exifSub.getFocalLengthDescription())
                        .append("</div>")
                        .append("<div id='camera-infos-lens' class='info-box'>")
                        .append(exifDir.getString(ExifIFD0Directory.TAG_MAKE))
                        .append("&nbsp;")
                        .append(exifDir.getString(ExifIFD0Directory.TAG_MODEL))
                        .append("&nbsp;-&nbsp;")
                        .append(exifSubDir.getString(ExifSubIFDDirectory.TAG_LENS_MODEL))
                        .append("</div>")
                        .append("</div>\"")
                }
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
