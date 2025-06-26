package blazern.langample.feature.search_result.llm

import blazern.langample.domain.model.TranslationsSet

data class LLMWordExplanation(
    val formsHtml: String,
    val explanation: String,
    val examples: List<TranslationsSet>,
)
