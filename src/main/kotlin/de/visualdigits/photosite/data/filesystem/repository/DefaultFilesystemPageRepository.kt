package de.visualdigits.photosite.data.filesystem.repository

import de.visualdigits.photosite.data.filesystem.mapper.toPage
import de.visualdigits.photosite.data.filesystem.model.PageDescriptor
import de.visualdigits.photosite.domain.data.model.photosite.Photosite.Companion.rootDirectory
import de.visualdigits.photosite.domain.data.model.page.Page
import de.visualdigits.photosite.domain.data.model.page.content.ContentType
import de.visualdigits.photosite.domain.data.model.page.content.ImageFile
import de.visualdigits.photosite.domain.data.repository.DatabasePageRepository
import de.visualdigits.photosite.domain.data.repository.FilesystemPageRepository
import de.visualdigits.photosite.domain.util.jsonMapper
import org.jetbrains.annotations.VisibleForTesting
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.io.File
import java.nio.file.Paths

@Service
class DefaultFilesystemPageRepository(
    private val databasePageRepository: DatabasePageRepository
) : FilesystemPageRepository {

    companion object {

        private val log = LoggerFactory.getLogger(DefaultFilesystemPageRepository::class.java)

        @VisibleForTesting
        fun readPageTree(directory: File, level: Int = 0, ariaName: String = "navigation"): Page {
            log.info("Initializing page '${"  ".repeat(level)}${directory.canonicalPath}'")

            val descriptorFile = File(directory, "page.json")
            val pageDescriptor = if (descriptorFile.exists()) {
                jsonMapper.decodeFromString<PageDescriptor>(descriptorFile.readText())
            } else {
                PageDescriptor()
            }

            val page = pageDescriptor.toPage()
            initialize(page, directory, level, ariaName)

            page.lastModified = page.allPages().maxOf { p -> p.content.lastModified }

            return page
        }

        fun initialize(
            page: Page,
            directory: File,
            level: Int,
            ariaName: String
        ) {
            page.directory = directory
            page.level = level
            page.ariaName = ariaName
            page.path = directory.name

            readContent(page, directory)
            readImages(page, directory)
            readChildren(page, directory, level, ariaName)

            page.content.calculateLastModified()
        }

        private fun readContent(
            page: Page,
            directory: File
        ) {
            val mdFile = File(directory, "page.md")
            if (mdFile.exists()) {
                page.content.contentType = ContentType.Markdown
                page.content.mdContent = mdFile.readText()
            }
            val htmlFile = File(directory, "page.html")
            if (htmlFile.exists()) {
                page.content.contentType = ContentType.Html
                page.content.htmlContent = htmlFile.readText()
            }
        }

        private fun readImages(
            page: Page,
            directory: File
        ) {
            page.content.images = directory.listFiles { file -> file.isFile && file.extension == "jpg" }
                ?.map { f ->
                    val image = ImageFile(file = f)
                    image.initiaslizeMetadata()
                    image
                }
                ?.toMutableList()
                ?: mutableListOf()
            page.content.sortImages()
        }

        private fun readChildren(
            page: Page,
            directory: File,
            level: Int,
            ariaName: String
        ) {
            page.children = directory.listFiles { file -> file.isDirectory }
                ?.mapIndexed { index, d ->
                    val child = readPageTree(d, level + 1, "${ariaName}_${index + 1}")
                    child.parent = page
                    child
                }
                ?.sortedBy { c -> c.path() }
                ?.toMutableList()
                ?: mutableListOf()
            if (page.children.isNotEmpty()) {
                page.icon = "folder"
            }
        }
    }

    override fun getPageTree(): Page {
        val rootPage = readPageTree(Paths.get(rootDirectory.canonicalPath, "resources", "pagetree").toFile())
        databasePageRepository.addPageTree(rootPage)

        return rootPage
    }
}
