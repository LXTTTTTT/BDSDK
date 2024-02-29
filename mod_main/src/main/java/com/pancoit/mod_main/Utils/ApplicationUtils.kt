package com.bdtx.mod_util.Utils

import android.app.Application
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.pancoit.mod_main.Utils.ActivityManagementUtils

// 应用程序工具
object ApplicationUtils {
    private lateinit var app: Application
    private var isDebug = false

    fun init(application: Application, isDebug: Boolean) {
        app = application
        ApplicationUtils.isDebug = isDebug
    }

    // 获取全局应用
    fun getApplication() = app

    // 当前是否为debug环境
    fun isDebug() = isDebug

    // 获取全局单例 viewModel
    private var mainVM : ViewModel? = null
    fun <T : ViewModel> getGlobalViewModel(viewModelClass: Class<T>) : T{
        if(mainVM==null){
            mainVM = ViewModelProvider.AndroidViewModelFactory(app).create(viewModelClass)
        }
        return mainVM as T
    }

    // 获取全局 viewModel
    inline fun <reified T : ViewModel> getGlobalViewModel(): T? {
        val activity = ActivityManagementUtils.getInstance().top() as FragmentActivity
        return ViewModelProvider(activity).get(T::class.java)
    }
    fun <T : ViewModel> getGlobalViewModelJava(viewModelClass: Class<T>): T? {
        val activity = ActivityManagementUtils.getInstance().top() as FragmentActivity
        return ViewModelProvider(activity).get(viewModelClass)
    }

}