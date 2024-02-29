package com.bdtx.main.Task

import android.util.Log
import androidx.collection.ArraySet


// 任务排序工具
object TaskSortUtil {
    val TAG = "TaskSortUtil"
    // 高优先级的Task
    private val sNewTasksHigh: MutableList<Task> = ArrayList()

    // 排序
    @Synchronized
    fun getSortResult(
        originTasks: List<Task>,
        clsLaunchTasks: List<Class<out Task>>
    ): List<Task> {
        val makeTime = System.currentTimeMillis()
        val dependSet: MutableSet<Int> = ArraySet()
        val graph = DirectionGraph(originTasks.size)

        for (i in originTasks.indices) {
            val task = originTasks[i]
            if (task.isSend || task.dependsOn().isNullOrEmpty()) {
                continue
            }

            task.dependsOn()?.let { list ->
                for (clazz in list) {
                    clazz?.let { cls ->
                        val indexOfDepend = getIndexOfTask(originTasks, clsLaunchTasks, cls)
                        check(indexOfDepend >= 0) {
//                            task.javaClass.simpleName +
//                                    " depends on " + cls?.simpleName + " can not be found in task list "
                            task.javaClass.simpleName +
                                    " 依赖于 " + cls?.simpleName + " 无法被找到 "
                        }
                        dependSet.add(indexOfDepend)
                        graph.addEdge(indexOfDepend, i)
                    }
                }
            }
        }

        val indexList: List<Int> = graph.topologicalSort()
        val newTasksAll = getResultTasks(originTasks, dependSet, indexList)
        Log.i(TAG,"任务执行时间：" + (System.currentTimeMillis() - makeTime))
        printAllTaskName(newTasksAll, true)
        return newTasksAll
    }

    /**
     * 获取最终任务列表
     */
    private fun getResultTasks(
        originTasks: List<Task>,
        dependSet: Set<Int>,
        indexList: List<Int>
    ): List<Task> {
        val newTasksAll: MutableList<Task> = ArrayList(originTasks.size)
        // 被其他任务依赖的
        val newTasksDepended: MutableList<Task> = ArrayList()
        // 没有依赖的
        val newTasksWithOutDepend: MutableList<Task> = ArrayList()
        // 需要提升自己优先级的，先执行（这个先是相对于没有依赖的先）
        val newTasksRunAsSoon: MutableList<Task> = ArrayList()

        for (index in indexList) {
            if (dependSet.contains(index)) {
                newTasksDepended.add(originTasks[index])
            } else {
                val task = originTasks[index]
                if (task.needRunAsSoon()) {
                    newTasksRunAsSoon.add(task)
                } else {
                    newTasksWithOutDepend.add(task)
                }
            }
        }
        // 顺序：被别人依赖的————》需要提升自己优先级的————》需要被等待的————》没有依赖的
        sNewTasksHigh.addAll(newTasksDepended)
        sNewTasksHigh.addAll(newTasksRunAsSoon)
        newTasksAll.addAll(sNewTasksHigh)
        newTasksAll.addAll(newTasksWithOutDepend)
        return newTasksAll
    }

    private fun printAllTaskName(newTasksAll: List<Task>, isPrintName: Boolean) {
        if (!isPrintName) {
            return
        }
        for (task in newTasksAll) {
            Log.i(TAG,"需执行任务：" + task.javaClass.simpleName)
        }
    }

    val tasksHigh: List<Task>
        get() = sNewTasksHigh


    // 获取任务在任务列表中的index
    private fun getIndexOfTask(
        originTasks: List<Task>,
        clsLaunchTasks: List<Class<out Task>>,
        cls: Class<*>
    ): Int {
        val index = clsLaunchTasks.indexOf(cls)
        if (index >= 0) {
            return index
        }

        // 仅仅是保护性代码
        val size = originTasks.size
        for (i in 0 until size) {
            if (cls.simpleName == originTasks[i].javaClass.simpleName) {
                return i
            }
        }
        return index
    }
}