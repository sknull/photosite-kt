package de.visualdigits.photosite.domain.data.model.photosite

import de.visualdigits.photosite.domain.data.model.common.Language
import de.visualdigits.photosite.domain.data.model.page.Page
import de.visualdigits.photosite.domain.data.model.page.content.ContentType
import de.visualdigits.photosite.domain.data.model.plugin.Plugin
import de.visualdigits.photosite.domain.data.repository.DatabasePageRepository
import de.visualdigits.photosite.domain.data.repository.FilesystemPageRepository
import org.slf4j.LoggerFactory
import org.springframework.core.env.Environment
import java.io.File
import java.nio.file.Paths

data class Photosite(
    val serverPort: Int? = null,
    val internalPort: Int? = null,
    val internalEndpoints: List<String> = listOf(),
    val ssl: Ssl? = null,
    val theme: String = "default",
    val siteTitle: String? = null,
    val siteSubTitle: String? = null,
    val protocol: String? = null,
    val domain: String? = null,
    val languages: List<Language> = listOf(),
    val languageDefault: Language = Language("de"),
    val naviMain: NavigationEntry? = null,
    val naviSub: List<NavigationEntry> = listOf(),
    val naviStatic: NavigationEntry? = null,
    val plugins: Plugins? = null
) {

    companion object {

        private val log = LoggerFactory.getLogger(Photosite::class.java)

        val rootDirectory: File = File(System.getProperty("user.home"), ".photosite")
        var thumbnailCacheFolder: File = Paths.get(rootDirectory.canonicalPath, "resources", "thumbnails").toFile()

    }

    val pluginsMap: MutableMap<ContentType, Plugin> = mutableMapOf()

    var siteUrl: String? = null
    var pageTree: Page = Page()
    var mainTree: Page = Page()
    var subTrees: List<Pair<NavigationEntry, List<Page>>> = listOf()
    var staticTree: Page = Page()

    fun initialize(
        environment: Environment,
        databasePageRepository: DatabasePageRepository,
        filesystemPageRepository: FilesystemPageRepository
    ) {
        plugins?.plugins()?.forEach { p -> pluginsMap[p.contentType] = p }
        siteUrl = protocol + domain
        if (!environment.activeProfiles.contains("checkCerts")) {
            log.info("initializing page tree...")
            val readPageTreeFromDatabase = databasePageRepository.getPageTree()
            pageTree = readPageTreeFromDatabase ?: filesystemPageRepository.getPageTree()
            mainTree = pageTree.clone { p -> !(p.path.startsWith("#") || p.path.startsWith("-")) }
            subTrees = naviSub?.mapNotNull { n ->
                n.rootFolder?.let { rf ->
                    Pair(n, pageTree.page(rf, pageTree).lastModifiedPages(n.numberOfEntries) { p -> p.children.isEmpty() })
                }
            } ?: listOf()
            staticTree = pageTree.clone { p -> p.path.startsWith("-") }
            log.info("initialized page tree")
        } else {
            log.info("checkCerts profile is active - omitting pagetree initialization")
        }
    }
}
