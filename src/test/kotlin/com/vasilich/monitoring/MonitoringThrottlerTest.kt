package com.vasilich.monitoring

import org.junit.Test
import com.google.common.cache.Cache
import com.vasilich.commands.api.Command
import com.vasilich.commands.basic.Ping
import java.util.concurrent.TimeUnit
import com.google.common.cache.CacheBuilder
import kotlin.test.assertTrue
import java.util.Comparator

public class MonitoringThrottlerTest {

    private trait SimpleSameStringMonitor: CommandMonitoring {
        override fun check(): Collection<String> {
            return listOf("Zombie apocalipse")
        }
    }

    private trait SlightlyDifferentStringCommandMonitoring: CommandMonitoring {
        val attempt: Int
        override fun check(): Collection<String> = when(attempt) {
            0 -> listOf("Zombie apocalipse")
            1 -> listOf("Zombie apocalipse!!")
            2 -> listOf("Run for your lives!")
            else -> listOf("They've bite me!")
        }
    }


    Test fun shouldPreventSameResultPropagation() {
        val monitor = object: SimpleSameStringMonitor {
            override val command: Command = Ping()
            val throttler = throttle<String>()
            override fun check(): Collection<String> {
                return throttler(super<MonitoringThrottlerTest.SimpleSameStringMonitor>.check())
            }
        }

        val firstTime = monitor.check()
        val secondTime = monitor.check()

        assertTrue(firstTime.isNotEmpty(), "For the first time throttler should pass the message")
        assertTrue(secondTime.isEmpty(), "For the second time throttler should block propagation")
    }

    Test fun shouldPreventSameResultOnSimilarAnswers() {
        val monitor = object: SlightlyDifferentStringCommandMonitoring {
            val throttler = throttle<String>(comparator = stringDistanceComparator(3))
            override var attempt: Int = 0
            override val command: Command = Ping()
            override fun check(): Collection<String> {
                val out = throttler(super<MonitoringThrottlerTest.SlightlyDifferentStringCommandMonitoring>.check())
                attempt += 1
                return out
            }
        }

        val firstTime = monitor.check()
        val secondTime = monitor.check()
        val thirdTime = monitor.check()

        assertTrue(firstTime.isNotEmpty(), "For the first time throttler should pass the message")
        assertTrue(secondTime.isEmpty(),
                "For the second time throttler should block propagation. Only 2 symbol difference")
        assertTrue(thirdTime.isNotEmpty(), "For the second time throttler should pass message. Because it's different")
    }


}
