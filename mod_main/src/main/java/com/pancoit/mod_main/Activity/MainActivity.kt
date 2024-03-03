package com.pancoit.mod_main.Activity

import android.os.Bundle
import com.pancoit.mod_main.Base.BaseMVVMActivity
import com.pancoit.mod_main.Utils.GlobalControlUtils
import com.pancoit.mod_main.ViewModel.MainVM
import com.pancoit.mod_main.databinding.ActivityMainBinding


class MainActivity : BaseMVVMActivity<ActivityMainBinding, MainVM>(true) {

    override fun beforeSetLayout() {}
    override fun enableEventBus(): Boolean { return true }
    override fun initView(savedInstanceState: Bundle?) {
        init_control()
    }
    override suspend fun initDataSuspend() {}
    override fun initData() {
        super.initData()  // 在父类初始化 viewModel
        init_view_model()
    }

    fun init_control(){


    }

    fun init_view_model(){

    }

    // 双击进入后台
    private var lastClickTime: Long = 0L
    override fun onBackPressed() {
        if (System.currentTimeMillis() - lastClickTime > 2000) {
            GlobalControlUtils.showToast("再按一次返回桌面",0)
            lastClickTime = System.currentTimeMillis()
        } else {
            moveTaskToBack(true)
        }
    }



    override fun onDestroy() {
        super.onDestroy()
    }

}