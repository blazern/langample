package blazern.langample.model.lexical_item_details_source.utils.examples_tools.di

import blazern.langample.model.lexical_item_details_source.utils.examples_tools.FormsAccentsEnhancerProvider
import blazern.langample.model.lexical_item_details_source.utils.examples_tools.FormsForExamplesProvider
import org.koin.dsl.module

fun examplesToolsModule() = module {
    factory {
        FormsForExamplesProvider(
            kaikki = get(),
        )
    }
    factory {
        FormsAccentsEnhancerProvider(
            formsProvider = get(),
        )
    }
}
