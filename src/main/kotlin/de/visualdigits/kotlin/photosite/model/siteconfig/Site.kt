package de.visualdigits.kotlin.photosite.model.siteconfig

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import de.visualdigits.kotlin.photosite.model.siteconfig.navi.NaviName

class Site(
    @JacksonXmlProperty(isAttribute = true) val theme: String? = null,
    @JacksonXmlProperty(isAttribute = true) val siteTitle: String? = null,
    @JacksonXmlProperty(isAttribute = true) val siteSubTitle: String? = null,
    @JacksonXmlProperty(isAttribute = true) val protocol: String? = null,
    @JacksonXmlProperty(isAttribute = true) val domain: String? = null,
    @JacksonXmlProperty(isAttribute = true) val resourcesRoot: String? = null,
    @JacksonXmlProperty(isAttribute = true) var rootFolder: String? = null,
    @JacksonXmlProperty(isAttribute = true) val languages: String? = null,
    @JacksonXmlProperty(isAttribute = true) val languageDefault: String? = null,
    @JacksonXmlProperty(isAttribute = true) val thumbnailCacheFolder: String? = null,
    @JacksonXmlProperty(localName = "naviMainName") val naviMain: NaviName? = null,
    val naviSub: List<NaviName>? = null
)
