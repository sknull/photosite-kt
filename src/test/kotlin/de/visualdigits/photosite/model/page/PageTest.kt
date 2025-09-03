package de.visualdigits.photosite.model.page

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.module.kotlin.jacksonMapperBuilder
import com.fasterxml.jackson.module.kotlin.kotlinModule
import de.visualdigits.photosite.model.pagemodern.ContentType
import de.visualdigits.photosite.model.pagemodern.ImageFile
import de.visualdigits.photosite.model.pagemodern.Page
import org.junit.jupiter.api.Test
import java.io.File
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneOffset

class PageTest {

    private val jsonMapper = jacksonMapperBuilder()
        .addModule(kotlinModule())
        .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
        .enable(SerializationFeature.INDENT_OUTPUT)
        .build()
        .setSerializationInclusion(JsonInclude.Include.NON_EMPTY)

    @Test
    fun testConvertDescriptor() {
//        val tree = Page.readValue(File("W:/"))
        val tree = Page.readValue(File("C:/Users/sknul/.photosite/resources/pagetree"))
        val mainTree = tree.clone { p -> !(p.name.startsWith("#") || p.name.startsWith("-")) }
        val staticTree = tree.clone { p -> p.name.startsWith("-") }
        println(staticTree)
    }

}
