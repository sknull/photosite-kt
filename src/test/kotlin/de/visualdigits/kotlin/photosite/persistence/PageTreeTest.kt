package de.visualdigits.kotlin.photosite.persistence

import de.visualdigits.kotlin.photosite.persistence.model.Page
import de.visualdigits.kotlin.photosite.persistence.service.PageService
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension

@Disabled("only for local testing")
@ExtendWith(SpringExtension::class)
@SpringBootTest
class PageTreeTest @Autowired constructor(
    val pages: PageService
) {

    @Test
    fun clear() {
        pages.deleteAll()
    }

    @Test
    fun testCreatePageTree() {
        val childA = Page(
            name = "childA",
        )
        val childB = Page(
            name = "childB",
        )
        val rootPage = Page(
            name = "root",
        )
            .withChild(childA)
            .withChild(childB)

        pages.saveIfNotExists(rootPage)
    }
}
