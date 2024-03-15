package com.pancoit.mod_main.Base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import com.pancoit.mod_main.Utils.ApplicationUtils
import java.lang.reflect.ParameterizedType

abstract class BaseMVVMFragment<VB : ViewBinding, VM : ViewModel>(global_model:Boolean = false) : BaseViewBindingFragment<VB>() {

    val global = global_model  // 是否全局使用的 ViewModel
    lateinit var viewModel: VM

    override fun initData() {
        val argument = (this.javaClass.genericSuperclass as ParameterizedType).actualTypeArguments
        if(global){loge("使用全局ViewModel")}else{loge("使用临时ViewModel")}
        viewModel = if(global)
            ApplicationUtils.getGlobalViewModel(argument[1] as Class<VM>)!!  // 使用全局 ViewModel
        else
            ViewModelProvider(requireActivity()).get(argument[1] as Class<VM>)
    }

}