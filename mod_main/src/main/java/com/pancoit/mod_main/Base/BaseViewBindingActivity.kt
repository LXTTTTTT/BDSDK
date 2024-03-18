package com.pancoit.mod_main.Base

import android.view.LayoutInflater
import androidx.viewbinding.ViewBinding
import com.pancoit.mod_main.Extension.saveAs
import com.pancoit.mod_main.Extension.saveAsUnChecked
import java.lang.reflect.ParameterizedType

// ViewBinding 基类
abstract class BaseViewBindingActivity<VB : ViewBinding> : BaseActivity() {

    lateinit var viewBinding : VB

    // 重写设置布局方法，反射获取对应 ViewBinding 对象
    override fun setActivityLayout() {
        val type = javaClass.genericSuperclass
        val vbClass: Class<VB> = type!!.saveAs<ParameterizedType>().actualTypeArguments[0].saveAs()  // genericSuperclass 强转为 ParameterizedType
        val method = vbClass.getDeclaredMethod("inflate", LayoutInflater::class.java)
        viewBinding = method.invoke(this, layoutInflater)!!.saveAsUnChecked()
        setContentView(viewBinding.root)  // 设置布局
    }

    override fun setLayout(): Any? {return null}  // 直接返回 null 就好了

}
