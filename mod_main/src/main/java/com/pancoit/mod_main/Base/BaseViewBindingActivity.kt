package com.pancoit.mod_main.Base

import android.view.LayoutInflater
import androidx.viewbinding.ViewBinding
import com.pancoit.mod_main.Extension.saveAs
import com.pancoit.mod_main.Extension.saveAsUnChecked
import java.lang.reflect.ParameterizedType

// ViewBinding 基类
abstract class BaseViewBindingActivity<VB : ViewBinding> : BaseActivity() {

    lateinit var viewBinding : VB

    // 重写设置布局方法
    override fun setActivityLayout() {
        // 通过反射获取到对应的 Binding 对象并拿到他的 Binding.inflate(layoutInflater) 方法执行
        val type = javaClass.genericSuperclass  // getClass().getGenericSuperclass();
        // 拿到 ViewBinding 类对象
        val vbClass: Class<VB> = type!!.saveAs<ParameterizedType>().actualTypeArguments[0].saveAs()  // genericSuperclass 强转为 ParameterizedType
        // 拿到 ViewBinding 类的inflate方法
        val method = vbClass.getDeclaredMethod("inflate", LayoutInflater::class.java)
        // 执行 ViewBinding.inflate(getLayoutInflater()); 前面的变量已声明类型VB所以不需要再指定<VB>
        viewBinding = method.invoke(this, layoutInflater)!!.saveAsUnChecked()
        setContentView(viewBinding.root)  // 设置布局
    }

    override fun setLayout(): Any? {return null}  // 直接返回 null 就好了

}
