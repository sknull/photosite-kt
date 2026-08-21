package de.visualdigits.photosite

import android.app.Application
import co.touchlab.kermit.Logger
import de.visualdigits.photosite.di.platformModule
import de.visualdigits.photosite.di.sharedModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class PhotositeApplication: Application() {

    override fun onCreate() {
        Logger.i("Starting koin...")
        startKoin {
            androidContext(this@PhotositeApplication)
            modules(sharedModule, platformModule)
        }

        // IMPORTANT do super create AFTER koin initializing to avoid problems with work managers
        Logger.i("Initializing application...")
        super.onCreate()

        Logger.i("Application initialized")
    }
}
