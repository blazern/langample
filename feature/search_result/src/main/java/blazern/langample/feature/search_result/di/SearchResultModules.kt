package blazern.langample.feature.search_result.di

import blazern.langample.data.tatoeba.di.tatoebaModule
import blazern.langample.feature.search_result.SearchResultsViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

fun searchResultModules() = listOf(
    tatoebaModule(),
    module {
        viewModelOf(::SearchResultsViewModel)
    }
)