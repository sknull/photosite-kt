package de.visualdigits.photosite.di

import de.visualdigits.photosite.PhotositeQueries
import de.visualdigits.photosite.SettingsDatabase
import de.visualdigits.photosite.data.database.DriverFactory
import de.visualdigits.photosite.data.database.PageDatasource
import de.visualdigits.photosite.data.http.HttpClientFactory
import de.visualdigits.photosite.data.repository.TestSettingsRepository
import de.visualdigits.photosite.domain.repository.SettingsRepository
import de.visualdigits.photosite.presentation.model.PhotositeViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module
import java.io.File

val testHomeDirectory = File("E:\\temp\\.planespottersfriend")

val testModule = module {

    single(named("homeDirectory")) { testHomeDirectory }

    singleOf(::PhotositeViewModel)

    single {
        val driver = get<DriverFactory>().createDriver(get<String>(named("homeDirectory")))
        SettingsDatabase(driver)
    }
    single<PhotositeQueries> {
        get<SettingsDatabase>().photositeQueries
    }

    single { HttpClientFactory.create(get()) }

    singleOf(::TestSettingsRepository).bind<SettingsRepository>()
    singleOf(::PageDatasource)
}
