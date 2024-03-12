package com.pancoit.mod_main.Activity

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.launcher.ARouter
import com.pancoit.mod_main.Base.BaseMVVMActivity
import com.pancoit.mod_main.Global.Constant
import com.pancoit.mod_main.Global.Variable
import com.pancoit.mod_main.R
import com.pancoit.mod_main.Utils.Connection.BLEConnector
import com.pancoit.mod_main.Utils.Connection.BaseConnector
import com.pancoit.mod_main.Utils.GlobalControlUtils
import com.pancoit.mod_main.ViewModel.MainVM
import com.pancoit.mod_main.databinding.ActivityMainBinding
import com.pancoit.mod_parse.CallBack.ParameterListener
import com.pancoit.mod_parse.Parameter.*
import com.pancoit.mod_parse.Protocol.ProtocolParser
import com.tbruyelle.rxpermissions3.RxPermissions


class MainActivity : BaseMVVMActivity<ActivityMainBinding, MainVM>(true) {

    lateinit var rxPermissions : RxPermissions
    override fun beforeSetLayout() {}
    override fun enableEventBus(): Boolean { return true }
    override fun initView(savedInstanceState: Bundle?) {
        rxPermissions = RxPermissions(this)
        init_control()
    }
    override suspend fun initDataSuspend() {}
    override fun initData() {
        super.initData()  // 在父类初始化 viewModel
        init_view_model()
        init_parser_callback()
    }

    fun init_control(){
        // 连接设备
        viewBinding.connectDevice.setOnClickListener {
            if(!Variable.isARouterInit) return@setOnClickListener
            if(viewModel.isConnectDevice.value==true){
                GlobalControlUtils.showAlertDialog("断开连接？","当前已连接北斗设备，是否需要断开",
                    onYesClick = {
                        BaseConnector.connector?.disconnect()
                    }
                )
            }
            else{
                rxPermissions
                    .request(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)  // 定位权限检测
                    .subscribe{  granted->
                        if(granted){
                            var isBluetoothEnable = BluetoothAdapter.getDefaultAdapter().isEnabled
                            var isLocationEnabled = isLocationEnabled()
                            // 蓝牙开启检测
                            if(!isBluetoothEnable){
                                val enableBluetooth = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                                startActivity(enableBluetooth)
                            }
                            // 定位开启检测
                            if(!isLocationEnabled){
                                val enableLocation = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                                startActivity(enableLocation)
                            }
                            if(isBluetoothEnable && isLocationEnabled){
                                if(BaseConnector.connector==null){
                                    val connector = BLEConnector()
                                    BaseConnector.setConnector(connector)
                                }
                                ARouter.getInstance().build(Constant.CONNECT_BLUETOOTH_ACTIVITY).navigation()
                            }else{
                                GlobalControlUtils.showToast("请先打开系统蓝牙和定位功能！",0)
                            }
                        }else{
                            GlobalControlUtils.showToast("请先授予权限！",0)
                        }
                    }
            }
        }

        // 收发测试
        viewBinding.sendTest.setOnClickListener {
            BaseConnector.connector?.sendMessage(viewModel.deviceCardID.value!!,2,"test")
        }

        signal_text.add(viewBinding.signal1Text);signal_text.add(viewBinding.signal2Text);signal_text.add(viewBinding.signal3Text);signal_text.add(viewBinding.signal4Text);signal_text.add(viewBinding.signal5Text);signal_text.add(viewBinding.signal6Text);signal_text.add(viewBinding.signal7Text);signal_text.add(viewBinding.signal8Text);signal_text.add(viewBinding.signal9Text);signal_text.add(viewBinding.signal10Text);signal_text.add(viewBinding.signal11Text);signal_text.add(viewBinding.signal12Text);signal_text.add(viewBinding.signal13Text);signal_text.add(viewBinding.signal14Text);signal_text.add(viewBinding.signal15Text);signal_text.add(viewBinding.signal16Text);signal_text.add(viewBinding.signal17Text);signal_text.add(viewBinding.signal18Text);signal_text.add(viewBinding.signal19Text);signal_text.add(viewBinding.signal20Text);signal_text.add(viewBinding.signal21Text);
        signal_view.add(viewBinding.signal1Img);signal_view.add(viewBinding.signal2Img);signal_view.add(viewBinding.signal3Img);signal_view.add(viewBinding.signal4Img);signal_view.add(viewBinding.signal5Img);signal_view.add(viewBinding.signal6Img);signal_view.add(viewBinding.signal7Img);signal_view.add(viewBinding.signal8Img);signal_view.add(viewBinding.signal9Img);signal_view.add(viewBinding.signal10Img);signal_view.add(viewBinding.signal11Img);signal_view.add(viewBinding.signal12Img);signal_view.add(viewBinding.signal13Img);signal_view.add(viewBinding.signal14Img);signal_view.add(viewBinding.signal15Img);signal_view.add(viewBinding.signal16Img);signal_view.add(viewBinding.signal17Img);signal_view.add(viewBinding.signal18Img);signal_view.add(viewBinding.signal19Img);signal_view.add(viewBinding.signal20Img);signal_view.add(viewBinding.signal21Img);

        // 计算出信号的长度变量
        viewBinding.signal1Group.post {
            signal_total_high = viewBinding.signal1Group.height // 总高度
            signal_text_high = viewBinding.signal1Text.height // 字体高度
            signal_high = signal_total_high - signal_text_high // 柱子最高高度
            signal_variable = signal_high / 60 // 计算每一信号，用最高高度除最大信号（60）
            loge("信号总高度: $signal_total_high/$signal_text_high/$signal_high/$signal_variable")
        }

    }

    // 数据变化监听
    fun init_view_model(){
        // 连接状态
        viewModel.isConnectDevice.observe(this,object : Observer<Boolean?> {
            override fun onChanged(isConnect: Boolean?) {
                if(isConnect==true){
                    viewBinding.connectDevice.text = "断开连接"
                    viewBinding.sendTest.setVisibility(View.VISIBLE);
                }else{
                    viewBinding.connectDevice.text = "连接设备"
                    viewBinding.sendTest.setVisibility(View.GONE);
                }
            }
        })

        viewModel.deviceCardID.observe(this,{
            it?.let {
                loge("监听到卡号变化 $it")
                viewBinding.cardId.setText(it)
            }
        })

        viewModel.deviceCardFrequency.observe(this,{
            it?.let {
                viewBinding.frequency.setText(it.toString())
            }
        })

        viewModel.deviceCardLevel.observe(this,{
            it?.let {
                viewBinding.level.setText(it.toString())
            }
        })

        viewModel.deviceBatteryLevel.observe(this,{
            it?.let {
                viewBinding.battery.setText(it.toString())
            }
        })

        viewModel.signal.observe(this,{
            it?.let {
                init_signal(it)
            }
        })

        viewModel.deviceLongitude.observe(this,{
            it?.let {
                viewBinding.longitude.setText(it.toString())
            }
        })
        viewModel.deviceLatitude.observe(this,{
            it?.let {
                viewBinding.latitude.setText(it.toString())
            }
        })
        viewModel.deviceAltitude.observe(this,{
            it?.let {
                viewBinding.altitude.setText(it.toString())
            }
        })

    }

    // 初始化数据解析SDK回调
    fun init_parser_callback(){
        ProtocolParser.getInstance().showLog(true)
        ProtocolParser.getInstance().setParameterListener(object : ParameterListener{
            override fun OnBDParameterChange(parameter: BD_Parameter?) {
                parameter?.let {  parameter->
                    viewModel.deviceCardID.postValue(parameter.CardID)
                    viewModel.deviceCardLevel.postValue(parameter.CardLevel)
                    viewModel.deviceCardFrequency.postValue(parameter.CardFrequency)
//                    viewModel.deviceBatteryLevel.postValue(parameter.BatteryLevel)
                    viewModel.signal.postValue(parameter.Signal)
                }
            }

            override fun OnDeviceAParameterChange(parameter: XYParameter?) {
                parameter?.let {
                    viewModel.deviceBatteryLevel.postValue(parameter.BatteryLevel)
                }
            }

            override fun OnDeviceBParameterChange(parameter: FDParameter?) {

            }

            override fun OnCommunicationFeedback(feedback: CommunicationFeedback?) {
                feedback?.let {
                    loge("指令下发反馈：${it.toString()}")
                    if(it.Type.equals("TCQ")){
                        if(it.Result){
                            GlobalControlUtils.showToast("消息发送成功",0)
                        }else{
                            GlobalControlUtils.showToast("消息发送失败",0)
                        }
                    }
                }
            }

            override fun OnMessageReceived(message: ReceivedMessage?) {
                message?.let {
                    loge("收到卫星消息：${it.toString()}")
                    GlobalControlUtils.showToast("收到 ${it.SendID} 消息：${it.Content}",0)
                }
            }

            override fun OnBDLocationChange(location: BD_Location?) {
                location?.let {
                    viewModel.deviceLongitude.postValue(it.Longitude)
                    viewModel.deviceLatitude.postValue(it.Latitude)
                    viewModel.deviceAltitude.postValue(it.EllipsoidHeight)
                    loge("卫星定位改变：${it.toString()}")
                }
            }

            override fun OnSatelliteStatusChange(status: SatelliteStatus?) {
                status?.let {
                    loge("卫星状态改变：${it.toString()}")
                }
            }

            override fun OnCommandResponse(command: ResponseCommand?) {
                command?.let {
                    loge("收到响应指令，指令类型：${it.Command} 响应结果：${it.Result} 其它说明：${it.Remark}")
                }
            }

        })
    }

    private val signal_text:MutableList<TextView> = mutableListOf()
    private val signal_view:MutableList<ImageView> = mutableListOf()
    var signal_total_high = 0 // 信号的总长度（文字 + 柱子）
    var signal_text_high = 0 // 信号文字的长度
    var signal_high = 0 // 信号的最长长度（总长度 - 文字长度）
    var signal_variable = 5 // 信号的变量（ 信号的最长长度 / 最大信号值）

    private fun init_signal(signals: IntArray) {

        if (signals.size != signal_text.size) {
            return
        }
        signal_text.forEachIndexed { index, textView ->
            textView.text = signals[index].toString()
            set_img_height(signal_view[index], signals[index])
        }
    }


    fun set_img_height(img: ImageView, signal: Int) {
        val params = img.layoutParams as LinearLayout.LayoutParams
        when {
            signal == 0 -> {
                params.height = signal_variable
                img.background = getDrawable(R.color.signal_1)
            }
            signal >= 50 -> params.height = 55 * signal_variable
            else -> params.height = signal * signal_variable
        }
        img.layoutParams = params
        img.background = getDrawable(when {
            signal >= 40 -> R.color.signal_5
            signal >= 30 -> R.color.signal_4
            signal >= 20 -> R.color.signal_3
            signal >= 10 -> R.color.signal_2
            else -> R.color.signal_1
        })
    }


    fun isLocationEnabled(): Boolean {
        val locationManager = this.getSystemService(LOCATION_SERVICE) as LocationManager
        if (locationManager != null) {
            // 检查 GPS 定位是否开启
            val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            return isGpsEnabled
        }
        return false
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
        // 断开设备连接
        BaseConnector.connector?.disconnect()
        super.onDestroy()
    }

}