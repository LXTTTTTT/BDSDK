package com.pancoit.mod_main.Activity

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import com.bdtx.mod_main.Base.BaseMVVMActivity
import com.pancoit.mod_main.databinding.ActivityMainBinding
import java.util.*


// 用不上 ViewModel
class MainActivity : BaseMVVMActivity<ActivityMainBinding,MainVM>(true) {

    lateinit var rxPermissions : RxPermissions
    override fun beforeSetLayout() {}
    override fun enableEventBus(): Boolean { return true }
    override fun initView(savedInstanceState: Bundle?) {
        init_control()
        start_timer()
    }
    override suspend fun initDataSuspend() {}
    override fun initData() {
        super.initData()  // 在父类初始化 viewModel
        rxPermissions = RxPermissions(this)
        init_view_model()
    }

    fun init_control(){
        viewBinding.messagePage.setOnClickListener {
            // 需要等到 ARouter 初始化完成后才能调用不然会崩溃，应该要加个闪屏页来等待任务初始化
            if(!Variable.isARouterInit) return@setOnClickListener
            ARouter.getInstance().build(Constant.MESSAGE_ACTIVITY).navigation()
        }

        viewBinding.statePage.setOnClickListener(object : View.OnClickListener{
            override fun onClick(p0: View?) {
                if(!Variable.isARouterInit) return
//                startActivity(Intent(my_context,StateActivity::class.java))
                ARouter.getInstance().build(Constant.STATE_ACTIVITY).navigation()
            }
        })

        viewBinding.mapPage.setOnClickListener(object : View.OnClickListener{
            override fun onClick(p0: View?) {
                if(!Variable.isARouterInit) return
                ARouter.getInstance().build(Constant.MAP_ACTIVITY).navigation()
            }
        })

        viewBinding.healthyPage.setOnClickListener(object : View.OnClickListener{
            override fun onClick(p0: View?) {
                if(!Variable.isARouterInit) return
                ARouter.getInstance().build(Constant.HEALTHY_ACTIVITY).navigation()
            }
        })

        viewBinding.sosPage.setOnClickListener(object : View.OnClickListener{
            override fun onClick(p0: View?) {
                if(!Variable.isARouterInit) return
                ARouter.getInstance().build(Constant.SOS_ACTIVITY).navigation()
            }
        })

        viewBinding.settingPage.setOnClickListener(object : View.OnClickListener{
            override fun onClick(p0: View?) {
                if(!Variable.isARouterInit) return
                ARouter.getInstance().build(Constant.SETTING_ACTIVITY).navigation()
            }
        })

        // 连接设备
        viewBinding.connectBluetoothGroup.setOnClickListener {
            if(!Variable.isARouterInit) return@setOnClickListener
            if(viewModel.isConnectDevice.value==true){
                GlobalControlUtils.showAlertDialog("断开蓝牙？","当前已连接蓝牙，是否需要断开蓝牙",
                    onYesClick = {
                        BluetoothTransferUtils.getInstance().disconnectDevice();
                    }
                )
            }
            else{
                // 这个手表是android9不需要bluetoothscan等权限，默认已获取系统权限省略某些权限申请
                rxPermissions
                    .request(Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION)  // 定位权限检测
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
                                ARouter.getInstance().build(Constant.COMMUNICATION_LINK_ACTIVITY).navigation()  // 页面跳转
//                                ARouter.getInstance().build(Constant.CONNECT_BLUETOOTH_ACTIVITY).navigation()  // 页面跳转
                            }else{
                                GlobalControlUtils.showToast("请先打开系统蓝牙和定位功能！",0)
                            }
                        }else{
                            GlobalControlUtils.showToast("请先授予权限！",0)
                        }
                    }

            }
        }

    // 测试组 ---------------------------------------------
        viewBinding.test1.setOnClickListener {
            var message = Message()
            message.content = "测试发送"
            message.messageType = Constant.MESSAGE_TEXT
            message.ioType = Constant.TYPE_SEND
            message.number = "666666"
            message.state = Constant.STATE_SENDING
            message.time = DataUtils.getTimeSeconds()
            DaoUtils.getInstance().addMessage(message)
        }

        viewBinding.test2.setOnClickListener {
            var message2 = Message()
            message2.content = "测试接收"
            message2.messageType = Constant.MESSAGE_TEXT
            message2.ioType = Constant.TYPE_RECEIVE
            message2.number = "666666"
            message2.state = Constant.STATE_SUCCESS
            message2.time = DataUtils.getTimeSeconds()
            message2.longitude = 113.499986333
            message2.latitude = 25.1552066
            DaoUtils.getInstance().addMessage(message2)
        }

        viewBinding.test3.setOnClickListener {
            loge("当前压缩码率 ${Variable.getCompressRate()} 平台号码 ${Variable.getSystemNumber()}")
            val test_A7_content = "\$BDTCI,04207733,4207733,2,070222,2,0,A7000690259E30000001000300052F200DDE0CEB00EC0836EB0FEA04EA09EB05DBEB08E00AE90288077BAD050B04EB08ED0671EF089D0AE3019D0F03E407EA07DE05DE052ABA05E00C5B049D0F57EA0CE305EA0CEE07465E0912007200AD0897280608070A000A01D5E4030A092809D600707603350036003700003700B600D200D20D00DE07370036203540403D30322035503D100033503470346034600034403F303F30331000364031603F803B70003B7039411420390025320032001B01120432A800AB075B05E2026FE007EB05EB000B06B6EB06EB08EB00EF02D7ED01ED0A9D090B054F0100440EC4154F156AC115C104C404C4193BC403440EC107C10988C00CC904C90CC00848400A41144004400A3C400A4009F1084D0B25010A01020A010B014E0A030B090B060B030DEB07ED00EB030D001DED01ED03EB06EB068B0D09BB060B050B01340B0700030A04200B330A040D040C067B015C0A070B080A070A09E90B070B020007BA06B90B081A050A0110006FBA090A0C0B060A08CC0A0A0A070D010D01A00B0720057B0BBA09902B06B800B200BD00C5BD0B20050B0D2B002D3D04350036003600403600B600B704BD0C0ABF00BD02DB01DB0058B80403*7E"
            BDProtocolUtils.getInstance().parseData(test_A7_content)
        }

        viewBinding.test4.setOnClickListener {
            loge("获取快捷消息：${Variable.getSwiftMsg()}")
        }

        viewBinding.test5.setOnClickListener {
            loge("key：${Variable.getKey()}")
            throw RuntimeException("崩它！")
        }

    }

    fun init_view_model(){
        // 数据变化监听
        viewModel.isConnectDevice.observe(this,object : Observer<Boolean?>{
            override fun onChanged(isConnect: Boolean?) {
                // 连接设备文字变化
                if(isConnect==true){
                    viewBinding.connectBluetooth.text = "断开连接"
                }else{
                    viewBinding.connectBluetooth.text = "点击连接北斗"
                }
            }
        })

        viewModel.deviceCardID.observe(this,{
            it?.let { loge("监听到卡号变化 $it") }
        })

        // 未读消息数量监听
        viewModel.getUnreadMessageCount().observe(this,{
            it?.let {
                viewBinding.unreadCount.text = if (it<100){it.toString()}else{"99+"}
                viewBinding.unreadCount.isVisible = it>0
            }
        })
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    fun onEvent(eventMsg: BaseMsg<Any>){
        loge("收到广播，类型：${eventMsg.type}")
        // 更新未读消息数量
        if(eventMsg.type== BaseMsg.MSG_UPDATE_CONTACT){
            viewModel.upDateUnreadMessageCount()
        }
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

    // 主页时间更新
    var timer:Timer? = null
    fun start_timer(){
        timer = Timer()
        timer!!.schedule(object : TimerTask(){
            override fun run() {
                viewBinding.nowTime.text = DataUtils.getTimeString()
            }
        },0,1000)
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

    override fun onDestroy() {
        timer?.let { it.cancel(); timer=null }
        BluetoothTransferUtils.getInstance().disconnectDevice();
        super.onDestroy()
    }

}