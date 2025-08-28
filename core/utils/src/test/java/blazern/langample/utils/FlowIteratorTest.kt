package blazern.langample.utils

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
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

        val iterator = FlowIterator(upstream)

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

        val iterator = FlowIterator(upstream)
        assertFalse { iterator.hasEnded() }
        assertEquals("123", iterator.next())

        // The flow has no other elements but [FlowIterator] does
        // not know it yet.
        assertFalse { iterator.hasEnded() }
        assertEquals(null, iterator.next())
        assertTrue { iterator.hasEnded() }
        iterator.close()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `cancellation of one consumer does not affect another one`() = runTest {
        val upstream = Channel<String>(Channel.UNLIMITED)
        val iterator = FlowIterator(
            flow { for (x in upstream) emit(x) }
        )

        val consumer1 = launch { iterator.next() }
        runCurrent()
        consumer1.cancel()
        runCurrent()

        // Emitting while the first consumer is gone
        upstream.send("A")

        // Consumer 2
        var result: String? = null
        launch { result = iterator.next() }
        advanceUntilIdle()

        assertEquals("A", result)
        upstream.send("B")
        assertEquals("B", iterator.next())

        // End of stream
        upstream.close()
        assertEquals(null, iterator.next())

        iterator.close()
    }
}
