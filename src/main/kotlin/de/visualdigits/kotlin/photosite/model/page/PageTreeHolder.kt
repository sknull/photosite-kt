package de.visualdigits.kotlin.photosite.model.page

import de.visualdigits.kotlin.photosite.model.siteconfig.Site
import de.visualdigits.kotlin.photosite.model.siteconfig.SiteConfigHolder
import de.visualdigits.kotlin.photosite.model.siteconfig.navi.PageTree
import de.visualdigits.kotlin.photosite.util.ProfileHelper
import jakarta.annotation.PostConstruct
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.io.File
import java.nio.file.Paths

@Component
class PageTreeHolder {

    private val log = LoggerFactory.getLogger(PageTreeHolder::class.java)

    @Autowired
    lateinit var siteConfig: SiteConfigHolder

    @Autowired
    lateinit var pageFactory: PageFactory

    @Autowired
    lateinit var profileHelper: ProfileHelper


    var site: Site? = null
    var siteUrl: String? = null
    var pageDirectory: File? = null
    var pageTree: PageTree? = null

    @PostConstruct
    fun postConstruct() {
        site = siteConfig.getSite()
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
        pageFactory.clearPageCache()
        pageTree = PageTree(
            pageFactory = pageFactory,
            pageDirectory = pageDirectory,
            dump = true
        )
        log.info("#### initialized page tree")
    }
}
