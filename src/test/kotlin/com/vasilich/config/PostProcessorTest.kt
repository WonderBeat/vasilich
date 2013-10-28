package com.vasilich.config

import org.junit.Test
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.core.io.ClassPathResource

public class PostProcessorTest {

    var mapper = ObjectMapper()

    val processor = JsonBasedConfigPostProcessor(mapper.readTree(ClassPathResource("test-post-processor.json")
            .getFile())!!, mapper)

    Config("one")
    class One(val enabled: Boolean = false)

    Test fun testConfigExtraction() {
        val cfg = processor.postProcessAfterInitialization(One(), "") as One
        assert(cfg.enabled == true, "Should decode config parameters")
    }
}
