package de.visualdigits.photosite.data.database

import de.visualdigits.common.domain.model.errorhandling.Result
import de.visualdigits.photosite.di.platformModule
import de.visualdigits.photosite.di.testModule
import de.visualdigits.photosite.domain.repository.SettingsRepository
import de.visualdigits.photosite.presentation.model.settings.SK
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.koin.test.KoinTest
import org.koin.test.inject
import org.koin.test.junit5.KoinTestExtension

class PageDatasourceTest : KoinTest {

    private val pogeDatasource: PageDatasource by inject()
    private val settingsRepository: SettingsRepository by inject()

    @JvmField
    @RegisterExtension
    val koinTestExtension = KoinTestExtension.create {
        modules(testModule, platformModule)
    }

    @Test
    fun testReadPageTree() = runBlocking {
        val settingsResult = settingsRepository.getSettings()
        if (settingsResult is Result.Success) {
            val settings = settingsResult.data
            settings?.get<String>(SK.hostUrl)?.also { hostUrl ->
                val pages = pogeDatasource.getPageTree(hostUrl)
                println(pages)
            }
        }
    }
}
