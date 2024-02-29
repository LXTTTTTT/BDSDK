package com.bdtx.main

import android.app.Application
import android.util.Log
import com.alibaba.android.arouter.launcher.ARouter
import com.bdtx.main.Task.DispatcherExecutor
import com.bdtx.main.Task.Task
import com.bdtx.mod_util.Utils.ApplicationUtils
import com.pancoit.bdsdk.BuildConfig
import com.pancoit.mod_main.Global.Variable
import com.pancoit.mod_main.Utils.CatchExceptionUtils
import com.tencent.mmkv.MMKV
import com.tencent.mmkv.MMKVLogLevel
import java.util.concurrent.ExecutorService

// 任务
val TAG = "MyTask"
// 初始化全局APP工具
class InitAppUtilTask(val application: Application) : Task() {

    // 异步线程执行的Task在被调用await的时候等待
    override fun needWait(): Boolean {
        return true
    }

    override fun run() {
        ApplicationUtils.init(application, BuildConfig.DEBUG)
        Log.e(TAG, "初始化APP工具" )
    }
}


// 初始化 MMKV
class InitMmkvTask() : Task() {

    // 异步线程执行的Task在被调用await的时候等待
    override fun needWait(): Boolean {
        return true
    }

    // 依赖某些任务，在某些任务完成后才能执行（初始化 ApplicationUtil 之后）
    override fun dependsOn(): MutableList<Class<out Task>> {
        val tasks = mutableListOf<Class<out Task?>>()
        tasks.add(InitAppUtilTask::class.java)  // MMKV初始化需要用到主程序
        return tasks
    }

    // 指定需要使用的线程池
    override fun runOn(): ExecutorService? {
        return DispatcherExecutor.iOExecutor
    }

    // 执行任务
    override fun run() {
        val rootDir: String = MMKV.initialize(ApplicationUtils.getApplication())
        MMKV.setLogLevel(
            if (BuildConfig.DEBUG) {
                MMKVLogLevel.LevelDebug
            } else {
                MMKVLogLevel.LevelError
            }
        )
        Log.e(TAG, "MMKV 初始化根目录: $rootDir")
    }
}


// 初始化 GreenDao
//class InitGreenDaoTask() : Task() {
//    // 异步线程执行的Task在被调用await的时候等待
//    override fun needWait(): Boolean {
//        return true
//    }
//
//    //依赖某些任务，在某些任务完成后才能执行
//    override fun dependsOn(): MutableList<Class<out Task>> {
//        val tasks = mutableListOf<Class<out Task?>>()
//        tasks.add(InitAppUtilTask::class.java)  // 需要用到主程序
//        return tasks
//    }
//
//    override fun run() {
//        DaoUtils.getInstance().init(ApplicationUtils.getApplication())
//        Log.e(TAG, "初始化GreenDao" )
//    }
//}


// 初始化 ARouter
class InitArouterTask() : Task() {
    // 异步线程执行的Task在被调用await的时候等待
    override fun needWait(): Boolean {
        return true
    }

    // 依赖某些任务，在某些任务完成后才能执行
    override fun dependsOn(): MutableList<Class<out Task>> {
        val tasks = mutableListOf<Class<out Task?>>()
        tasks.add(InitAppUtilTask::class.java)  // 好像全部都要用到主程序..
        return tasks
    }

    // 执行任务，任务真正的执行逻辑
    override fun run() {
        // 这两行必须写在init之前，否则这些配置在init过程中将无效
        if (BuildConfig.DEBUG) {
            // 开启打印日志
            ARouter.openLog()
            // 开启调试模式(如果在 InstantRun 模式下运行，必须开启调试模式！线上版本需要关闭,否则有安全风险)
            ARouter.openDebug()
        }
        ARouter.init(ApplicationUtils.getApplication())
        Log.e(TAG, "初始化ARouter" ); Variable.isARouterInit = true
    }
}

// 初始化中大压缩库
//class InitZDCompression() : Task() {
//    // 异步线程执行的Task在被调用await的时候等待
//    override fun needWait(): Boolean {
//        return true
//    }
//
//    //依赖某些任务，在某些任务完成后才能执行
//    override fun dependsOn(): MutableList<Class<out Task>> {
//        val tasks = mutableListOf<Class<out Task?>>()
//        tasks.add(InitAppUtilTask::class.java)
//        tasks.add(InitMmkvTask::class.java)  // 压缩库工具要用到 mmkv
//        return tasks
//    }
//
//    override fun run() {
//        ZDCompressionUtils.getInstance().initZipSdk()
//        Log.e(TAG, "初始化压缩库" )
//    }
//}

// 捕获异常注册
class InitCatchException() : Task() {
    // 异步线程执行的Task在被调用await的时候等待
    override fun needWait(): Boolean {
        return true
    }

    //依赖某些任务，在某些任务完成后才能执行
    override fun dependsOn(): MutableList<Class<out Task>> {
        val tasks = mutableListOf<Class<out Task?>>()
        tasks.add(InitAppUtilTask::class.java)
        return tasks
    }

    override fun run() {
        CatchExceptionUtils.getInstance().init(ApplicationUtils.getApplication())
        Log.e(TAG, "初始化捕获异常" )
    }
}

// 系统定位变化监听
//class InitSystemLocation() : Task() {
//    // 异步线程执行的Task在被调用await的时候等待
//    override fun needWait(): Boolean {
//        return true
//    }
//    //依赖某些任务，在某些任务完成后才能执行
//    override fun dependsOn(): MutableList<Class<out Task>> {
//        val tasks = mutableListOf<Class<out Task?>>()
//        tasks.add(InitAppUtilTask::class.java)
//        return tasks
//    }
//    override fun run() {
//        SystemLocationUtils.init(ApplicationUtils.getApplication())
//        Log.e(TAG, "初始化系统定位变化监听" )
//    }
//}

// 快捷创建其他任务 -------------------------------

// 初始化A
class InitTaskA() : Task() {
    override fun run() {
        //...
    }
}

// 初始化B
class InitTaskB() : Task() {
    override fun run() {
        //...
    }
}