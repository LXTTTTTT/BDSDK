package com.pancoit.bdsdk.TaskDispatch.Dispatcher

import android.app.Application
import android.os.Looper
import android.util.Log
import androidx.annotation.UiThread
import com.pancoit.bdsdk.TaskDispatch.Task.DispatchRunnable
import com.pancoit.bdsdk.TaskDispatch.State.StaterUtils
import com.pancoit.bdsdk.TaskDispatch.Task.Task
import com.pancoit.bdsdk.TaskDispatch.Task.TaskCallBack
import com.pancoit.bdsdk.TaskDispatch.Sort.TaskSortUtil
import com.pancoit.bdsdk.TaskDispatch.State.TaskStat
import com.pancoit.mod_main.Utils.LogUtils
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger


// 任务调度器
class TaskDispatcher private constructor() {
    private var mStartTime: Long = 0
    private val mFutures: MutableList<Future<*>> = ArrayList()
    private var mAllTasks: MutableList<Task> = ArrayList()  // 所有需要执行的任务
    private val mClsAllTasks: MutableList<Class<out Task>> = ArrayList()

    @Volatile
    private var mMainThreadTasks: MutableList<Task> = ArrayList()
    private var mCountDownLatch: CountDownLatch? = null

    //保存需要Wait的Task的数量
    private val mNeedWaitCount = AtomicInteger()

    //调用了await的时候还没结束的且需要等待的Task
    private val mNeedWaitTasks: MutableList<Task> = ArrayList()

    //已经结束了的Task
    @Volatile
    private var mFinishedTasks: MutableList<Class<out Task>> = ArrayList(100)
    private val mDependedHashMap = HashMap<Class<out Task>, ArrayList<Task>?>()

    //启动器分析的次数，统计下分析的耗时
    private val mAnalyseCount = AtomicInteger()

    fun addTask(task: Task?): TaskDispatcher {
        task?.let {
            collectDepends(it)
            mAllTasks.add(it)
            mClsAllTasks.add(it.javaClass)
            // 非主线程且需要wait的，主线程不需要CountDownLatch也是同步的
            if (ifNeedWait(it)) {
                mNeedWaitTasks.add(it)
                mNeedWaitCount.getAndIncrement()
            }
        }
        return this
    }

    private fun collectDepends(task: Task) {
        task.dependsOn()?.let { list ->
            for (cls in list) {
                cls?.let { cls ->
                    if (mDependedHashMap[cls] == null) {
                        mDependedHashMap[cls] = ArrayList()
                    }
                    mDependedHashMap[cls]?.add(task)
                    if (mFinishedTasks.contains(cls)) {
                        task.satisfy()
                    }
                }
            }
        }
    }

    private fun ifNeedWait(task: Task): Boolean {
        return !task.runOnMainThread() && task.needWait()
    }

    @UiThread
    fun start() {
        mStartTime = System.currentTimeMillis()  // 开始任务的时间
        if (Looper.getMainLooper() != Looper.myLooper()) {
            throw RuntimeException("要在主线程调用")
        }

        // 任务不为空时
        if (!mAllTasks.isNullOrEmpty()) {
            mAnalyseCount.getAndIncrement()  // 返回原值然后+1
            printDependedMsg(false)  // 打印依赖的任务信息
            mAllTasks = TaskSortUtil.getSortResult(mAllTasks, mClsAllTasks).toMutableList()  // 任务排序
            mCountDownLatch = CountDownLatch(mNeedWaitCount.get())  // 创建倒计时计数器
            sendAndExecuteAsyncTasks()
            LogUtils.i("预计消耗时间： ${(System.currentTimeMillis() - mStartTime)} 开始执行主任务 ")
            executeTaskMain()
        }
        LogUtils.i("预计消耗时间 ${(System.currentTimeMillis() - mStartTime)}")
    }

    fun cancel() {
        for (future in mFutures) {
            future.cancel(true)
        }
    }

    private fun executeTaskMain() {
        mStartTime = System.currentTimeMillis()
        for (task in mMainThreadTasks) {
            val time = System.currentTimeMillis()
            LogUtils.i(
                "任务 ${task.javaClass.simpleName} 消耗 ${(System.currentTimeMillis() - time)}"
            )
        }
        LogUtils.i("任务消耗 ${(System.currentTimeMillis() - mStartTime)}")
    }

    private fun sendAndExecuteAsyncTasks() {
        for (task in mAllTasks) {
            // 如果任务只能在主线程执行并且当前不处于主线程
            if (task.onlyInMainProcess() && !isMainProcess) {
                markTaskDone(task)
            } else {
                sendTaskReal(task)
            }
            task.isSend = true
        }
    }


    // 被依赖信息
    private fun printDependedMsg(isPrintAllTask: Boolean) {
        LogUtils.e("需要等待的任务数量 : ${mNeedWaitCount.get()}")
        if (isPrintAllTask) {
            for (cls in mDependedHashMap.keys) {
                LogUtils.e("cls: ${cls.simpleName} ${mDependedHashMap[cls]?.size}")
                mDependedHashMap[cls]?.let {
                    for (task in it) {
                        LogUtils.e("cls:${task.javaClass.simpleName}")
                    }
                }
            }
        }
    }

    // 通知Children一个前置任务已完成
    fun satisfyChildren(launchTask: Task) {
        val arrayList = mDependedHashMap[launchTask.javaClass]
        if (!arrayList.isNullOrEmpty()) {
            for (task in arrayList) {
                task.satisfy()
            }
        }
    }

    fun markTaskDone(task: Task) {
        if (ifNeedWait(task)) {
            mFinishedTasks.add(task.javaClass)
            mNeedWaitTasks.remove(task)
            mCountDownLatch?.countDown()  // 开始倒计时
            mNeedWaitCount.getAndDecrement()
        }
    }

    private fun sendTaskReal(task: Task) {
        if (task.runOnMainThread()) {
            mMainThreadTasks.add(task)
            if (task.needCall()) {
                task.setTaskCallBack(object : TaskCallBack {
                    override fun call() {
                        TaskStat.markTaskDone()
                        task.isFinished = true
                        satisfyChildren(task)
                        markTaskDone(task)
                        LogUtils.i("${task.javaClass.simpleName} finish")
                        Log.i("测试", "---")
                    }
                })
            }
        } else {
            // 直接发，是否执行取决于具体线程池
            val future = task.runOn()?.submit(DispatchRunnable(task, this))  // 使用线程池执行任务
            // 这里直到
            future?.let {
                mFutures.add(it)
            }
        }
    }

    fun executeTask(task: Task) {
        if (ifNeedWait(task)) {
            mNeedWaitCount.getAndIncrement()
        }
        task.runOn()?.execute(DispatchRunnable(task, this))
    }

    @UiThread
    fun await() {
        try {
            if (LogUtils.isDebug) {
                LogUtils.e("还有任务数量： ${mNeedWaitCount.get()}")
                for (task in mNeedWaitTasks) {
                    LogUtils.e("需要等待: ${task.javaClass.simpleName}")
                }
            }
            if (mNeedWaitCount.get() > 0) {
                mCountDownLatch?.await(WAIT_TIME.toLong(), TimeUnit.MILLISECONDS)  // 阻塞当前线程，直到计数器减到零或者超过等待时间
            }
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    companion object {
        private const val WAIT_TIME = 10000
        var context: Application? = null
            private set
        var isMainProcess = false
            private set

        @Volatile
        private var sHasInit = false

        fun init(context: Application?) {
            context?.let {
                Companion.context = it
                sHasInit = true
                isMainProcess = StaterUtils.isMainProcess(context)
            }
        }

        /**
         * 注意：每次获取的都是新对象
         * @return
         */
        fun createInstance(): TaskDispatcher {
            if (!sHasInit) {
                throw RuntimeException("需要先执行初始化 TaskDispatcher.init")
            }
            return TaskDispatcher()
        }
    }
}