package de.visualdigits.kotlin.photosite.model.siteconfig.navi

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import de.visualdigits.kotlin.photosite.model.common.Label

class NaviName(
    @JacksonXmlProperty(isAttribute = true) var rootFolder: String? = null,
    @JacksonXmlProperty(isAttribute = true) val numberOfEntries: Int = 0,
    val label: Label? = null
)
