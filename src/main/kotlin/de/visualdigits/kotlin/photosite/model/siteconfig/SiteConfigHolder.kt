package de.visualdigits.kotlin.photosite.model.siteconfig

import de.visualdigits.kotlin.photosite.model.common.Link
import de.visualdigits.kotlin.photosite.model.siteconfig.plugin.Plugin
import de.visualdigits.kotlin.photosite.util.DomainCertificatesHelper
import jakarta.annotation.PostConstruct
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.io.File
import java.nio.file.Paths
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

@Component
class SiteConfigHolder {

    private val log = LoggerFactory.getLogger(SiteConfigHolder::class.java)

    @Value("\${spring.resources.static-locations}")
    lateinit var rootDirectory: String

    @Value("\${certbot.uri}")
    lateinit var certbotUri: String

    @Autowired
    lateinit var domainCertificatesHelper: DomainCertificatesHelper

    var siteConfig: SiteConfig? = null

    @PostConstruct
    fun postConstruct() {
        val rootFolder = rootDirectory.substring("file:".length)
        siteConfig = SiteConfig.load(Paths.get(rootFolder, "resources", "config.xml").toFile())
        siteConfig?.site?.rootFolder = rootFolder
    }

    fun maintainServerCertificate(forceUpdate: Boolean): LocalDateTime {
        val alias = "springboot"
        val password = "foodlyboo"
        val rootFolder = getSite().rootFolder?.let { File(it) }
        val targetKeystore = File(rootFolder, "keystore.p12")
        var expiryDate: LocalDateTime = domainCertificatesHelper.determineExpiryDate(targetKeystore, alias, password)
        val updateDate = expiryDate.minus(30, ChronoUnit.DAYS)
        if (forceUpdate || LocalDateTime.now().isAfter(updateDate)) {
            log.info("Server certificate will expire at $expiryDate days - updating now...")
            domainCertificatesHelper.createCertificates(
                this,
                certbotUri,
                2048,
                rootFolder, listOf(siteConfig?.site?.domain),
                alias,
                password,
                targetKeystore
            )
            expiryDate = domainCertificatesHelper.determineExpiryDate(targetKeystore, alias, password)
            log.info("Successfully updated server certificate, new certificate will be valid until $expiryDate")
        } else {
            log.info("Server certificate is valid until $expiryDate")
        }
        return expiryDate
    }

    fun setSite(site: Site) {
        siteConfig?.site = site
    }

    fun setSiteLinks(siteLinks: List<Link>) {
        siteConfig?.siteLinks?.clear()
        siteConfig?.siteLinks?.addAll(siteLinks)
    }

    fun setPlugins(plugins: List<Plugin>) {
        siteConfig?.plugins?.clear()
        siteConfig?.plugins?.addAll(plugins)
    }

    fun setPluginsMap(pluginsMap: Map<String, Plugin>) {
        siteConfig?.pluginsMap?.clear()
        siteConfig?.pluginsMap?.putAll(pluginsMap)
    }

    fun getSite(): Site {
        return siteConfig?.site!!
    }

    fun getSiteLinks(): List<Link> {
        return siteConfig?.siteLinks?:listOf()
    }

    fun getPlugins(): List<Plugin> {
        return siteConfig?.plugins?:listOf()
    }

    fun getPluginsMap(): Map<String, Plugin> {
        return siteConfig?.pluginsMap?:mapOf()
    }

    fun getPluginConfig(pluginName: String): Plugin? {
        return siteConfig?.getPluginConfig(pluginName)
    }

    fun getRelativeResourcePath(absoluteResource: File): String? {
        return siteConfig?.getRelativeResourcePath(absoluteResource)
    }

    fun getAbsoluteResource(resourceFolder: String, relativeResourePath: String): File? {
        return siteConfig?.getAbsoluteResource(resourceFolder, relativeResourePath)
    }
}
