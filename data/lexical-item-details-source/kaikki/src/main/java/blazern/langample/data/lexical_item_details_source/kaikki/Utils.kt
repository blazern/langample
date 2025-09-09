package blazern.langample.data.lexical_item_details_source.kaikki

import blazern.langample.data.kaikki.model.Form
import blazern.langample.domain.model.Lang
import blazern.langample.domain.model.WordForm

internal fun Form.toDomain(lang: Lang): WordForm {
    return WordForm(
        text = form.trim(),
        tags = (tags ?: emptyList()).map { it.toWordFormTag() },
        lang = lang,
    )
}

private fun String.toWordFormTag(): WordForm.Tag {
    return when (this.lowercase()) {
        "infinitive" -> WordForm.Tag.Defined.Infinitive(this)
        "participle" -> WordForm.Tag.Defined.Participle(this)
        "present" -> WordForm.Tag.Defined.Present(this)
        "past" -> WordForm.Tag.Defined.Past(this)
        "indicative" -> WordForm.Tag.Defined.Indicative(this)
        "subjunctive-i" -> WordForm.Tag.Defined.SubjunctiveI(this)
        "subjunctive-ii" -> WordForm.Tag.Defined.SubjunctiveII(this)
        "imperative" -> WordForm.Tag.Defined.Imperative(this)
        "preterite" -> WordForm.Tag.Defined.Preterite(this)
        "perfect" -> WordForm.Tag.Defined.Perfect(this)
        "pluperfect" -> WordForm.Tag.Defined.Pluperfect(this)
        "future-i" -> WordForm.Tag.Defined.FutureI(this)
        "future-ii" -> WordForm.Tag.Defined.FutureII(this)
        "first-person" -> WordForm.Tag.Defined.FirstPerson(this)
        "second-person" -> WordForm.Tag.Defined.SecondPerson(this)
        "third-person" -> WordForm.Tag.Defined.ThirdPerson(this)
        "singular" -> WordForm.Tag.Defined.Singular(this)
        "plural" -> WordForm.Tag.Defined.Plural(this)
        "informal" -> WordForm.Tag.Defined.Informal(this)
        "formal" -> WordForm.Tag.Defined.Formal(this)
        "rare" -> WordForm.Tag.Defined.Rare(this)
        "auxiliary" -> WordForm.Tag.Defined.Auxiliary(this)
        "multiword-construction" -> WordForm.Tag.Defined.MultiwordConstruction(this)
        else -> WordForm.Tag.Undefined(this)
    }
}
