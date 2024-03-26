package demo.resilience4j

import io.github.resilience4j.timelimiter.TimeLimiter
import io.github.resilience4j.timelimiter.TimeLimiterConfig
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import java.time.Duration
import java.util.concurrent.Callable
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit


/**
 *  limit the amount of time spent calling a remote service using the TimeLimiter
 */
class TimeLimiterTest {

    private val remoteService: RemoteService = mock()

    @Test
    fun `TimeLimiter should limit time spent calling remote service`() {
        val ttl: Long = 1
        val config  =
            TimeLimiterConfig.custom().timeoutDuration(Duration.ofMillis(ttl)).build()

        val timeLimiter: TimeLimiter = TimeLimiter.of(config)

        val futureMock: Future<Int> = mock()

        val restrictedCall: Callable<Int> = TimeLimiter.decorateFutureSupplier(timeLimiter) { futureMock }

        restrictedCall.call()

        // verify that Resilience4j calls Future.get() with the expected timeout
        verify(futureMock).get(ttl, TimeUnit.MILLISECONDS)
    }
}