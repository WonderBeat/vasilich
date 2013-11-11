package com.vasilich.commands.bootstrap

import org.junit.Test
import kotlin.test.assertEquals


public class CommandPostProcessorTest {

    Test fun testConfigExtraction() {
        assertEquals(getNodeName("SpiderMan"), "spider-man");
        assertEquals(getNodeName("Command"), "command");
        assertEquals(getNodeName(""), "");
        assertEquals(getNodeName("superMan"), "super-man");
        assertEquals(getNodeName("EPAM"), "e-p-a-m");
        assertEquals(getNodeName("DateTimeCommand"), "date-time-command");
    }
}

