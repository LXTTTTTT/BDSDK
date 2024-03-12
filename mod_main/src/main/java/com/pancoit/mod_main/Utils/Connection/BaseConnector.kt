package com.pancoit.mod_main.Utils.Connection

import android.util.Log
import kotlinx.coroutines.*

// 通信链路<T：子通信对象>
abstract class BaseConnector {

    var TAG = "BaseConnector"
    var isConnected = false  // 连接状态

    companion object{
        var connector: BaseConnector? = null  // 全局唯一连接器
        @JvmName("setConnectorJava")
        fun setConnector(connector: BaseConnector?){
            Companion.connector = connector
        }
    }

    abstract fun connect(device:Any)  // 连接设备
    abstract fun disconnect()  // 断开连接
    abstract suspend fun getDevices():List<Any>?  // 获取可用设备
    abstract fun initDevice()  // 初始化设备（下发指令）
    abstract fun sendMessage(targetCardNumber:String, type:Int, content_str:String)  // 发送消息


    // 连接设备
    open fun <T> connectDevice(
        device: T,
        before: (()->Unit)?=null,  // 前置准备
        connectWithTransfer:(T)->Boolean,  // 连接操作
        success: (()->Unit)?=null,  // 成功
        fail: (()->Unit)?=null,  // 失败
    ){
        before?.let { it.invoke() }
        if(connectWithTransfer(device)){
            Log.e(TAG, "设备连接成功" )
            success?.let { it.invoke() }
        }else{
            Log.e(TAG, "设备连接失败" )
            fail?.let { it.invoke() }
        }
    }

    // 连接设备（前置条件）
    open fun <T> connectDeviceWithCondition(
        device: T,
        before: (()->Unit)?=null,  // 前置准备
        condition: (()->Boolean),  // 前置条件
        connectWithTransfer:(T)->Boolean,  // 连接操作
        conditionFail: (()->Unit)?=null,  // 条件检测失败
        success: (()->Unit)?=null,  // 成功
        fail: (()->Unit)?=null,  // 失败
    ){
        before?.let { it.invoke() }
        if(condition()){
            if(connectWithTransfer(device)){
                Log.e(TAG, "设备连接成功" )
                success?.let { it.invoke() }
            }else{
                Log.e(TAG, "设备连接失败" )
                fail?.let { it.invoke() }
            }
        }else{
            Log.e(TAG, "连接条件不足！")
            conditionFail?.let { it.invoke() }
        }
    }


    // 从所有设备的列表中筛选并根据条件自动连接
    open fun <T> connectDevice(
        before: (()->Unit)? = null,  // 前置准备
        search: (suspend ()->MutableList<T>?)?,  // 获取设备
        successHandler:(MutableList<T>?)->Unit,  // 获取设备成功处理
        after: (()->Unit)? = null  // 后置处理
    ){
        GlobalScope.launch(Dispatchers.Main) {
            before?.let { it.invoke() }
            search?.let {
                val devices = getDevicesWithCondition(search=it)
                devices?.let {
                    successHandler(it)
                }
            }
            after?.let { it.invoke() }
        }
    }

    // 获取可用设备列表
    open suspend fun <T> getDevicesWithCondition(
        before: (()->Unit)?=null,
        search: suspend ()->MutableList<T>?,
        after: (()->Unit)?=null
    ): MutableList<T>? {
        try {
            before?.let { it.invoke() }
            val devices = withContext(Dispatchers.IO){
                withTimeout(10*1000){
                    search()
                }
            } ?: return null
            after?.let { it.invoke() }
            return devices
        }catch (e:Exception){
            e.printStackTrace()
        }
        return null
    }


}