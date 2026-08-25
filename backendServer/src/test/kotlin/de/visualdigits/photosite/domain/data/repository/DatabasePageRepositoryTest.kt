package de.visualdigits.photosite.domain.data.repository

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
@DisabledIfEnvironmentVariable(named = "CI", matches = "true")
class DatabasePageRepositoryTest @Autowired constructor(
    private val databasePageRepository: DatabasePageRepository
) {

    @Test
    fun readPages() {
        val page = databasePageRepository.getPageTree()
        println(page)
    }
}
