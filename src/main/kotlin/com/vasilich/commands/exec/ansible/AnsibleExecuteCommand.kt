package com.vasilich.commands.exec

import org.springframework.stereotype.Component
import com.vasilich.config.Config
import com.vasilich.commands.api.Command
import java.text.MessageFormat
import org.springframework.beans.factory.annotation.Autowired

Component
Config("ansible")
public class AnsibleCgf(val scripts: Array<ExecUnit> = array(),
                        val timeout: Long = 60000,
                        val done: String = "Done",
                        val error: String = "Failed")

