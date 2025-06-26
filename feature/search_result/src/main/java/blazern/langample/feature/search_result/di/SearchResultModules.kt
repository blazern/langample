package blazern.langample.feature.search_result.di

import blazern.langample.data.chatgpt.di.chatgptModule
import blazern.langample.data.tatoeba.di.tatoebaModule
import blazern.langample.feature.search_result.SearchResultsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

fun searchResultModules() = listOf(
    tatoebaModule(),
    chatgptModule(),
    module {
        viewModel { (query: String) ->
            SearchResultsViewModel(
                startQuery = query,
                tatoebaClient = get(),
                chatGPTClient = get(),
            )
        }
    }
)