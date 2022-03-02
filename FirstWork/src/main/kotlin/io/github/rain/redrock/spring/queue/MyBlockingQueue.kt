package io.github.rain.redrock.spring.queue

import java.util.*
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.locks.ReentrantLock

/**
 * io.rain.redrock.spring.MyBlockingQueue
 * FirstWork
 * 堵塞队列个人实现
 *
 * @author 寒雨
 * @since 2022/3/2 18:26
 **/
class MyBlockingQueue<E>(
    private val capacity: Int = Int.MAX_VALUE,
) : IBlockingQueue<E> {

    private val list = LinkedList<E>()

    private val count = AtomicInteger()

    // 被take方法持有的锁
    private val takeLock = ReentrantLock()

    private val notEmpty = takeLock.newCondition()

    // 被put方法持有的锁
    private val putLock = ReentrantLock()

    private val notFull = putLock.newCondition()

    override fun add(element: E) {
        putLock.lockInterruptibly()
        if (count.get() == capacity) {
            error("full!")
        }
        list.add(element)
        val c = count.getAndIncrement()
        if (c + 1 < capacity)
            notFull.signal()
        if (c == 0)
            signalNotEmpty()
        putLock.unlock()
    }

    override fun offer(element: E): Boolean {
        putLock.lockInterruptibly()
        if (count.get() == capacity) {
            return false
        }
        list.add(element)
        val c = count.getAndIncrement()
        if (c + 1 < capacity)
            notFull.signal()
        if (c == 0)
            signalNotEmpty()
        putLock.unlock()
        return true
    }

    override fun put(element: E) {
        putLock.lockInterruptibly()
        while (count.get() == capacity) {
            notFull.await()
        }
        list.add(element)
        val c = count.getAndIncrement()
        if (c + 1 < capacity)
            notFull.signal()
        if (c == 0)
            signalNotEmpty()
        putLock.unlock()
    }

    override fun remove(element: E) {
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

    override fun take(): E {
        takeLock.lockInterruptibly()
        // 链表为空则进入循环
        while (count.get() == 0) {
            // 使当前线程等待
            notEmpty.await()
            // 被signal唤醒，并通过循环的条件跳出循环
        }
        return list.pollFirst().also {
            val c = count.getAndDecrement()
            if (c > 1)
                notEmpty.signal()
            if (c == capacity)
                signalNotFull()
            takeLock.unlock()
        }
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
}