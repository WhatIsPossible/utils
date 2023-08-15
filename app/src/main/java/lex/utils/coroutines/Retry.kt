package lex.utils.coroutines

import kotlinx.coroutines.delay
import kotlinx.coroutines.withTimeout
import timber.log.Timber

/**
 * @Author dxl
 * @Date 2023/3/31 16:56
 * @Email lex911118@gmail.com
 * @Description This is Retry
 */

// retry with exponential backoff
// inspired by https://stackoverflow.com/questions/46872242/how-to-exponential-backoff-retry-on-kotlin-coroutines
/**
 * Retry
 * 每次重试时延迟时间递增
 * @param T
 * @param times 重试次数
 * @param initialDelayMillis    初始重试延迟时间
 * @param maxDelayMillis    最大延迟时间
 * @param factor    延迟时间递增因数
 * @param block
 * @receiver
 * @return
 */
suspend fun <T> retry(
    times: Int = Int.MAX_VALUE,
    initialDelayMillis: Long = 100,// 0.1 second
    maxDelayMillis: Long = 1000, // 1 second
    factor: Double = 2.0,
    block: suspend () -> T
): T {
    var currentDelay = initialDelayMillis
    repeat(times) {
        try {
            return block()
        } catch (exception: Exception) {
            // you can log an error here and/or make a more finer-grained
            // analysis of the cause to see if retry is needed
            Timber.e(exception)
        }
        delay(currentDelay)
        currentDelay = (currentDelay * factor).toLong().coerceAtMost(maxDelayMillis)
    }
    return block() // last attempt
}

suspend fun <T> retryWithTimeout(
    numberOfRetries: Int = Int.MAX_VALUE,
    timeout: Long,
    block: suspend () -> T
) = retry(numberOfRetries, factor = 1.0) {
    withTimeout(timeout) {
        block()
    }
}

/**
 * Retry
 * 每次重试时延迟时间递增
 * @param T
 * @param initialDelayMillis
 * @param maxDelayMillis
 * @param factor
 * @param predicate 根据条件决定是否重试
 * @param block
 * @receiver
 * @receiver
 * @return
 */
suspend fun <T> retry(
    initialDelayMillis: Long = 100,// 0.1 second
    maxDelayMillis: Long = 1000, // 1 second
    factor: Double = 2.0,
    predicate: (count: Int, exception: Exception) -> Boolean,// the predicate that determines if a resubscription may happen in case of a specific exception and retry count
    block: suspend () -> T
): T? {
    var currentDelay = initialDelayMillis
    repeat(Int.MAX_VALUE) {
        try {
            return block()
        } catch (exception: Exception) {
            // you can log an error here and/or make a more finer-grained
            // analysis of the cause to see if retry is needed
            if (!predicate(it + 1, exception)) {
                throw exception
            }
            Timber.e(exception)
        }
        delay(currentDelay)
        currentDelay = (currentDelay * factor).toLong().coerceAtMost(maxDelayMillis)
    }
    return block() // last attempt
}
/*
private suspend fun test() {
    val networkResult = retry { api.getArticle().await() } //无限重试
    val networkResult = retry(times = 3) { api.doSomething().await() }//重试3次
    val networkResult = retryWithTimeout(timeout = 10000) { api.getArticle().await() }//超时10s后自动重试
    val networkResult = retryWithTimeout(numberOfRetries = 3, timeout = 10000) { api.doSomething().await() }//超时10s后自动重试3次
}
*/
