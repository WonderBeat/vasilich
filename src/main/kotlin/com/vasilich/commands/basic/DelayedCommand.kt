package com.vasilich.commands.basic

import com.vasilich.commands.api.Command
import org.springframework.stereotype.Component
import java.util.concurrent.BlockingQueue
import java.util.concurrent.Delayed
import java.util.concurrent.TimeUnit
import java.util.concurrent.DelayQueue
import java.util.regex.Pattern
import java.util.ArrayList
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.Executors
import com.vasilich.config.Config
import org.springframework.beans.factory.annotation.Autowired
import com.vasilich.connectors.chat.Chat
import org.jivesoftware.smack.packet.Message

/**
 *
 * @author Ilia_Mestnikov
 */
class DelayedItem(val task : Runnable, val delay : Long, val unit : TimeUnit,
                  val start : Long = System.currentTimeMillis()) : Delayed {

    private val delayInMillis: Long
        get() = TimeUnit.MILLISECONDS.convert(delay, unit)


    override fun compareTo(other: Delayed): Int {
        if (this == other)
            return 0

        return this.getDelay(TimeUnit.MILLISECONDS).compareTo((other.getDelay(TimeUnit.MILLISECONDS)))

    }
    override fun getDelay(unit: TimeUnit): Long {
        return unit.convert(delayInMillis - (System.currentTimeMillis() - start), TimeUnit.MILLISECONDS)
    }

}

// command format: delay 5 sec , ping
// command format: delay 10 min , mon disk
Component
Config("delayed")
class DelayedCommandCfg(val enabled: Boolean = false, val initialDelay: Long = 5, val delay: Long = 5, val timeUnit: String = "SECONDS")

Component
public class DelayedCommand [Autowired] (val cfg : DelayedCommandCfg, val chat: Chat<Message>) : Command {

    val queue: BlockingQueue<DelayedItem> = DelayQueue<DelayedItem>()

    val pattern : Pattern = Pattern.compile("(Vasilich)?\\s*,\\s*delay\\s+(\\d+)\\s+([a-zA-Z]+)\\s*,\\s*(.+)")

    val usageMsg = "Usage: delay <amount> <sec|min|hour> , <command>\nExample: delay 5 sec, ping"

    val scheduler : ScheduledExecutorService = Executors.newScheduledThreadPool(1);

    {
        if (cfg.enabled) {
            scheduler.scheduleWithFixedDelay(Runnable{process()}, cfg.initialDelay, cfg.delay, TimeUnit.valueOf(cfg.timeUnit))
        }
    }

    override fun execute(msg: String): String? {

        val matcher = pattern.matcher(msg)
        if (matcher.matches()) {
            val delay:Long = matcher.group(2)!!.toLong()
            val unit = getTimeUnit(matcher.group(3)!!)
            if (unit == null) return usageMsg
            val command = matcher.group(4)
            val result = queue.offer(DelayedItem(
                    Runnable {
                        chat.send("Vasilich, $command")
                    },
                    delay, unit))
            return if (result) "Your command: \"$command\" has been added to the queue and will be executed in $delay $unit"
                else "Your command \"$command\" wasn't added to the queue, please try again"
        } else {
            return usageMsg
        }
    }


    fun getTimeUnit(string:String) : TimeUnit? {
        val s = string.toLowerCase()
        when {
            "sec".equals(s) || s.startsWith("second") -> return TimeUnit.SECONDS
            "min".equals(s) || s.startsWith("minute") -> return TimeUnit.MINUTES
            s.startsWith("hour") -> return TimeUnit.HOURS
            else -> return null
        }
    }

    //Scheduled(fixedDelay = 5000)
    fun process(): Unit {
        if (queue.notEmpty) {
            val readyToExecute: MutableCollection<DelayedItem> = ArrayList<DelayedItem>()
            queue.drainTo(readyToExecute)
            for (item in readyToExecute) {
                item.task.run()
            }
        }
    }

}

