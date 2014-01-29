package com.vasilich.commands.basic

import com.vasilich.commands.api.Command
import reactor.core.Observable
import com.vasilich.connectors.xmpp.Topics
import reactor.event.selector.Selectors
import reactor.function.Consumer
import reactor.event.Event
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.LinkedBlockingQueue
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.concurrent.locks.ReentrantLock
import com.vasilich.config.Config
import org.springframework.core.Ordered


Component
Config("history")
class HistoryCfg(val size: Long = 10)
/**
 * Created by developer on 1/28/14.
 */
Component
public class HistoryCommand [Autowired](var reactor: Observable, val cfg : HistoryCfg) : Command  {
    private val queue = LinkedBlockingQueue<String>();
    private val lock = ReentrantLock(false);

    {
        val topics: Topics = Topics();
        reactor.on(Selectors.`$`(topics.receive), Consumer<Event<String>> {
            val msg = it!!.getData()!!;
            queue.offer(msg);
            if (queue.size() >= (cfg.size * cfg.size)){
                clearQueue();
            }
        })
    }

    fun clearQueue(msg : String = "") : String{
        var result = "";
        lock.lock();
        try {
            if (!"".equals(msg)){
                queue.remove(msg);
            }
            while (queue.size() > cfg.size) {
                queue.poll();
            }
            result = queue.toString();
        } finally{
            lock.unlock();
        }

        return result;
    }
    override fun execute(msg: String): String? {
        return clearQueue(msg);
    }
}