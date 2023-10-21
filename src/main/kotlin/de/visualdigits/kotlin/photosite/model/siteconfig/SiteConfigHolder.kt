package de.visualdigits.kotlin.photosite.model.siteconfig

import de.visualdigits.kotlin.photosite.model.siteconfig.navi.PageTree
import de.visualdigits.kotlin.photosite.util.DomainCertificatesHelper
import jakarta.annotation.PostConstruct
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.env.Environment
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
    lateinit var envvironment: Environment

    var siteConfig: SiteConfig? = null
    var siteUrl: String? = null
    var pageDirectory: File? = null
    var pageTree: PageTree? = null

    @PostConstruct
    fun postConstruct() {
        val rootFolder = File(rootDirectory.substring("file:".length)).canonicalPath
        siteConfig = SiteConfig.load(Paths.get(rootFolder, "resources", "config.xml").toFile())
        siteConfig?.site?.rootFolder = rootFolder

        siteUrl = siteConfig?.site?.protocol + siteConfig?.site?.domain
        pageDirectory = siteConfig?.site?.rootFolder?.let { Paths.get(it, "resources", "pagetree").toFile() }
        if (!isProfileActive("checkCerts")) {
            reloadPageTree()
        } else {
            log.info("#### checkCerts profile is active - omitting pagetree initialization")
        }
    }

    fun isProfileActive(profile: String): Boolean {
        return envvironment.activeProfiles.contains(profile)
    }

    fun reloadPageTree() {
        log.info("#### initializing page tree...")
        pageTree = PageTree(
            pageDirectory = pageDirectory,
            nameFilter = { name -> "pagetree" == name || (!name.startsWith("#") && !name.startsWith("-")) },
            dump = true
        )
        log.info("#### initialized page tree")
    }

    fun maintainServerCertificate(forceUpdate: Boolean): LocalDateTime {
        val alias = "springboot"
        val password = "foodlyboo"
        val rootFolder = siteConfig?.site?.rootFolder?.let { File(it) }
        val targetKeystore = File(rootFolder, "keystore.p12")
        var expiryDate: LocalDateTime = DomainCertificatesHelper.determineExpiryDate(targetKeystore, alias, password)
        val updateDate = expiryDate.minus(30, ChronoUnit.DAYS)
        if (forceUpdate || LocalDateTime.now().isAfter(updateDate)) {
            log.info("Server certificate will expire at $expiryDate days - updating now...")
            DomainCertificatesHelper.createCertificates(
                siteConfig!!,
                certbotUri,
                2048,
                rootFolder, listOf(siteConfig?.site?.domain),
                alias,
                password,
                targetKeystore
            )
            expiryDate = DomainCertificatesHelper.determineExpiryDate(targetKeystore, alias, password)
            log.info("Successfully updated server certificate, new certificate will be valid until $expiryDate")
        } else {
            log.info("Server certificate is valid until $expiryDate")
        }
        return expiryDate
    }
}
