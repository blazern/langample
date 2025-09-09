package blazern.langample.domain.model

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class WordFormTest {
    @Test
    fun `cleaned text does not contain pronouns`() {
        val wf1 = WordForm("ich tanze", emptyList(), Lang.DE)
        assertEquals("tanze", wf1.textCleaned)

        val wf2 = WordForm("Sie tanzen", emptyList(), Lang.DE)
        assertEquals("tanzen", wf2.textCleaned)

        val wf3 = WordForm("er/sie tanzt", emptyList(), Lang.DE)
        assertEquals("tanzt", wf3.textCleaned)

        val wf4 = WordForm("wir\\sie tanzen", emptyList(), Lang.DE)
        assertEquals("tanzen", wf4.textCleaned)
    }

    @Test
    fun `cleaned text contains articles`() {
        val wf1 = WordForm("der Tisch", emptyList(), Lang.DE)
        assertEquals("der Tisch", wf1.textCleaned)
    }

    @Test
    fun `hasArticle field`() {
        assertFalse(WordForm("Tisch", emptyList(), Lang.DE).hasArticle)
        assertTrue(WordForm("der Tisch", emptyList(), Lang.DE).hasArticle)
    }
}
