package com.pancoit.bdsdk.TaskDispatch.Task

import android.os.Looper
import android.os.Process
import androidx.core.os.TraceCompat
import com.pancoit.bdsdk.TaskDispatch.Dispatcher.TaskDispatcher
import com.pancoit.bdsdk.TaskDispatch.State.TaskStat
import com.pancoit.mod_main.Utils.LogUtils


// 任务执行处
class DispatchRunnable : Runnable {
    private var mTask: Task
    private var mTaskDispatcher: TaskDispatcher? = null

    constructor(task: Task) {
        mTask = task
    }

    constructor(task: Task, dispatcher: TaskDispatcher?) {
        mTask = task
        mTaskDispatcher = dispatcher
    }

    override fun run() {
        TraceCompat.beginSection(mTask.javaClass.simpleName)
        LogUtils.i("${mTask.javaClass.simpleName}开始执行 | Situation：${TaskStat.currentSituation}")
        Process.setThreadPriority(mTask.priority())
        var startTime = System.currentTimeMillis()
        mTask.isWaiting = true
        mTask.waitToSatisfy()
        val waitTime = System.currentTimeMillis() - startTime
        startTime = System.currentTimeMillis()

        // 执行Task
        mTask.isRunning = true
        mTask.run()

        // 执行Task的尾部任务
        val tailRunnable = mTask.tailRunnable
        tailRunnable?.run()
        if (!mTask.needCall() || !mTask.runOnMainThread()) {
            printTaskLog(startTime, waitTime)
            TaskStat.markTaskDone()
            mTask.isFinished = true
            mTaskDispatcher?.let {
                it.satisfyChildren(mTask)
                it.markTaskDone(mTask)
            }
            LogUtils.i("${mTask.javaClass.simpleName} finish")
        }
        TraceCompat.endSection()
    }

    /**
     * 打印出来Task执行的日志
     *
     * @param startTime
     * @param waitTime
     */
    private fun printTaskLog(startTime: Long, waitTime: Long) {
        val runTime = System.currentTimeMillis() - startTime
        if (LogUtils.isDebug) {
            LogUtils.i(
            mTask.javaClass.simpleName + "| wait：" + waitTime + "| run："
                    + runTime + "| isMain：" + (Looper.getMainLooper() == Looper.myLooper())
                    + "| needWait：" + (mTask.needWait() || Looper.getMainLooper() == Looper.myLooper())
                    + "| ThreadId：" + Thread.currentThread().id
                    + "| ThreadName：" + Thread.currentThread().name
                    + "| Situation：" + TaskStat.currentSituation
            )
        }
    }
}