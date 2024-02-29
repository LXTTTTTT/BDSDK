package com.pancoit.mod_main.View

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.pancoit.mod_main.R

// 正在加载中
class LoadingDialog() : DialogFragment() {

    lateinit var title: String

    constructor(title: String) : this() {
        this.title = title
    }


    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            // 设置对话框背景为透明
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        isCancelable = false  // 不可取消
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view:View = inflater.inflate(R.layout.dialog_loading,container,false)
        var title : TextView = view.findViewById(R.id.title)
        title.text = this.title
        return view
    }

}