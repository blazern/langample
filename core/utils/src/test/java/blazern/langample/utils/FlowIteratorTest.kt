package blazern.langample.utils

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.advanceUntilIdle
import org.junit.Test
import kotlinx.coroutines.test.runTest
import java.util.concurrent.atomic.AtomicInteger
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class FlowIteratorTest {
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `flow is not collected before next is called`() = runTest {
        val collectedCount = AtomicInteger(0)

        val upstream = flow {
            repeat(2) {
                collectedCount.incrementAndGet()
                emit("value")
            }
        }

        val iterator = FlowIterator(upstream, this)

        advanceUntilIdle()
        assertEquals(0, collectedCount.get())

        assertEquals("value", iterator.next())
        assertEquals(1, collectedCount.get(),)

        advanceUntilIdle()
        assertEquals(1, collectedCount.get(),)

        assertEquals("value", iterator.next())
        assertEquals(2, collectedCount.get())

        assertEquals(null, iterator.next())

        iterator.close()
    }

    @Test
    fun `hasEnded behavior`() = runTest {
        val upstream = flow {
            emit("123")
        }

        val iterator = FlowIterator(upstream, this)
        assertFalse { iterator.hasEnded() }
        assertEquals("123", iterator.next())

        // The flow has no other elements but [FlowIterator] does
        // not know it yet.
        assertFalse { iterator.hasEnded() }
        assertEquals(null, iterator.next())
        assertTrue { iterator.hasEnded() }
        iterator.close()
    }
}
