package demo.resilience4j

import io.github.resilience4j.ratelimiter.RateLimiter
import io.github.resilience4j.ratelimiter.RateLimiterConfig
import io.github.resilience4j.ratelimiter.RateLimiterRegistry
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
import java.time.Duration
import java.util.function.Function
import kotlin.test.assertFailsWith


/**
 * this functionality allows limiting access to some service or resources.
 */
class RateLimiterTest {

    private val remoteService: RemoteService = mock()

    @Test
    fun `RateLimiter should limit excessive calls to service`() {
        val config: RateLimiterConfig =
            RateLimiterConfig.custom()
                .limitRefreshPeriod(Duration.ofMinutes(1))
                .limitForPeriod(2)
                .timeoutDuration(Duration.ofMillis(100))
                .build()

        val registry: RateLimiterRegistry = RateLimiterRegistry.of(config)
        val rateLimiter: RateLimiter = registry.rateLimiter("my")

        val decorated: Function<Int, Int> =
            RateLimiter.decorateFunction(rateLimiter, remoteService::process)

        decorated.apply(1)
        decorated.apply(2)
        assertFailsWith(RuntimeException::class, "RateLimiter does not permit further calls") {
            decorated.apply(3)
        }
    }
}