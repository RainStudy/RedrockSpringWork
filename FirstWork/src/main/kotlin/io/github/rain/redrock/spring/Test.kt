package io.github.rain.redrock.spring

import io.github.rain.redrock.spring.executor.SingleThreadPool
import java.util.concurrent.Callable
import java.util.concurrent.ThreadLocalRandom
import java.util.concurrent.TimeUnit


/**
 * io.rain.redrock.spring.Test
 * FirstWork
 *
 * @author 寒雨
 * @since 2022/3/2 18:26
 **/
fun main() {
    val pool = SingleThreadPool()
    pool.submit {
        println(Thread.currentThread().name + ": REDROCK")
    }
    pool.submit {
        println(Thread.currentThread().name + ": redrock")
    }
    val future = pool.submit(Callable {
        Thread.sleep(TimeUnit.SECONDS.toMillis(2))
        return@Callable ThreadLocalRandom.current().nextInt()
    })
    Thread.sleep(TimeUnit.SECONDS.toMillis(5))
    println(future.get())
    pool.shutdown()
}