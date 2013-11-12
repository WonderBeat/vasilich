package com.vasilich.commands.chatbot;

import bitoflife.chatterbean.AliceBot;
import bitoflife.chatterbean.parser.AliceBotParser;
import bitoflife.chatterbean.parser.AliceBotParserConfigurationException;
import bitoflife.chatterbean.parser.AliceBotParserException;

import java.io.InputStream;
import java.util.Collection;

/**
 * Kotlin fails to execute command with arguments (InputStream, InputStream, InputStream, InputStream...)
 * Varargs are problem
 * Spread operator can't help.
 * Remove this class, when Kotlin updates
 */
public class ChatBotLoader {

    public static AliceBot createBot(InputStream context,
                                     InputStream splitters,
                                     InputStream substitution,
                                     Collection<InputStream> resources) {
        try {
            AliceBotParser parser = new AliceBotParser();
            return parser.parse(context, splitters, substitution, resources.toArray(new InputStream[1]));
        } catch (AliceBotParserConfigurationException exception) {
            throw new RuntimeException(exception);
        } catch (AliceBotParserException exception) {
            throw new RuntimeException(exception);
        }
    }
}

