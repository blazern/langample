package blazern.langample.feature.search_result.model

import androidx.compose.runtime.Immutable
import blazern.langample.domain.model.DataSource
import blazern.langample.domain.model.LexicalItemDetail
import blazern.langample.domain.model.LexicalItemDetail.Example
import blazern.langample.domain.model.LexicalItemDetail.Explanation
import blazern.langample.domain.model.LexicalItemDetail.Forms
import kotlin.reflect.KClass

@Immutable
internal data class SearchResultsState(
    val forms: List<LexicalItemDetailState<Forms>> = emptyList(),
    val explanations: List<LexicalItemDetailState<Explanation>> = emptyList(),
    val examples: List<LexicalItemDetailState<Example>> = emptyList(),
)

internal fun SearchResultsState.add(
    clazz: KClass<out LexicalItemDetail>,
    new: LexicalItemDetailState<*>,
): SearchResultsState {
    return copyWithNewDetails(
        clazz,
        detailsOfType(clazz) + new
    )
}

internal fun SearchResultsState.remove(
    classes: List<KClass<out LexicalItemDetailState<*>>>,
    source: DataSource,
): SearchResultsState {
    var result = this
    classes.forEach {
        result = result.remove(it, source)
    }
    return result
}

internal fun SearchResultsState.remove(
    clazz: KClass<out LexicalItemDetailState<*>>,
    source: DataSource,
): SearchResultsState {
    return copy(
        forms = forms.filterNot { it.javaClass == clazz.java && it.source == source },
        explanations = explanations.filterNot { it.javaClass == clazz.java && it.source == source },
        examples = examples.filterNot { it.javaClass == clazz.java && it.source == source },
    )
}

internal fun SearchResultsState.replaceWithError(
    source: DataSource,
    classes: List<KClass<out LexicalItemDetailState<*>>>,
    new: () -> LexicalItemDetailState.Error<*>,
) = replaceManyImpl(source, classes, new)

private fun SearchResultsState.replaceManyImpl(
    source: DataSource,
    classes: List<KClass<out LexicalItemDetailState<*>>>,
    new: () -> LexicalItemDetailState<*>,
): SearchResultsState {
    var result = this
    classes.forEach {
        result = result.replace(source, it, new)
    }
    return result
}

private fun SearchResultsState.replace(
    source: DataSource,
    clazz: KClass<out LexicalItemDetailState<*>>,
    new: () -> LexicalItemDetailState<*>,
): SearchResultsState {
    fun <R : LexicalItemDetail> List<LexicalItemDetailState<R>>
            .replaced(): List<LexicalItemDetailState<R>> =
        map { current ->
            if (clazz.isInstance(current) && current.source == source) {
                @Suppress("UNCHECKED_CAST")
                new() as LexicalItemDetailState<R>
            } else {
                current
            }
        }

    return copy(
        forms = forms.replaced(),
        explanations = explanations.replaced(),
        examples = examples.replaced(),
    )
}

@Suppress("UNCHECKED_CAST")
private fun SearchResultsState.copyWithNewDetails(
    clazz: KClass<out LexicalItemDetail>,
    updatedDetails: List<LexicalItemDetailState<out LexicalItemDetail>>,
): SearchResultsState {
    return when (clazz) {
        Forms::class ->
            copy(forms = updatedDetails as List<LexicalItemDetailState<Forms>>)
        Explanation::class ->
            copy(explanations = updatedDetails as List<LexicalItemDetailState<Explanation>>)
        Example::class ->
            copy(examples = updatedDetails as List<LexicalItemDetailState<Example>>)
        else -> throw Error("Unhandled class: $clazz")
    }
}

private fun SearchResultsState.detailsOfType(
    clazz: KClass<out LexicalItemDetail>,
): List<LexicalItemDetailState<out LexicalItemDetail>> {
    return when (clazz) {
        Forms::class -> forms
        Explanation::class -> explanations
        Example::class -> examples
        else -> throw Error("Unhandled class: $clazz")
    }
}
