package com.vasilich.commands.basic

import java.util.Date
import org.springframework.stereotype.Component
import com.vasilich.commands.api.Command

Component
public class DateTimeCommand : Command {

    override fun execute(msg: String): String? {
        return Date().toString()
    }
}
