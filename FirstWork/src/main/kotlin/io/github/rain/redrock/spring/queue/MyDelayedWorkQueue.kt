package io.github.rain.redrock.spring.queue

import java.util.*
import java.util.concurrent.RunnableScheduledFuture
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.locks.ReentrantLock

/**
 * io.github.rain.redrock.spring.queue.MyDelayedWorkQueue
 * FirstWork
 *
 * @author 寒雨
 * @since 2022/3/3 11:24
 **/
class MyDelayedWorkQueue(
    private val capacity: Int = Int.MAX_VALUE,
) : IBlockingQueue<Runnable> {

    private val list = LinkedList<RunnableScheduledFuture<*>>()

    private val count = AtomicInteger()

    // 被take方法持有的锁
    private val takeLock = ReentrantLock()

    private val notEmpty = takeLock.newCondition()

    // 被put方法持有的锁
    private val putLock = ReentrantLock()

    private val notFull = putLock.newCondition()

    override fun add(element: Runnable) {
        putLock.lockInterruptibly()
        if (count.get() == capacity) {
            error("full!")
        }
        list.addAndSort(element as RunnableScheduledFuture<*>)
        val c = count.getAndIncrement()
        if (c + 1 < capacity)
            notFull.signal()
        if (c == 0)
            signalNotEmpty()
        putLock.unlock()
    }

    override fun offer(element: Runnable): Boolean {
        putLock.lockInterruptibly()
        if (count.get() == capacity) {
            return false
        }
        list.addAndSort(element as RunnableScheduledFuture<*>)
        val c = count.getAndIncrement()
        if (c + 1 < capacity)
            notFull.signal()
        if (c == 0)
            signalNotEmpty()
        putLock.unlock()
        return true
    }

    override fun put(element: Runnable) {
        putLock.lockInterruptibly()
        while (count.get() == capacity) {
            notFull.await()
        }
        list.addAndSort(element as RunnableScheduledFuture<*>)
        val c = count.getAndIncrement()
        if (c + 1 < capacity)
            notFull.signal()
        if (c == 0)
            signalNotEmpty()
        putLock.unlock()
    }

    override fun remove(element: Runnable) {
        takeLock.lockInterruptibly()
        val amount = list.filter { it == element }.size
        list.remove(element)
        val c = count.getAndUpdate { it - amount }
        if (c > amount)
            notEmpty.signal()
        if (c == capacity)
            signalNotFull()
        takeLock.unlock()
    }

    private fun signalNotFull() {
        putLock.lock()
        try {
            notFull.signal()
        } finally {
            putLock.unlock()
        }
    }

    private fun signalNotEmpty() {
        takeLock.lock()
        try {
            notEmpty.signal()
        } finally {
            takeLock.unlock()
        }
    }

    override fun take(): RunnableScheduledFuture<*> {
        takeLock.lockInterruptibly()
        // 链表为空则进入循环
        while (count.get() == 0) {
            // 使当前线程等待
            notEmpty.await()
            // 被signal唤醒，并通过循环的条件跳出循环
        }
        val first = list.pollFirst().also {
            while (it.getDelay(TimeUnit.MILLISECONDS) > 0) {
//                println(it.getDelay(TimeUnit.MILLISECONDS))
            }
            val c = count.getAndDecrement()
            if (c > 1)
                notEmpty.signal()
            if (c == capacity)
                signalNotFull()
            takeLock.unlock()
        }
        return first
    }

    private fun LinkedList<RunnableScheduledFuture<*>>.addAndSort(element: RunnableScheduledFuture<*>) {
        var loc = 0
        for (i in 0 until size) {
            if (get(loc) > element) {
                loc = i
                break
            }
        }
        add(loc, element)
    }

    override fun peek(): RunnableScheduledFuture<*>? {
        takeLock.lockInterruptibly()
        return list.peek().also { takeLock.unlock() }
    }
}