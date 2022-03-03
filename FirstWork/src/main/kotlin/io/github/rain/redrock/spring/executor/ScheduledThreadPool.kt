package io.github.rain.redrock.spring.executor

import io.github.rain.redrock.spring.queue.MyBlockingQueue
import java.util.concurrent.Delayed
import java.util.concurrent.FutureTask
import java.util.concurrent.RunnableScheduledFuture
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicLong

/**
 * io.github.rain.redrock.spring.executor.ScheduledThreadPool
 * FirstWork
 *
 * @author 寒雨
 * @since 2022/3/3 10:47
 **/
class ScheduledThreadPool(threadsCount: Int, queueSize: Int = 10) : ThreadPool(
    corePoolSize = threadsCount,
    maxPoolSize = threadsCount,
    keepAliveTime = null,
    queue = MyBlockingQueue(queueSize)
), IScheduledThreadPool {

    override fun schedule(delay: Long, timeUnit: TimeUnit, task: Runnable): RunnableScheduledFuture<Unit> {
        val future = ScheduledFutureTask(
            runnable = task,
            result = Unit,
            time = triggerTime(delay, timeUnit)
        )
        queue.add(future)
        return future
    }

    override fun scheduleTimer(delay: Long, period: Long, timeUnit: TimeUnit, task: Runnable): RunnableScheduledFuture<Unit> {
        require(period > 0L)
        val sft = ScheduledFutureTask(
            runnable = task,
            result = Unit,
            time = triggerTime(delay, timeUnit),
            period = triggerTime(period, timeUnit)
        )
        queue.add(sft)
        return sft
    }

    /**
     * 返回延迟操作的基于nano time的触发时间
     *
     * @param delay
     * @return
     */
    private fun triggerTime(delay: Long): Long {
        return System.nanoTime() +
                if (delay < Long.MAX_VALUE shr 1) delay else overflowFree(delay)
    }

    private fun triggerTime(delay: Long, unit: TimeUnit): Long {
        return triggerTime(unit.toNanos(if (delay < 0) 0 else delay))
    }

    /**
     * 将队列中所有延迟的值限制在彼此的 Long.MAX_VALUE 范围内，以避免 compareTo 中的溢出。
     * 如果一个任务有资格出队，但尚未出队，而其他一些任务被延迟 Long.MAX_VALUE 添加，则可能会发生这种情况
     *
     * @param delay
     * @return
     */
    private fun overflowFree(delay: Long): Long {
        var d = delay
        val head = queue.peek() as Delayed?
        if (head != null) {
            val headDelay = head.getDelay(TimeUnit.NANOSECONDS)
            if (headDelay < 0 && delay - headDelay < 0) d = Long.MAX_VALUE + headDelay
        }
        return d
    }

    /**
     * Scheduled future task
     * 注意 时间单位为nano time
     *
     * @param V
     * @property runnable
     * @property result
     * @property sequenceNumber
     * @property time
     * @property period
     * @constructor Create empty Scheduled future task
     */
    inner class ScheduledFutureTask<V>(
        private val runnable: Runnable,
        private val result: V,
        private val sequenceNumber: Long = sequencer.getAndIncrement(),
        private var time: Long,
        private val period: Long = 0
    ) : FutureTask<V>(runnable, result), RunnableScheduledFuture<V> {

        override fun isPeriodic(): Boolean {
            return period != 0L
        }

        override fun compareTo(other: Delayed?): Int {
            if (other === this) // compare zero if same object
                return 0
            if (other is ScheduledFutureTask<*>) {
                val diff = time - other.time
                return if (diff < 0) -1 else if (diff > 0) 1 else if (sequenceNumber < other.sequenceNumber) -1 else 1
            }
            val diff = getDelay(TimeUnit.NANOSECONDS) - other!!.getDelay(TimeUnit.NANOSECONDS)
            return if (diff < 0) -1 else if (diff > 0) 1 else 0
        }

        override fun getDelay(unit: TimeUnit): Long {
            return unit.convert(time - System.nanoTime(), TimeUnit.NANOSECONDS)
        }

        override fun run() {
            if (!isPeriodic)
                run()
            else if (runAndReset()) {
                setNextRunTime()
                queue.put(this)
            }
        }

        private fun setNextRunTime() {
            val p = period
            if (p > 0) time += p else time = triggerTime(-p)
        }
    }

    companion object {
        val sequencer = AtomicLong()
    }
}