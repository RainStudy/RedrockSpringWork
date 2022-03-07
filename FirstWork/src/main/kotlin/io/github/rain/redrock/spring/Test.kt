package io.github.rain.redrock.spring

import io.github.rain.redrock.spring.executor.ScheduledThreadPool
import java.util.concurrent.*


/**
 * io.rain.redrock.spring.Test
 * FirstWork
 *
 * @author 寒雨
 * @since 2022/3/2 18:26
 **/
fun main() {
    val pool = ScheduledThreadPool(
        threadsCount = 5,
    )
    pool.scheduleTimer(
        delay = 1,
        period = 1,
        timeUnit = TimeUnit.SECONDS
    ) {
        println("REDROCK!")
    }
//    pool.schedule(delay = 1, timeUnit = TimeUnit.SECONDS) {
//        println("延迟1秒")
//    }
//    pool.schedule(delay = 5, timeUnit = TimeUnit.SECONDS) {
//        println("延迟5秒")
//    }
    Thread.sleep(TimeUnit.SECONDS.toMillis(10))
    pool.shutdown()
}