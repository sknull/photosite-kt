package de.visualdigits.photosite.domain.data.repository

import de.visualdigits.photosite.domain.data.model.page.Page
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
@DisabledIfEnvironmentVariable(named = "CI", matches = "true")
class PageRepositoryTest @Autowired constructor(
    private val pageRepository: PageRepository
) {

    @Test
    fun readPageTreeToDatabase() {
        val rootPage = Page.readPagetreeFromFilesystem(pageRepository)
    }

    @Test
    fun readPageTreeFromDatabase() {
        // read all pages from database
        val rootPage = Page.readPageTreeFromDatabase(pageRepository)
        println(rootPage)
    }
}
