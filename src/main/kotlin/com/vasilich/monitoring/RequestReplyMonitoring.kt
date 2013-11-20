package com.vasilich.monitoring

class RequestReplyMatcher(val request: String, val replyMatcher: (String) -> Boolean, val explanation: String)

fun createRequestReplyMatchers(requestReply: Collection<RequestReplyMonitoringCfg>,
                               matcherBuilder: (String) -> ((String) -> Boolean))
                                                        :Collection<RequestReplyMatcher> =
        requestReply.map { entry -> RequestReplyMatcher(entry.say, matcherBuilder(entry.reply), entry.explanation) }


public trait RequestReplyMonitor: CommandMonitoring {

    val matchers: Collection<RequestReplyMatcher>

    private fun lookupFailedCommands(requestReply: RequestReplyMatcher): String? {
        fun RequestReplyMatcher.match(reply: String) = replyMatcher(reply)
        val reply = command.execute(requestReply.request)
        return when {
            reply != null && requestReply.match(reply) -> requestReply.explanation
            else -> null
        }
    }

    override fun check(): Collection<String> = matchers.map { lookupFailedCommands(it) }.filterNotNull().plus(super.check())

}
