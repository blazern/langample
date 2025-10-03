package blazern.langample.model.lexical_item_details_source.utils.examples_tools

import blazern.langample.domain.model.Sentence
import blazern.langample.domain.model.TextAccent
import blazern.langample.domain.model.WordForm

class FormsAccentsEnhancer internal constructor(
    private val forms: List<WordForm>,
) {
    fun enhance(
        sentence: Sentence,
    ): Sentence {
        val forms = forms
            .map { it.text }
            .filter { it.isNotEmpty() }

        val text = sentence.text
        val accents = mutableSetOf<TextAccent>()
        for (form in forms) {
            var startIndex = 0
            while (true) {
                startIndex = text.indexOf(form, startIndex, ignoreCase = true)
                if (startIndex == -1) {
                    break
                }
                accents.add(TextAccent(startIndex, startIndex + form.length))
                startIndex = startIndex + form.length
            }
        }

        return sentence.copy(
            textAccents = sentence.textAccents + accents,
        )
    }
}
