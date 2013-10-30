package com.vasilich.scheduler.bootstrap

import org.springframework.beans.factory.config.BeanPostProcessor
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor

public class SchedulerPostProcessor: BeanPostProcessor {

    val tastExecutor = ThreadPoolTaskExecutor()

    override fun postProcessBeforeInitialization(bean: Any?, beanName: String?): Any? {
        return bean
    }
    override fun postProcessAfterInitialization(bean: Any?, beanName: String?): Any? {
        return bean
    }
}
