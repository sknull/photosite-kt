package de.visualdigits.photosite.di

import de.visualdigits.photosite.data.database.DriverFactory
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp
import org.koin.android.ext.koin.androidApplication
import org.koin.core.module.Module
import org.koin.dsl.module

actual val homeDirectory: String
    get() = ""

actual val platformModule: Module
    get() = module {
        single<HttpClientEngine> { OkHttp.create() }
        single { DriverFactory(androidApplication()) }
}
