package com.pancoit.mod_main.Base

import android.app.Application
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.pancoit.mod_main.Utils.ApplicationUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

abstract class BaseFragment:Fragment() {

    private var TAG:String = javaClass.name
    val APP : Application by lazy { ApplicationUtils.getApplication() }
    lateinit var my_context: Context;
    lateinit var title: TextView;
    lateinit var back: ImageView;
    lateinit var layoutView:View;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: 打开 Fragment")
        setHasOptionsMenu(true)  // 开启菜单项
        beforeSetLayout()
        my_context = requireContext()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        layoutView = if(setLayout() is Int) inflater.inflate((setLayout() as Int),container,false) else (setLayout() as View)  // 使用 R.layout 或 ViewBinding
        return layoutView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getView()?.let { initView(it) }  // 初始化控件事件
        CoroutineScope(Dispatchers.Main).launch{
            initData();  // 初始化数据
            withContext(Dispatchers.IO){
                initDataSuspend()
            }
        }
    }

    abstract fun beforeSetLayout()
    abstract fun setLayout():Any?
    abstract fun initView(view: View)
    abstract fun initData()
    abstract suspend fun initDataSuspend()

    fun loge(log:String){
        Log.e(TAG, log )
    }


    override fun onDestroy() {
        Log.d(TAG, "onCreate: 关闭 Fragment")
        super.onDestroy()
    }
}