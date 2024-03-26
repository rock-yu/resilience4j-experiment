package demo.resilience4j

import io.github.resilience4j.retry.Retry
import io.github.resilience4j.retry.RetryConfig
import io.github.resilience4j.retry.RetryRegistry
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
import java.util.function.Function
import kotlin.test.fail


/**
 * automatically retry a failed call using the Retry API
 */
class RetryTest {

    private val remoteService: RemoteService = mock()

    @Test
    fun `Retry should retry calls to service on failures`() {
        val config = RetryConfig.custom<Any>().maxAttempts(2).build()
        val registry = RetryRegistry.of(config)
        val retry: Retry = registry.retry("my")

        val decorated: Function<Int, Void> =
            Retry.decorateFunction(retry) { s: Int ->
                remoteService.process(s)
                null
            }

        // emulate a situation where an exception is thrown during a remote service call and ensure that the
        // library automatically retries the failed call
        whenever(remoteService.process(any())).thenThrow(RuntimeException())

        try {
            decorated.apply(1)
            fail("Expected an exception to be thrown if all retries failed")
        } catch (e: Exception) {
            verify(remoteService, times(2)).process(any())
        }
    }
}