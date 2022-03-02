package io.github.rain.redrock.spring.queue

/**
 * io.rain.redrock.spring.IBlockingQueue
 * FirstWork
 *
 * @author 寒雨
 * @since 2022/3/2 18:31
 **/
interface IBlockingQueue<E> {
    /**
     * 立即向队列中添加元素
     * 没有可用空间时抛出异常
     *
     * @param element
     */
    fun add(element: E)

    /**
     * 立即向队列中添加元素
     * 没有可用空间时返回false
     *
     * @param element
     */
    fun offer(element: E): Boolean

    /**
     * 向队列中插入元素，如有必要则等待空余位置
     *
     * @param element
     * @throws InterruptedException 执行该方法的线程已经中断
     */
    @Throws(InterruptedException::class)
    fun put(element: E)

    /**
     * 检索并删除第一个元素，如有必要，等待一个可用的元素
     *
     * @return 第一个元素
     * @throws InterruptedException 执行该方法的线程已经中断
     * @throws IllegalStateException 当前列表为空
     */
    @Throws(InterruptedException::class)
    fun take(): E

    /**
     * 删除元素 (若存在)
     *
     * @param element
     */
    fun remove(element: E)
}