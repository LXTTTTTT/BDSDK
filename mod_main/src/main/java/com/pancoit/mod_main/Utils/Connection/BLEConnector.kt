package com.pancoit.mod_main.Utils.Connection

import android.bluetooth.BluetoothDevice
import android.util.Log
import com.pancoit.mod_bluetooth.BLE.BluetoothTransfer
import com.pancoit.mod_main.Utils.ApplicationUtils
import com.pancoit.mod_main.Utils.Connection.BaseConnector
import com.pancoit.mod_main.Utils.GlobalControlUtils
import com.pancoit.mod_main.ViewModel.MainVM
import com.pancoit.mod_parse.Protocol.ProtocolPackager
import kotlinx.coroutines.*

class BLEConnector: BaseConnector() {

    init {
        TAG = "BLEConnector"
        Log.e(TAG, "低功耗蓝牙连接器" )
    }

    override fun connect(device: Any) {
        if(device !is BluetoothDevice){
            Log.e(TAG, "非法对象")
            return
        }
        connectDeviceWithCondition(
            device,
            before = {
                GlobalControlUtils.showLoadingDialog("正在连接");
            },
            // 蓝牙连接前置条件
            condition = object :()->Boolean{
                override fun invoke(): Boolean {
                    return true
                }
            },
            connectWithTransfer = {
                // 连接蓝牙
                BluetoothTransfer.getInstance().connectDevice(it)
                return@connectDeviceWithCondition true
            },
            success = {
                // 连接成功操作
                // ble连接之后还要进行分析/选择服务，无法在连接的第一时间判断设备是否连接成功，在别的地方进行成功/失败处理
            },
            fail = {
                // 连接失败操作
                disconnect()
            }
        )
    }

    override fun disconnect(){
        BluetoothTransfer.getInstance().disconnect()  // 断开蓝牙
        BaseConnector.setConnector(null)  // 初始化连接器
    }

    override suspend fun getDevices(): List<Any>? {
        return null
    }

    override fun initDevice() {
        GlobalScope.launch(Dispatchers.Main) {
            withContext(Dispatchers.IO){
                delay(500)
                BluetoothTransfer.getInstance().writeHex(ProtocolPackager.CCPWD(5,"000020"))
                delay(300)
                BluetoothTransfer.getInstance().writeHex(ProtocolPackager.CCICR(0,"00"))
                delay(300)
                BluetoothTransfer.getInstance().writeHex(ProtocolPackager.CCRMO("PWI",2,5))
                delay(300)
                BluetoothTransfer.getInstance().writeHex(ProtocolPackager.CCZDC(5))
                delay(300)
                BluetoothTransfer.getInstance().writeHex(ProtocolPackager.CCPRS("15950044",0,0,0,0))
                delay(300)
                BluetoothTransfer.getInstance().writeHex(ProtocolPackager.CCRNS(5,5,5,5,5,5))
                delay(300)
                BluetoothTransfer.getInstance().writeHex(ProtocolPackager.CCRMO("MCH",1,0))
            }
        }

    }


    // 实现发送消息
    override fun sendMessage(targetCardNumber: String, type: Int, content_str: String) {
        BluetoothTransfer.getInstance().writeHex(ProtocolPackager.CCTCQ(targetCardNumber,type,content_str))
    }
}