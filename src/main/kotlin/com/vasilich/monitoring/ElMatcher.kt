package com.vasilich.monitoring

import java.util.regex.Pattern
import org.springframework.expression.spel.standard.SpelExpressionParser
import org.springframework.expression.spel.support.StandardEvaluationContext

private val pattern = Pattern.compile("-?\\d+") // any digit

private class ElMatcherContext(val answer: String, val number: Int)

public fun elMatcher(expression: String): (String) -> Boolean {
    val parser = SpelExpressionParser()
    return {
        val matcher = pattern.matcher(it)
        val number = if(matcher.find()) matcher.group().toInt() else 0
        val context = StandardEvaluationContext(ElMatcherContext(it, number))
        parser.parseExpression(expression)?.getValue(context) as Boolean
    }
}
