package io.github.rain.redrock.spring.executor

import io.github.rain.redrock.spring.queue.MyBlockingQueue
import java.util.concurrent.Callable
import java.util.concurrent.Future

/**
 * io.github.rain.redrock.spring.executor.SingleThreadPool
 * FirstWork
 *
 * @author 寒雨
 * @since 2022/3/2 22:59
 **/
class SingleThreadPool(queueSize: Int = 10) : ThreadPool(
    corePoolSize = 1,
    maxPoolSize = 1,
    keepAliveTime = null,
    queue = MyBlockingQueue(queueSize)
)