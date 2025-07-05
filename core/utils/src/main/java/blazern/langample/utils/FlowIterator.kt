package blazern.langample.utils

import arrow.core.Either
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.io.Closeable
import kotlin.concurrent.atomics.AtomicBoolean
import kotlin.concurrent.atomics.ExperimentalAtomicApi

/**
 * NOTE: could contain tricky concurrency bugs
 */
@OptIn(ExperimentalAtomicApi::class)
class FlowIterator<T>(
    private val flow: Flow<T>,
    private val scope: CoroutineScope
) : Closeable {

    private val mutex = Mutex()
    private val signal = Channel<Unit>(Channel.RENDEZVOUS)
    private val values = Channel<Either<Throwable, T?>>(Channel.RENDEZVOUS)
    private val hasEnded = AtomicBoolean(false)

    private val job: Job = scope.launch {
        try {
            signal.receive()
            flow.collect { v ->
                values.send(Either.Right(v))
                signal.receive()
            }
            hasEnded.store(true)
            values.send(Either.Right(null))
        } catch (t: Throwable) {
            values.trySend(Either.Left(t))
        } finally {
            values.close()
            signal.close()
        }
    }

    /**
     * @return null if the flow has finished
     * @throws Throwable - whatever the flow has thrown
     */
    suspend fun next(): T? = mutex.withLock {
        try {
            signal.send(Unit)
        } catch (e: ClosedReceiveChannelException) {
            hasEnded.store(true)
            return null
        }

        val result = values.receive()
        return result.fold(
            { throw it },
            { it }
        )
    }

    override fun close() {
        hasEnded.store(true)
        job.cancel()
        signal.close()
        values.close()
    }

    /**
     * Returns true if the upstream is know to have finished.
     * Note that except for rare corner-cases this method will always
     * return `false` after the last element from the upstream has arrived
     * but `next` was not called yet.
     */
    fun hasEnded(): Boolean = hasEnded.load()
}
