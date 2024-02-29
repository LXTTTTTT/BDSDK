package com.bdtx.main.Task

import android.os.Looper
import android.os.Process
import android.util.Log
import androidx.core.os.TraceCompat


// 执行任务的地方
class DispatchRunnable : Runnable {
    val TAG = "DispatchRunnable"
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
        Log.i(TAG,"${mTask.javaClass.simpleName}开始执行 | Situation：${TaskStat.currentSituation}")
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
            Log.i(TAG,"${mTask.javaClass.simpleName} finish")
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
        Log.i(TAG,
            mTask.javaClass.simpleName + "| wait：" + waitTime + "| run："
                    + runTime + "| isMain：" + (Looper.getMainLooper() == Looper.myLooper())
                    + "| needWait：" + (mTask.needWait() || Looper.getMainLooper() == Looper.myLooper())
                    + "| ThreadId：" + Thread.currentThread().id
                    + "| ThreadName：" + Thread.currentThread().name
                    + "| Situation：" + TaskStat.currentSituation
        )
    }
}