package io.github.rain.redrock.spring.executor

import io.github.rain.redrock.spring.queue.IBlockingQueue
import java.util.*
import java.util.concurrent.Callable
import java.util.concurrent.Future
import java.util.concurrent.FutureTask

/**
 * io.github.rain.redrock.spring.executor.ThreadPoolExecutor
 * FirstWork
 * 线程池抽象类
 *
 * @author 寒雨
 * @since 2022/3/2 20:51
 **/
abstract class ThreadPool(
    private val corePoolSize: Int,
    private val maxPoolSize: Int,
    protected val keepAliveTime: Long?,
    protected val queue: IBlockingQueue<Runnable>
) {

    protected val threads = mutableListOf<Thread>()

    private val runnable = Runnable {
        try {
            while (!Thread.currentThread().isInterrupted) {
                queue.take().run()
            }
        } catch (ignored: InterruptedException) {  }
    }

    private val uuid: UUID = UUID.randomUUID()

    init {
        repeat(corePoolSize) {
            threads.add(Thread(runnable, "${javaClass.simpleName}--$uuid--$it").apply { start() })
        }
    }

    open fun <T> submit(task: Callable<T>): Future<T> {
        val future = FutureTask(task)
        queue.put(future)
        return future
    }

    open fun <T> submit(task: Runnable, def: T): Future<T> {
        val future = FutureTask(task, def)
        queue.put(future)
        return future
    }

    fun submit(task: Runnable): Future<Unit> {
        return submit(task, Unit)
    }

    fun shutdown() {
        threads.forEach {
            it.interrupt()
        }
    }
}