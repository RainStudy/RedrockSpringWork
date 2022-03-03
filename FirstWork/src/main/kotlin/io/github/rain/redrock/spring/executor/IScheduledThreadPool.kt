package io.github.rain.redrock.spring.executor

import java.util.concurrent.RunnableScheduledFuture
import java.util.concurrent.TimeUnit

/**
 * io.github.rain.redrock.spring.executor.IScheduledThreadPool
 * FirstWork
 *
 * @author 寒雨
 * @since 2022/3/3 10:54
 **/
interface IScheduledThreadPool {

    fun schedule(delay: Long = 50, timeUnit: TimeUnit = TimeUnit.MILLISECONDS, task: Runnable): RunnableScheduledFuture<Unit>

    fun scheduleTimer(delay: Long = 50, period: Long, timeUnit: TimeUnit = TimeUnit.MILLISECONDS, task: Runnable): RunnableScheduledFuture<Unit>
}