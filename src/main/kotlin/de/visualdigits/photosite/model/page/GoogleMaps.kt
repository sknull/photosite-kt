package de.visualdigits.photosite.model.page

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.apache.commons.text.StringEscapeUtils

@JsonIgnoreProperties(
    "html"
)
class GoogleMaps(
    val name: String? = null,
    val width: String? = null,
    val height: String? = null,
    val align: String? = null,
    val lat: Double? = null,
    val lng: Double? = null,
    val zoom: Int? = null,
) {

    fun getHtml(): String {
        val sb = StringBuilder()
        val url = "https://www.google.com/maps/embed?pb=" +
                "!1m18!1m12!1m3" +
                "!1d" + zoom +  // height above ground
                "!2d" + lng +
                "!3d" + lat +
                "!2m3!1f0!2f0!3f0!3m2" +
                "!1i1024" +  // width - constant
                "!2i768" +  // height - constant
                "!4f13.1" +  // diagonal screen width - constant
                "!3m3" +
                "!1m2" +
                "!1s" +  //+ "0x47b1efca7eac62df%3A0xa96fb2703b35e848" + // marker
                "!2s" + StringEscapeUtils.escapeHtml4(name) +
                "!5e0!3m2" +
                "!1sde" +  // language
                "!2sde" +  // country
                "!4v1592729653313" +  // timestamp
                "!5m2!1sde!2sde"
        sb.append("          <div id=\"map\" class=\"")
            .append(align)
            .append("\" style=\"width: ")
            .append(width)
            .append("; height:")
            .append(height)
            .append(";\">")
            .append("<iframe src=\"")
            .append(url)
            .append("\" width=\"100%%\" height=\"100%\" frameborder=\"0\" allowfullscreen=\"\" aria-hidden=\"false\" tabindex=\"0\"></iframe>")
            .append("</div>\n")
        return sb.toString()
    }
}

