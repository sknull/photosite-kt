package de.visualdigits.photosite.model.photosite

import de.visualdigits.photosite.model.page.Page
import de.visualdigits.photosite.model.photosite.Photosite.Companion.rootDirectory
import org.junit.jupiter.api.Test
import java.nio.file.Paths

class PhotositeTest {

    @Test
    fun pageTreeTest() {
        val pageTree = Page.readValue(Paths.get(rootDirectory.canonicalPath, "resources", "pagetree").toFile())

        println(pageTree)
    }
}