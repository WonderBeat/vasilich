package com.vasilich.commands

import java.util.Date
import org.springframework.stereotype.Component

Component
public class DateTimeCommand : Command {

    override fun execute(msg: String): String? {
        return Date().toString()
    }
}
