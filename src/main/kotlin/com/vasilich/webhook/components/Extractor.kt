package com.vasilich.webhook.processors

import io.netty.handler.codec.http.HttpRequest
import io.netty.handler.codec.http.HttpMethod
import java.nio.charset.Charset
import io.netty.buffer.ByteBufHolder

/**
 * Webhook conditionals are applied to HTTP request.
 * Conditional statement contains key and value
 * Key -> HTTP header / Body / URI
 * Value -> matcher
 * This fun extracts data from HTTP packet according conditional key
 */
fun httpConditionFieldValueExtractor(conditionKey: String): (HttpRequest) -> String = { request ->
    val header = request.headers()?.entries()?.find { it.getKey() == conditionKey }?.getValue()
    val charset = Charset.forName("UTF-8")
    when {
        conditionKey equalsIgnoreCase "uri" -> request.getUri()!!
        conditionKey equalsIgnoreCase "body" && request is ByteBufHolder ->
            request.content()?.toString(charset)!!
        header != null -> header
        request.getMethod() == HttpMethod.POST && request is ByteBufHolder ->
            request.content()?.toString(charset)!!
        else -> throw NoSuchFieldException("Can't apply condition ${conditionKey}. No such header")
    }
}
