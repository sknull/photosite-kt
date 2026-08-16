package de.visualdigits.photosite.domain.data.util

import kotlinx.serialization.encodeToString
import nl.adaptivity.xmlutil.XmlDeclMode
import nl.adaptivity.xmlutil.core.XmlVersion
import nl.adaptivity.xmlutil.serialization.XML
import java.io.File

inline fun <reified T> T.writeValueAsXmlString(
    indent: Boolean = true,
    writeXmlDeclaration: Boolean = true,
    expandSelfClosingTags: Boolean = false
): String {
    val xmlConfig = XML {
        xmlDeclMode = if (writeXmlDeclaration) XmlDeclMode.Charset else XmlDeclMode.None
        xmlVersion = XmlVersion.XML10
        indentString = if (indent) "  " else ""
    }

    val rawXml = xmlConfig.encodeToString(this)

    val finalXml = if (expandSelfClosingTags) {
        rawXml.replace(Regex("""<([\w:]+)([^>]*)\s*/>""")) { match ->
            val tagName = match.groupValues[1]
            val attributes = match.groupValues[2]
            "<$tagName$attributes></$tagName>"
        }
    } else {
        rawXml
    }

    return finalXml.replace("\n", "\r\n")
}

inline fun <reified T> T.writeValueAsXmlFile(
    file: File,
    indent: Boolean = true,
    writeXmlDeclaration: Boolean = true,
    expandSelfClosingTags: Boolean = false
) {
    file.writeText(writeValueAsXmlString(indent, writeXmlDeclaration, expandSelfClosingTags))
}
