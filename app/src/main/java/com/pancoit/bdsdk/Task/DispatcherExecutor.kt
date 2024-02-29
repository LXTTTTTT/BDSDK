package com.bdtx.main.Task

import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicInteger

/**
 * 线程调度池
 */
// 线程池管理器
object DispatcherExecutor {

    private val CPU_COUNT = Runtime.getRuntime().availableProcessors()  // 获取当前设备的 CPU 数量
    private val CORE_POOL_SIZE = 2.coerceAtLeast((CPU_COUNT - 1).coerceAtMost(5))  // 设置核心线程池大小和最大线程池大小
    private val MAXIMUM_POOL_SIZE = CORE_POOL_SIZE

    // We want at least 2 threads and at most 4 threads in the core pool,
    // preferring to have 1 less than the CPU count to avoid saturating
    // the CPU with background work
    private const val KEEP_ALIVE_SECONDS = 5  // 非核心线程的闲置超时时间
    private val sPoolWorkQueue: BlockingQueue<Runnable> = LinkedBlockingQueue()  // 用于保存等待执行任务的队列
    private val sThreadFactory = DefaultThreadFactory()  // 自定义线程工厂
    // 用于处理被拒绝的任务的处理器
    private val sHandler = RejectedExecutionHandler { r, executor ->
        // 一般不会到这里
        Executors.newCachedThreadPool().execute(r)
    }

    /**
     * 获取CPU线程池
     * @return
     */
    var cPUExecutor: ThreadPoolExecutor? = null
        private set

    /**
     * 获取IO线程池
     * @return
     */
    var iOExecutor: ExecutorService? = null
        private set

    /**
     * The default thread factory.
     */
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
        cPUExecutor = ThreadPoolExecutor(
            CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_SECONDS.toLong(), TimeUnit.SECONDS,
            sPoolWorkQueue, sThreadFactory, sHandler
        )
        cPUExecutor?.allowCoreThreadTimeOut(true)
        iOExecutor = Executors.newCachedThreadPool(sThreadFactory)
    }
}