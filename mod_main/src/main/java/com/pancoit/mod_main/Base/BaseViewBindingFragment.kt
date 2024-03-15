package com.pancoit.mod_main.Base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.pancoit.mod_main.Extension.saveAs
import com.pancoit.mod_main.Extension.saveAsUnChecked
import java.lang.reflect.ParameterizedType

abstract class BaseViewBindingFragment<VB : ViewBinding> : BaseFragment() {

    lateinit var viewBinding : VB

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val type = javaClass.genericSuperclass
        val vbClass: Class<VB> = type!!.saveAs<ParameterizedType>().actualTypeArguments[0].saveAs()
        val method = vbClass.getDeclaredMethod("inflate", LayoutInflater::class.java)
        viewBinding = method.invoke(this, layoutInflater)!!.saveAsUnChecked()
        return viewBinding.root
    }

    override fun setLayout(): Any? { return null }

}