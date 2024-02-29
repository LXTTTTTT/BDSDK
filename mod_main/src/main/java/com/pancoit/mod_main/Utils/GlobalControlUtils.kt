package com.pancoit.mod_main.Utils

import android.app.AlertDialog
import android.app.Application
import android.content.Context
import android.media.AudioManager
import android.media.RingtoneManager
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.bdtx.mod_util.Utils.ApplicationUtils
import com.pancoit.mod_main.View.LoadingDialog

// 全局控件工具
object GlobalControlUtils {

    val TAG = "GlobalControlUtil"
    val APP : Application by lazy { ApplicationUtils.getApplication() }
//    val APP : Application = ApplicationUtil.getApplication()

    // 控件 ------------------------------------------
    var my_toast : Toast? = null
    var loading_dialog : LoadingDialog? = null  // 加载框
    var alertDialog : AlertDialog? = null  // 警告框
    // 其他 -----------------------------------------
    val audioManager:AudioManager? = APP.getSystemService(Context.AUDIO_SERVICE) as AudioManager  // 系统音频管理器


    // 全局唯一 Toast 方法：0 - 短 ， 1 - 长
    fun showToast(msg: String, length: Int) {
        ActivityManagementUtils.getInstance().top()?.let {
            it.runOnUiThread(Runnable {
                my_toast?.let {
                    my_toast!!.cancel()
                    my_toast = null
                }
                my_toast = if (length == 0) { Toast.makeText(it, msg, Toast.LENGTH_SHORT) }
                else { Toast.makeText(it, msg, Toast.LENGTH_LONG) }
                my_toast?.show()
                Log.e(TAG, "showToast：$msg")
            })
        }
    }

    // 显示加载中
    fun showLoadingDialog(title : String){
        ActivityManagementUtils.getInstance().top()?.let {
            loading_dialog = LoadingDialog(title)
            loading_dialog!!.show((it as FragmentActivity).supportFragmentManager,"")
        }
    }
    fun hideLoadingDialog(){
        loading_dialog?.let {
            loading_dialog!!.dismiss()
            loading_dialog = null
        }
    }

    // 显示警告框
    fun showAlertDialog(title: String, message: String, onYesClick: (() -> Unit)? = null, onNoClick:(() -> Unit)? = null){
        ActivityManagementUtils.getInstance().top()?.let {
            alertDialog?.let { it.dismiss();alertDialog=null }
            alertDialog = AlertDialog.Builder(it)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("确定") { dialog, which ->
                    onYesClick?.let { onYesClick() }
                }
                .setNegativeButton(
                    "取消"
                ) { dialog, which ->
                    onNoClick?.let { onNoClick() }
                }.create()
            alertDialog?.show()
        }
    }

    // 播放系统提示音
    fun ringBell(){
        audioManager?.let {
            if(audioManager.ringerMode == AudioManager.RINGER_MODE_NORMAL){
                // 播放铃声
                val defaultNotificationUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                val defaultNotificationRingtone = RingtoneManager.getRingtone(APP, defaultNotificationUri)
                defaultNotificationRingtone?.play()
            }
        }
    }
}