package de.visualdigits.kotlin.photosite.model.siteconfig

import de.visualdigits.kotlin.photosite.model.common.Link
import de.visualdigits.kotlin.photosite.model.siteconfig.navi.PageTree
import de.visualdigits.kotlin.photosite.model.siteconfig.plugin.Plugin
import de.visualdigits.kotlin.photosite.util.DomainCertificatesHelper
import de.visualdigits.kotlin.photosite.util.ProfileHelper
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

    @Autowired
    lateinit var profileHelper: ProfileHelper

    var siteConfig: SiteConfig? = null
    var site: Site? = null
    var siteUrl: String? = null
    var pageDirectory: File? = null
    var pageTree: PageTree? = null

    @PostConstruct
    fun postConstruct() {
        val rootFolder = rootDirectory.substring("file:".length)
        siteConfig = SiteConfig.load(Paths.get(rootFolder, "resources", "config.xml").toFile())
        siteConfig?.site?.rootFolder = rootFolder

        site = siteConfig?.site
        siteUrl = site?.protocol + site?.domain
        pageDirectory = site?.rootFolder?.let { Paths.get(it, "resources", "pagetree").toFile() }
        if (!profileHelper.isProfileActive("checkCerts")) {
            reloadPageTree()
        } else {
            log.info("#### checkCerts profile is active - omitting pagetree initialization")
        }
    }

    fun reloadPageTree() {
        log.info("#### initializing page tree...")
        pageTree = PageTree(
            pageDirectory = pageDirectory,
            dump = true
        )
        log.info("#### initialized page tree")
    }

    fun maintainServerCertificate(forceUpdate: Boolean): LocalDateTime {
        val alias = "springboot"
        val password = "foodlyboo"
        val rootFolder = site?.rootFolder?.let { File(it) }
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
}
