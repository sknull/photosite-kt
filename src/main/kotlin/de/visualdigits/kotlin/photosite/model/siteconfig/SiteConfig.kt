package de.visualdigits.kotlin.photosite.model.siteconfig

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.module.kotlin.kotlinModule
import de.visualdigits.kotlin.photosite.model.common.Link
import de.visualdigits.kotlin.photosite.model.siteconfig.plugin.Plugin
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths

@JsonIgnoreProperties("pluginsMap")
class SiteConfig(
    var site: Site,
    val plugins: MutableList<Plugin> = mutableListOf(),
    @JacksonXmlProperty(localName = "sitelinks") val siteLinks: MutableList<Link> = mutableListOf()
) {
    val pluginsMap: MutableMap<String, Plugin> = mutableMapOf()

    companion object {
        val MAPPER = XmlMapper.builder()
            .addModule(kotlinModule())
            .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
            .build()

        fun load(resource: File): SiteConfig {
            MAPPER.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true)
            try {
                Files.newInputStream(resource.toPath()).use { ins ->
                    return MAPPER.readValue(
                        ins,
                        SiteConfig::class.java
                    )
                }
            } catch (e: IOException) {
                throw IllegalArgumentException("Could not parse config resource: $resource", e)
            }
        }
    }

    init {
        plugins.forEach { pluginsMap[it.name] = it }
    }

    fun getPluginConfig(pluginName: String): Plugin? {
        return pluginsMap[pluginName]
    }

    fun getRelativeResourcePath(absoluteResource: File): String? {
        return site.rootFolder?.let {
            Paths
                .get(it)
                .relativize(Paths.get(absoluteResource.absolutePath))
                .toString()
                .replace("\\", "/")
        }
    }

    fun getAbsoluteResource(resourceFolder: String, relativeResourePath: String): File? {
        return site.rootFolder?.let { Paths.get(it, resourceFolder, relativeResourePath).toFile() }
    }
}
