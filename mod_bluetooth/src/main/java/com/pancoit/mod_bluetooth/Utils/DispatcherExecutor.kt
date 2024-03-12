package com.pancoit.mod_bluetooth.Utils

import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicInteger

// 线程池管理器
object DispatcherExecutor {

    private val CPU_COUNT = Runtime.getRuntime().availableProcessors()  // 获取当前设备的 CPU 数量
    private val CORE_POOL_SIZE = 2.coerceAtLeast((CPU_COUNT - 1).coerceAtMost(5))  // 设置核心线程池大小和最大线程池大小
    private val MAXIMUM_POOL_SIZE = CORE_POOL_SIZE

    // 核心池中至少有2个线程，最多5个线程，最好比CPU数量少1个，以避免后台工作使CPU满载
    private const val KEEP_ALIVE_SECONDS = 5  // 非核心线程的闲置超时时间
    private val workQueue: BlockingQueue<Runnable> = LinkedBlockingQueue()  // 用于保存等待执行任务的队列
    private val threadFactory = DefaultThreadFactory()  // 自定义线程工厂
    // 用于处理被拒绝的任务的处理器
    private val Handler = RejectedExecutionHandler { r, executor ->
        // 一般不会到这里
        Executors.newCachedThreadPool().execute(r)
    }

    /**
     * CPU 线程池：
     * 用于处理 CPU 密集型的任务，例如计算、数据处理等。
     * 核心线程数一般设置为 CPU 核心数，以避免线程过多导致的上下文切换和资源消耗。
     * 任务执行时间较长，线程池中的线程会一直占用 CPU 资源，直到任务完成。
     * 通常采用固定大小的线程池来避免线程数量过多，导致资源争夺和性能下降。
     * IO 线程池：
     * 用于处理 IO 密集型的任务，例如文件读写、网络通信等。
     * 核心线程数一般较少，可以根据具体需求动态调整线程数量。
     * 任务执行时间较释短，线程池中的线程会在完成任务后放 CPU 资源，避免资源浪费。
     * 通常采用可缓存的线程池来处理，线程数量根据任务需求动态增减，适应不同的负载情况。
     * */
    // CPU线程池
    var CPUExecutor: ThreadPoolExecutor? = null
        private set

    // IO线程池
    var IOExecutor: ExecutorService? = null
        private set


    private class DefaultThreadFactory : ThreadFactory {
        private val group: ThreadGroup?
        private val threadNumber = AtomicInteger(1)
        private val namePrefix: String
        override fun newThread(r: Runnable): Thread {
            val t = Thread(
                group, r,
                namePrefix + threadNumber.getAndIncrement(),
                0
            )
            if (t.isDaemon) {
                t.isDaemon = false
            }
            if (t.priority != Thread.NORM_PRIORITY) {
                t.priority = Thread.NORM_PRIORITY
            }
            return t
        }

        companion object {
            private val poolNumber = AtomicInteger(1)
        }

        init {
            val s = System.getSecurityManager()
            group = s?.threadGroup ?: Thread.currentThread().threadGroup ?: null
            namePrefix = "TaskDispatcherPool-${poolNumber.getAndIncrement()}-Thread-"
        }
    }

    init {
        CPUExecutor = ThreadPoolExecutor(
            CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_SECONDS.toLong(), TimeUnit.SECONDS,
            workQueue, threadFactory, Handler
        )
        CPUExecutor?.allowCoreThreadTimeOut(true)
        IOExecutor = Executors.newCachedThreadPool(threadFactory)
    }

    fun destroy(){
        CPUExecutor?.shutdown()
        IOExecutor?.shutdown()
    }

}