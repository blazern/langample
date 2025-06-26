package blazern.langample.feature.search_result

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import blazern.langample.data.chatgpt.ChatGPTClient
import blazern.langample.data.tatoeba.TatoebaClient
import blazern.langample.domain.model.Sentence
import blazern.langample.domain.model.TranslationsSet
import kotlinx.coroutines.launch

class SearchResultsViewModel(
    private val tatoebaClient: TatoebaClient,
    private val chatGPTClient: ChatGPTClient,
) : ViewModel() {
    private val _state = mutableStateOf<SearchResultsState>(SearchResultsState.PerformingSearch)
    val state: State<SearchResultsState> = _state

    fun search(query: String) {
        val langFrom = Lang.RU
        val langTo = Lang.DE
        viewModelScope.launch {
            // TODO: create a usecase?
            var examples = tatoebaClient.search(query, langFrom, langTo)

            val request = """
                you are called from a language learning app
                generate 1 sentence example with the word $query
                your output must follow next format:
                <sentence with the word in language: $langFrom> ||| <translation of the first sentence into $langTo>
            """.trimIndent()

            val chatGptResponse = chatGPTClient.request(request)
            examples = examples + listOf(
                TranslationsSet(
                    original = Sentence(
                        chatGptResponse.split("|||").first(),
                        langFrom,
                    ),
                    translations = listOf(Sentence(
                        chatGptResponse.split("|||").last(),
                        langTo,
                    ))
                )
            )

            _state.value = SearchResultsState.Results(
                explanation = query,
                examples = examples,
            )
        }
    }
}