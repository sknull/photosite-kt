package de.visualdigits.kotlin.photosite.model.siteconfig.plugin

import com.drew.metadata.exif.ExifIFD0Directory
import com.drew.metadata.exif.ExifSubIFDDescriptor
import com.drew.metadata.exif.ExifSubIFDDirectory
import de.visualdigits.kotlin.photosite.model.page.Page
import de.visualdigits.kotlin.photosite.model.siteconfig.SiteConfig
import de.visualdigits.kotlin.photosite.util.ImageHelper
import java.io.File
import java.text.SimpleDateFormat

class LightGallery(
    val mode: String? = null,
    val speed: Long = 0,
    val pause: Long = 0,
    val showThumbByDefault: Boolean = false,
    val animateThumb: Boolean = false,
    val progressBar: Boolean = false,
    val download: Boolean = false
) : Plugin(
    name = "LightGallery",
    clazz = "de.visualdigits.kotlin.photosite.model.siteconfig.plugin.LightGallery"
) {

    private val imageHelper: ImageHelper = ImageHelper()

    override fun getHead(siteConfig: SiteConfig): String {
        val theme = siteConfig.site.theme
        return """<link href="/resources/theme/$theme/plugins/lightgallery/css/lightgallery.min.css" rel="stylesheet" type="text/css"/>
    <link href="/resources/theme/$theme/css/plugin-photostory.css" rel="stylesheet" type="text/css"/>
    <link href="/resources/theme/$theme/css/plugin-lightgallery.css" rel="stylesheet" type="text/css"/>
    <script src="/resources/theme/$theme/plugins/lightgallery/js/lightgallery.min.js" type="text/javascript"></script>
    <script src="/resources/theme/$theme/plugins/lightgallery/js/lg-thumbnail.min.js" type="text/javascript"></script>
    <script src="/resources/theme/$theme/plugins/lightgallery/js/lg-fullscreen.min.js" type="text/javascript"></script>
    <script src="/resources/theme/$theme/plugins/lightgallery/js/lg-autoplay.min.js" type="text/javascript"></script>"""
    }

    override fun getHtml(siteConfig: SiteConfig, page: Page, language: String): String {
        val sb =
            StringBuilder("          <div id=\"lightgallery\" itemscope=\"itemscope\" itemtype=\"http://schema.org/ImageGallery\">\n")
        page.images
            ?.forEach { imageFile ->
                val image: File = imageFile.file
                val metadata = imageFile.metadata
                val imagePath = siteConfig.getRelativeResourcePath(image)
                val thumbPath = imageHelper.getThumbnail(siteConfig, imageFile)
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
                    val date = imageFile.date
                    if (date != null) {
                        imageName += "&nbsp;(" + SimpleDateFormat("yyy-MM-dd HH:mm:ss").format(date) + ")"
                    }
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
        return sb.toString()
    }

}
