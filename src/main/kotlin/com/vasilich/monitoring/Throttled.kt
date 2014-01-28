package com.vasilich.monitoring

import com.google.common.cache.Cache
import org.apache.commons.lang3.StringUtils
import java.util.Comparator
import java.util.concurrent.TimeUnit
import com.google.common.cache.CacheBuilder
import java.util.TreeSet


fun stringDistanceComparator(criticalDistance: Int) = Comparator<String> { (one: String, another: String): Int ->
    val distance = StringUtils.getLevenshteinDistance(one, another)
    when {
        distance > criticalDistance -> distance
        else -> 0
    }
}

/**
 * Monitoring can be very importunate
 * With this trait we can reduce it verbosity
 */
fun throttle<T>(cache: Cache<T, Unit> = CacheBuilder.newBuilder()!!.maximumSize(20)!!
        .expireAfterWrite(2, TimeUnit.MINUTES)!!.build()!!,
                comparator: Comparator<T>? = null): (Collection<T>) -> Collection<T> =
        { input ->
            val keyset = cache.asMap()!!.keySet()
            val newInstances = input.filter { when {
                comparator == null && !keyset.contains(it) -> true
                comparator == null -> false
                else -> {
                    val keyComparableSet = TreeSet(comparator)
                    keyComparableSet.addAll(keyset)
                    !keyComparableSet.containsItem(it)
                }
            }}
            newInstances.forEach { cache.put(it, Unit.VALUE) }
            newInstances
        }
