package blazern.langample

import android.app.Application
import blazern.langample.core.ktor.di.ktorModule
import blazern.langample.data.lexical_item_details_source.aggregation.di.aggregatingLexicalItemDetailsSourceModules
import blazern.langample.domain.settings.di.settingsModule
import blazern.langample.feature.home.di.homeScreenModule
import blazern.langample.feature.search_result.di.searchResultModules
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            modules(
                ktorModule(),
                settingsModule(),
                homeScreenModule(),
                *searchResultModules().toTypedArray(),
                *aggregatingLexicalItemDetailsSourceModules().toTypedArray(),
            )
        }
    }
}
