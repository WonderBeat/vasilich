package com.vasilich.commands.basic.talk

import com.vasilich.commands.api.Command
import org.springframework.stereotype.Component
import com.vasilich.config.Config
import javax.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Autowired
import java.util.HashMap

Component
Config("basic-talk")
public class BasicTalkCommandCgf(val knowledge : Map<String, Array<String>> = HashMap<String,Array<String>>())

val createPairs = { (entry : Map.Entry<String, Array<String>>) -> Pair(entry.getKey(), entry.getValue()) }

fun String.containsIgnoreCase(it : String) = this.toLowerCase().contains(it.toLowerCase())

Component
public class BasicTalkCommand [Autowired] (private val cfg: BasicTalkCommandCgf): Command  {

    private val knowledgeBase : Map<String, String> = cfg.knowledge.map(createPairs).fold(HashMap<String,String>(), {
        akk, entry -> entry.second.forEach { key -> akk.put(key, entry.first) }
        akk
    })

    override fun execute(msg: String): String? {
        val key = knowledgeBase.keySet().find { msg containsIgnoreCase it }
        when(key) {
           null -> return null
           else -> return knowledgeBase.get(key);
        }
    }
}

