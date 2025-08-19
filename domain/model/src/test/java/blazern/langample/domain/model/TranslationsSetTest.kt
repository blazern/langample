package blazern.langample.domain.model

import blazern.langample.domain.model.TranslationsSet.Companion.QUALITY_BASIC
import org.junit.Test
import kotlin.test.assertEquals

class TranslationsSetTest {
    @Test
    fun `translations and qualities get sorted`() {
        val original = sentence("orig")
        val translations = listOf(sentence("t1"), sentence("t2"), sentence("t3"))
        val qualities = listOf(3, 9, 1)

        val result = TranslationsSet(original, translations, qualities)

        assertEquals(listOf(9, 3, 1), result.translationsQualities)
        assertEquals(listOf("t2", "t1", "t3"), result.translations.map { it.text })

        // Also equal to an object constructed with everything sorted already
        assertEquals(
            TranslationsSet(original, result.translations, result.translationsQualities),
            result,
        )
    }

    @Test
    fun `when too many qualities they get truncated`() {
        val original = sentence("orig")
        val translations = listOf(sentence("t1"), sentence("t2"))

        // One extra
        val qualities = listOf(5, 4, 9)
        val result = TranslationsSet(original, translations, qualities)

        // Third quality dropped
        assertEquals(listOf(5, 4), result.translationsQualities)
    }

    @Test
    fun `when not enough qualities QUALITY_BASIC is added`() {
        val original = sentence("orig")
        val translations = listOf(sentence("t1"), sentence("t2"), sentence("t3"))

        // Only one
        val qualities = listOf(9)
        val set = TranslationsSet(original, translations, qualities)

        // Extra added
        assertEquals(
            listOf(9, QUALITY_BASIC, QUALITY_BASIC),
            set.translationsQualities,
        )
    }

    private fun sentence(text: String) = Sentence(text, Lang.EN, DataSource.PANLEX)
}
