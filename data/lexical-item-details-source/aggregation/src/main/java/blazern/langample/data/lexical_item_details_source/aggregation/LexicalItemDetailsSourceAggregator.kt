package blazern.langample.data.lexical_item_details_source.aggregation

import blazern.langample.data.lexical_item_details_source.api.LexicalItemDetailsSource
import blazern.langample.domain.model.Lang
import blazern.langample.model.lexical_item_details_source.utils.examples_tools.FormsAccentsEnhancerProvider

class LexicalItemDetailsSourceAggregator(
    private val dataSources: List<LexicalItemDetailsSource>,
    private val accentsEnhancerProvider: FormsAccentsEnhancerProvider,
) {
    fun sourceFor(
        query: String,
        langFrom: Lang,
        langTo: Lang,
    ) = LexicalItemDetailsSourceAggregatorForQuery(
        query,
        langFrom,
        langTo,
        accentsEnhancerProvider,
        dataSources,
    )
}
