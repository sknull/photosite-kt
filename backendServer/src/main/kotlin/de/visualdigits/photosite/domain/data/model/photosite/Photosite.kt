package de.visualdigits.photosite.domain.data.model.photosite

import de.visualdigits.photosite.data.database.mapper.toPageDto
import de.visualdigits.photosite.domain.data.model.common.Language
import de.visualdigits.photosite.domain.data.model.page.Page
import de.visualdigits.photosite.domain.data.model.page.content.ContentType
import de.visualdigits.photosite.domain.data.model.plugin.Plugin
import de.visualdigits.photosite.domain.data.repository.DatabasePageRepository
import de.visualdigits.photosite.domain.data.repository.FilesystemPageRepository
import de.visualdigits.photosite.presentation.model.PageDto
import org.slf4j.LoggerFactory
import org.springframework.core.env.Environment
import java.io.File
import java.nio.file.Paths
import java.time.OffsetDateTime

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
    val languages: List<Language>,
    val languageDefault: Language = Language("de"),
    val naviMain: NavigationEntry,
    val naviSub: List<NavigationEntry>,
    val naviStatic: NavigationEntry,
    val plugins: Plugins? = null
) {

    companion object {

        private val log = LoggerFactory.getLogger(Photosite::class.java)

        val rootDirectory: File = File(System.getProperty("user.home"), ".photosite")
        var thumbnailCacheFolder: File = Paths.get(rootDirectory.canonicalPath, "resources", "thumbnails").toFile()

    }

    val pluginsMap: MutableMap<ContentType, Plugin> = mutableMapOf()

    var siteUrl: String? = null
    var pageMap: Map<String, Page> = mapOf()
    var pageTree: Page = Page()
    var mainTree: Page = Page()
    var subTrees: List<Page> = listOf()
    var staticTree: Page = Page()

    val mainTreeDto: PageDto
        get() = mainTree.toPageDto()

    val staticTreeDto: PageDto
        get() = staticTree.toPageDto()

    val subTreeDtos: PageDto
        get() = mainTree.toPageDto()

    fun initialize(
        environment: Environment,
        databasePageRepository: DatabasePageRepository,
        filesystemPageRepository: FilesystemPageRepository
    ) {
        plugins?.plugins()?.forEach { p -> pluginsMap[p.contentType] = p }
        siteUrl = protocol + domain
        if (!environment.activeProfiles.contains("checkCerts")) {
            log.info("initializing page tree...")

            val databasePageTree = databasePageRepository.getPageTree()
            pageTree = if (databasePageTree != null){
                databasePageTree
            } else {
                val filesystemPageTree = filesystemPageRepository.getPageTree()
                databasePageRepository.addPageTree(filesystemPageTree)
                filesystemPageTree
            }
            pageMap = calculatePageMap(pageTree)

            mainTree = pageTree.copy(
                translations = naviMain.translations,
                children = pageTree.children.filter { p -> !(p.name.startsWith("#") || p.name.startsWith("-")) }.toMutableList()
            )
            subTrees = naviSub.mapNotNull { navigationEntry ->
                navigationEntry.rootFolder?.let { rf ->
                    val page = pageMap[rf]
                    page?.copy(
                        translations = navigationEntry.translations,
                        children = lastModifiedPages(
                            page = page,
                            count = navigationEntry.numberOfEntries,
                            filter = { p -> p.children.isEmpty() }
                        ).toMutableList())
                }
            }
            staticTree = Page(
                translations = naviStatic.translations ?: listOf(),
                children = pageTree.children.filter { p -> p.name.startsWith("-") }.toMutableList()
            )
            log.info("initialized page tree")
        } else {
            log.info("checkCerts profile is active - omitting pagetree initialization")
        }
    }

    private fun calculatePageMap(page: Page, pageMap: MutableMap<String, Page> = mutableMapOf()): Map<String, Page> {
        pageMap[page.path] = page
        page.children.forEach { child -> calculatePageMap(child, pageMap) }

        return pageMap
    }

    fun lastModifiedPages(page: Page = pageTree, count: Int? = null, filter: ((Page) -> Boolean)? = null): List<Page> {
        val pages = pageMap.values
            .filter { it.path.startsWith(page.path) && (filter == null || filter(it)) }
            .sortedByDescending { it.lastModified ?: OffsetDateTime.MIN }
        return if (count != null) {
            pages.take(count)
        } else {
            pages
        }
    }
}
