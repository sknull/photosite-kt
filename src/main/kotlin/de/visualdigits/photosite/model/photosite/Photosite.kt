package de.visualdigits.photosite.model.photosite

import de.visualdigits.photosite.model.navi.NaviName
import de.visualdigits.photosite.model.page.Page
import de.visualdigits.photosite.model.page.content.ContentType
import de.visualdigits.photosite.model.plugin.Plugin
import de.visualdigits.photosite.model.plugin.Plugins
import jakarta.annotation.PostConstruct
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import java.io.File
import java.nio.file.Paths
import java.util.Locale

@Configuration
@ConfigurationProperties(prefix = "photosite")
@ConfigurationPropertiesScan
class Photosite(
    var serverPort: Int? = null,
    var internalPort: Int? = null,
    var internalEndpoints: List<String> = listOf(),
    var ssl: Ssl? = null,
    var theme: String = "default",
    var siteTitle: String? = null,
    var siteSubTitle: String? = null,
    var protocol: String? = null,
    var domain: String? = null,
    var languages: List<Locale> = listOf(),
    var languageDefault: Locale = Locale.GERMAN,
    var naviMain: NaviName? = null,
    var naviSub: List<NaviName>? = null,
    var naviStatic: NaviName? = null,
    var plugins: Plugins? = null
) {

    companion object {

        private val log = LoggerFactory.getLogger(Photosite::class.java)

        val rootDirectory: File = File(System.getProperty("user.home"), ".photosite")
        var thumbnailCacheFolder: File = Paths.get(rootDirectory.canonicalPath, "resources", "thumbnails").toFile()

        fun getRelativeResourcePath(resource: File): String? {
            return try {
                rootDirectory.toPath()
                    .relativize(Paths.get(resource.canonicalPath))
                    .toString()
                    .replace("\\", "/")
            } catch (e: Exception) {
                log.error("Could not determine relative path for resource '$resource'", e)
                null
            }
        }
    }

    val pluginsMap: MutableMap<ContentType, Plugin> = mutableMapOf()

    @Autowired
    private lateinit var envvironment: Environment

    var siteUrl: String? = null
    var pageTree: Page = Page()
    var mainTree: Page = Page()
    var subTrees: List<Pair<NaviName, List<Page>>> = listOf()
    var staticTree: Page = Page()

    @PostConstruct
    fun initialize() {
        plugins?.plugins()?.forEach { p -> pluginsMap[p.contentType] = p }
        siteUrl = protocol + domain
        if (!isProfileActive("checkCerts")) {
            reloadPageTree()
        } else {
            log.info("checkCerts profile is active - omitting pagetree initialization")
        }
    }

    fun isProfileActive(profile: String): Boolean {
        return envvironment.activeProfiles.contains(profile)
    }

    fun reloadPageTree() {
        log.info("initializing page tree...")
        pageTree = Page.readValue(Paths.get(rootDirectory.canonicalPath, "resources", "pagetree").toFile())
        mainTree = pageTree.clone { p -> !(p.name.startsWith("#") || p.name.startsWith("-")) }
        subTrees = naviSub?.mapNotNull { n -> n.rootFolder?.let { rf -> Pair(n,pageTree.page(rf)?.lastModifiedPages(n.numberOfEntries) { p -> p.children.isEmpty() } ?:error("No sub navigation '${n.rootFolder}'")) } }?:listOf()
        staticTree = pageTree.clone { p -> p.name.startsWith("-") }
        log.info("initialized page tree")
    }
}

