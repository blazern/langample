package blazern.langample.feature.home.di

import blazern.langample.feature.home.ui.HomeScreenViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

fun homeScreenModule() = module {
    viewModel { (query: String) ->
        HomeScreenViewModel(
            query = query,
            settings = get(),
        )
    }
}
