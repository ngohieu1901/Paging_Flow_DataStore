package com.hieunt.base.widget

import kotlinx.coroutines.withContext
import kotlin.contracts.ExperimentalContracts
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.cancellation.CancellationException

@OptIn(ExperimentalContracts::class)
suspend inline fun <R> runSuspendCatching(
    context: CoroutineContext = EmptyCoroutineContext,
    crossinline block: suspend () -> R,
): Result<R> {
//    contract { ContractBuilder.callsInPlace(block, InvocationKind.AT_MOST_ONCE) }

    return try {
        Result.success(withContext(context) { block() })
    } catch (c: CancellationException) {
        throw c
    } catch (e: Throwable) {
        Result.failure(e)
    }
}
