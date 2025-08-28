package de.visualdigits.photosite.model.siteconfig

import de.visualdigits.photosite.model.siteconfig.navi.NaviName
import de.visualdigits.photosite.model.siteconfig.navi.PageTree
import de.visualdigits.photosite.model.siteconfig.plugin.Plugin
import de.visualdigits.photosite.model.siteconfig.plugin.Plugins
import jakarta.annotation.PostConstruct
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import java.io.File
import java.nio.file.Paths

@Configuration
@ConfigurationProperties(prefix = "photosite")
@ConfigurationPropertiesScan
class Photosite(
    var theme: String = "default",
    var siteTitle: String? = null,
    var siteSubTitle: String? = null,
    var protocol: String? = null,
    var domain: String? = null,
    var languages: List<String> = listOf(),
    var languageDefault: String = "de",
    var naviMain: NaviName? = null,
    var naviSub: List<NaviName>? = null,
    var plugins: Plugins? = null
) {

    private val log = LoggerFactory.getLogger(javaClass)

    val rootDirectory: File = File(System.getProperty("user.home"), ".photosite")
    var thumbnailCacheFolder: File = Paths.get(rootDirectory.canonicalPath, "resources", "thumbnails").toFile()

    private val pluginsMap: MutableMap<String, Plugin> = mutableMapOf()

    @Autowired
    private lateinit var envvironment: Environment

    var siteUrl: String? = null
    var pageTree: PageTree = PageTree()

    @PostConstruct
    fun initialize() {
        plugins?.plugins()?.forEach { p -> pluginsMap[p.name] = p }
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
        pageTree = PageTree(
            pageDirectory = Paths.get(rootDirectory.canonicalPath, "resources", "pagetree").toFile(),
            nameFilter = { name -> "pagetree" == name || (!name.startsWith("#") && !name.startsWith("-")) },
            dump = true
        )
        log.info("initialized page tree")
    }

    fun getPluginConfig(pluginName: String): Plugin? {
        return pluginsMap[pluginName]
    }

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

