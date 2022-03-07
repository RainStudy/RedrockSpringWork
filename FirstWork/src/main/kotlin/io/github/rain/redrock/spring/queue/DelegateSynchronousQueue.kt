package io.github.rain.redrock.spring.queue

import java.util.concurrent.SynchronousQueue

/**
 * io.github.rain.redrock.spring.queue.DelegateSynchronousQueue
 * FirstWork
 * 不想实现SynchronousQueue了
 * 其实也很好实现 但我犯懒了
 *
 * @author 寒雨
 * @since 2022/3/8 0:37
 **/
class DelegateSynchronousQueue<T> : IBlockingQueue<T> {

    private val delegate = SynchronousQueue<T>()

    override fun add(element: T) {
        delegate.add(element)
    }

    override fun offer(element: T): Boolean {
        return delegate.offer(element)
    }

    override fun peek(): T? {
        return delegate.peek()
    }

    override fun put(element: T) {
        delegate.put(element)
    }

    override fun remove(element: T) {
        delegate.remove(element)
    }

    override fun take(): T {
        return delegate.take()
    }
}