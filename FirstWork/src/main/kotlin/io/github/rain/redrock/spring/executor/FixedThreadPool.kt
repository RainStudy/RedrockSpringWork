package io.github.rain.redrock.spring.executor

import io.github.rain.redrock.spring.queue.MyBlockingQueue

/**
 * io.github.rain.redrock.spring.executor.FixedThreadPool
 * FirstWork
 *
 * @author 寒雨
 * @since 2022/3/3 10:30
 **/
class FixedThreadPool(threadsCount: Int, queueSize: Int = 10) : ThreadPool(
    corePoolSize = threadsCount,
    maxPoolSize = threadsCount,
    keepAliveTime = null,
    queue = MyBlockingQueue(queueSize)
)