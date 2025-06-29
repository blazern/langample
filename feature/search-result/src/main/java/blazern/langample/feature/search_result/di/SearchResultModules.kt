package blazern.langample.feature.search_result.di

import blazern.langample.domain.model.Lang
import blazern.langample.feature.search_result.SearchResultsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

fun searchResultModules() = listOf(
    module {
        viewModel { (query: String, langFrom: Lang, langTo: Lang) ->
            SearchResultsViewModel(
                startQuery = query,
                langFrom = langFrom,
                langTo = langTo,
                dataSources = getAll(),
            )
        }
    }
)