package blazern.langample.domain.model

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class WordFormTest {
    @Test
    fun `text without pronouns`() {
        val wf1 = WordForm("ich tanze", emptyList(), Lang.DE)
        assertEquals("tanze", wf1.withoutPronoun().text)
        assertTrue(wf1.hasPronoun)
        assertFalse(wf1.withoutPronoun().hasPronoun)
        assertEquals("ich tanze", wf1.text)

        // But articles are still there
        val wf2 = WordForm("der Tisch", emptyList(), Lang.DE)
        assertEquals("der Tisch", wf2.withoutPronoun().text)
        assertFalse(wf2.hasPronoun)
        assertTrue(wf2.withoutPronoun().hasArticle)
    }

    @Test
    fun `text without article`() {
        val wf1 = WordForm("der Tisch", emptyList(), Lang.DE)
        assertEquals("Tisch", wf1.withoutArticle().text)
        assertTrue(wf1.hasArticle)
        assertFalse(wf1.withoutArticle().hasArticle)

        // But pronouns are still there
        val wf2 = WordForm("ich tanze", emptyList(), Lang.DE)
        assertEquals("ich tanze", wf2.withoutArticle().text)
        assertFalse(wf2.hasArticle)
        assertTrue(wf2.withoutArticle().hasPronoun)
    }
}
