package io.github.rain.redrock.spring.executor

import io.github.rain.redrock.spring.queue.DelegateSynchronousQueue

/**
 * io.github.rain.redrock.spring.executor.CachedThreadPool
 * FirstWork
 *
 * @author 寒雨
 * @since 2022/3/8 0:34
 **/
class CachedThreadPool(
    corePoolSize: Int,
    maxPoolSize: Int,
    keepAliveTime: Long
) : ThreadPool(
    corePoolSize = corePoolSize,
    maxPoolSize = maxPoolSize,
    keepAliveTime = keepAliveTime,
    queue = DelegateSynchronousQueue()
) {
    val cachedWorkers = arrayListOf<CachedThreadWorker>()

    inner class CachedThreadWorker : Runnable {
        private val thread = Thread(this)

        override fun run() {
            try {
                var blockingTime = System.currentTimeMillis()
                while (!thread.isInterrupted) {
                    if (System.currentTimeMillis() > blockingTime + keepAliveTime!!) {

                    }
                    queue.take().run()
                }
            } catch (e: InterruptedException) { }
        }
    }
}