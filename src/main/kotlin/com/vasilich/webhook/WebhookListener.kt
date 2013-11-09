package com.vasilich.webhook

import org.springframework.stereotype.Component
import com.vasilich.config.Config
import reactor.function.Consumer
import io.netty.handler.codec.http.HttpRequest
import com.vasilich.webhook.processors.createExactMatcher
import com.vasilich.webhook.processors.createRegexMatcher
import com.vasilich.webhook.processors.httpConditionFieldValueExtractor
import reactor.core.Observable
import com.vasilich.connectors.xmpp.Topics
import com.vasilich.webhook.processors.createNotificationProcessor

Component
Config("webhook")
public class WebhookCfg(val enabled: Boolean = false,
                        val port: Int = -1, val address: String = "",
                        val commands: Array<WebhookCommand> = array())

public class WebhookCommand(val conditions: Map<String, String> = mapOf(), val trigger: String = "",
                            val notify: String = "")

fun createProcessors(reactor: Observable,
                     notify: String,
                     trigger: String = "",
                     topics: Topics = Topics()): Collection<(Collection<String>) -> Unit> {
    //Currying is not implemented in current version of Kotlin. =(
    val templateAndTopic = array(Pair(trigger, topics.receive), Pair(notify, topics.send))
    return templateAndTopic
            .map { if(it.first.isNotEmpty()) createNotificationProcessor(reactor, it.second,it.first) else null }
            .filterNotNull()
}

/**
 * Webhook consumer
 *
 * Any request, that come with HTTP is analysed. If conditions are met, then processors are executed
 * Configuration contains conditions as a map: field -> matcher
 * 1. We need to extract interesting fields from HTTP request [fieldExtractor]
 * 2. We need to apply matcher on this field.
 * 3. if all matchers succeeded, than "webhook is matched" and time to execute processors
 */
fun createWebhookConsumer(conditions: Map<String, String>,
                          processors: Collection<(Collection<String>) -> Unit>): Consumer<HttpRequest> {
    val matchers = conditions.map {
        val value = it.getValue()
        val fieldExtractor = httpConditionFieldValueExtractor(it.getKey())
        val matcher = when {
            value.startsWith("match: ") -> createExactMatcher(value.trimLeading("match: "))
            value.startsWith("regexp: ") -> createRegexMatcher(value.trimLeading("regexp: "))
            else -> createExactMatcher(value)
        }
        Pair(fieldExtractor, matcher)
    }

    fun getMatchedData(request: HttpRequest): List<Collection<String>?> {
        try {
            return matchers.map { it.second(it.first(request)) }
        } catch (exc: NoSuchFieldException) {
            return listOf()
        }
    }

    return Consumer<HttpRequest> { request ->
        val matchedData = getMatchedData(request!!)
        val allMatcherSucceeded = matchedData.filterNotNull().size == matchers.size
        if(allMatcherSucceeded) {
            val flatResults = matchedData.flatMap { it!! }
            processors.forEach { it(flatResults) }
        }
    }

}
