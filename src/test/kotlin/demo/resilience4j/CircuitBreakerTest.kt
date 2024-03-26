package demo.resilience4j

import io.github.resilience4j.circuitbreaker.CircuitBreaker
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
import java.util.function.Function
import kotlin.test.assertEquals


/**
 * The Circuit Breaker pattern helps us in preventing a cascade of failures when a remote service
 * is down.
 *
 * After a number of failed attempts, we can consider that the service is unavailable/overloaded
 * and eagerly reject all subsequent requests to it. In this way, we can save system resources for
 * calls which are likely to fail.
 */
class CircuitBreakerTest {

    private val remoteService: RemoteService = mock()

    @Test
    fun `CircuitBreaker should prevent excessive calls to a sick service`() {
        // set the rate threshold to 20% and a minimum number of 5 call attempts.
        val config: CircuitBreakerConfig = CircuitBreakerConfig.custom()
            .failureRateThreshold(20f)
            .slidingWindowSize(5)
            .build()

        val registry = CircuitBreakerRegistry.of(config)
        val circuitBreaker = registry.circuitBreaker("my")
        val decorated: Function<Int, Int> = CircuitBreaker
            .decorateFunction(circuitBreaker, remoteService::process)

        whenever(remoteService.process(any())).thenThrow(RuntimeException())

        assertEquals(CircuitBreaker.State.CLOSED, circuitBreaker.state)

        for (i in 0..9) {
            try {
                decorated.apply(i)
            } catch (ignore: Exception) {
                // ignored
            }
        }

        verify(remoteService, times(5)).process(any())

        // A CircuitBreaker can be in one of the three states:
        // ** CLOSED – everything is fine, no short-circuiting involved
        // ** OPEN – remote server is down, all requests to it are short-circuited
        // **  HALF_OPEN – a configured amount of time since entering OPEN state has elapsed and CircuitBreaker allows requests to check if the remote service is back online

        // the circuit breaker should be open after 5 failed attempts
        assertEquals(CircuitBreaker.State.OPEN, circuitBreaker.state)
    }
}