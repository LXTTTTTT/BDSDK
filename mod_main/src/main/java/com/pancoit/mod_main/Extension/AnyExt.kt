package com.bdtx.mod_util.Extension

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle

// 扩展函数 ------------------------------------

// 强转，内联降低性能开销
inline fun <reified T> Any.saveAs() : T{
    return this as T
}

// 强转2
@Suppress("UNCHECKED_CAST")
fun <T> Any.saveAsUnChecked() : T{
    return this as T
}

// 判断类型
inline fun <reified T> Any.isEqualType() : Boolean{
    return this is T
}

// 带参数跳转Activity  如：startActivity<TestActivity>()
inline fun <reified T : Activity> Context.startActivity(data: Bundle? = null) {
    val intent = Intent(this, T::class.java)
    if (data != null) {
        intent.putExtras(data)
    }
    this.startActivity(intent)
}