package blazern.langample.feature.search_result.di

import blazern.langample.data.chatgpt.di.chatgptModule
import blazern.langample.data.tatoeba.di.tatoebaModule
import blazern.langample.domain.model.Lang
import blazern.langample.feature.search_result.SearchResultsViewModel
import blazern.langample.feature.search_result.usecase.ChatGPTWordSearchUseCase
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

fun searchResultModules() = listOf(
    tatoebaModule(),
    chatgptModule(),
    module {
        factory { ChatGPTWordSearchUseCase(chatGPTClient = get()) }
        viewModel { (query: String, langFrom: Lang, langTo: Lang) ->
            SearchResultsViewModel(
                startQuery = query,
                langFrom = langFrom,
                langTo = langTo,
                tatoebaClient = get(),
                chatGPTUseCase = get(),
            )
        }
    }
)