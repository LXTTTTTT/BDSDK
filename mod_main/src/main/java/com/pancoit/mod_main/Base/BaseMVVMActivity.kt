package com.bdtx.mod_main.Base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import com.bdtx.mod_util.Utils.ApplicationUtils
import java.lang.reflect.ParameterizedType

// MVVM 基类
abstract class BaseMVVMActivity<VB : ViewBinding ,VM : ViewModel>(global_model:Boolean = false) : BaseViewBindingActivity<VB>(){

    val global = global_model  // 是否全局使用的 ViewModel
    lateinit var viewModel: VM

    // 第一次重写，子类还要重写一次并且必须调用super方法执行下面的初始化
    override fun initData() {
        // 反射创建 ViewModel 对象
        val argument = (this.javaClass.genericSuperclass as ParameterizedType).actualTypeArguments

        if(global){loge("使用全局ViewModel")}else{loge("使用临时ViewModel")}
        viewModel = if(global)
            ApplicationUtils.getGlobalViewModel(argument[1] as Class<VM>)!!  // 使用全局 ViewModel
        else
            ViewModelProvider(this).get(argument[1] as Class<VM>)

//        viewModel = ViewModelProvider(this).get(argument[1] as Class<VM>)
//        viewModel = ViewModelProvider.AndroidViewModelFactory(ApplicationUtil.getApplication()).create(argument[1] as Class<VM>)
    }

}