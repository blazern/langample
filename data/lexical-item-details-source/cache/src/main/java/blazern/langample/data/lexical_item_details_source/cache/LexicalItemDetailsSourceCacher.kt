package blazern.langample.data.lexical_item_details_source.cache

import arrow.core.Either
import arrow.core.Either.Left
import arrow.core.Either.Right
import blazern.langample.data.lexical_item_details_source.api.LexicalItemDetailsFlow
import blazern.langample.domain.model.DataSource
import blazern.langample.domain.model.Lang
import blazern.langample.domain.model.LexicalItemDetail
import blazern.langample.utils.FlowIterator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

open class LexicalItemDetailsSourceCacher {
    private val dataStreamsMutex = Mutex()
    private val dataStreams = mutableMapOf<DataKey, DataStream>()

    /**
     * Won't cache errors - every error it emits was just received from [execute].
     * Order of emitted items is not guaranteed to be kept, please order them manually.
     */
    @Suppress("TooGenericExceptionCaught")
    open fun retrieveOrExecute(
        source: DataSource,
        query: String,
        langFrom: Lang,
        langTo: Lang,
        coroutineScope: CoroutineScope? = null,
        execute: () -> LexicalItemDetailsFlow,
    ): LexicalItemDetailsFlow = flow {
        val key = DataKey(source, query, langFrom, langTo)
        val dataStream = dataStreamsMutex.withLock {
            dataStreams.getOrPut(key) {
                DataStream(
                    iterator = FlowIterator(execute(), coroutineScope),
                    receivedData = mutableListOf(),
                )
            }
        }

        var nextDataEntryIndex = 0
        while (true) {
            val newEntries = dataStream.dataMutex.withLock {
                if (nextDataEntryIndex < dataStream.receivedData.size) {
                    dataStream.receivedData.subList(
                        nextDataEntryIndex,
                        dataStream.receivedData.size,
                    ).toList()
                } else {
                    emptyList()
                }
            }
            // Each emit can suspend for long, so we're
            // emitting from outside of the lock
            if (newEntries.isNotEmpty()) {
                newEntries.forEach { emit(Right(it)) }
                nextDataEntryIndex += newEntries.size
                continue
            }

            val newResult = try {
                dataStream.iterator.next()
            } catch (e: Exception) {
                emit(Left(e))
                continue
            }
            if (newResult == null) {
                // End of stream
                return@flow
            }
            val newItem = newResult.fold(
                { emit(Left(it)); continue },
                { it }
            )
            dataStream.dataMutex.withLock {
                dataStream.receivedData.add(newItem)
            }
        }
    }

    companion object {
        val NOOP = object : LexicalItemDetailsSourceCacher() {
            override fun retrieveOrExecute(
                source: DataSource,
                query: String,
                langFrom: Lang,
                langTo: Lang,
                coroutineScope: CoroutineScope?,
                execute: () -> LexicalItemDetailsFlow,
            ) = execute()
        }
    }
}

private data class DataKey(
    val source: DataSource,
    val query: String,
    val langFrom: Lang,
    val langTo: Lang,
)

private class DataStream(
    val iterator: FlowIterator<Either<Exception, LexicalItemDetail>>,
    val receivedData: MutableList<LexicalItemDetail>,
    val dataMutex: Mutex = Mutex(),
)
