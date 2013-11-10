package com.vasilich.chatbot
import bitoflife.chatterbean.AliceBot
import bitoflife.chatterbean.parser.AliceBotParser
import com.vasilich.commands.chatbot.ChatBotLoader
import com.vasilich.commands.chatbot.ChatbotPackage
import spock.lang.Specification

class AliceChatbotTest extends Specification {

    def asInStream(String name) {
        this.class.getResourceAsStream(name)
    }

    def 'general chatterbean chatbot test'() {
        given:
        AliceBotParser parser = new AliceBotParser();

        def aimlResources = ChatbotPackage.loadAimsFromClasspath("classpath:/Bots/Alice/**.aiml")

        AliceBot bot = ChatBotLoader.createBot(asInStream("/Bots/context.xml"),
                                    asInStream("/Bots/splitters.xml"),
                                    asInStream("/Bots/substitutions.xml"),
                                    aimlResources)
        when:
        def answer = bot.respond('hello')

        then:
        assert !answer.isEmpty()


    }

}
