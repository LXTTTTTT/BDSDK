package com.pancoit.mod_main.Activity

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.pancoit.mod_main.Base.BaseMVVMActivity
import com.pancoit.mod_main.Fragment.BDFragment
import com.pancoit.mod_main.Fragment.DeviceFragment
import com.pancoit.mod_main.R
import com.pancoit.mod_main.Utils.ApplicationUtils
import com.pancoit.mod_main.Utils.Connection.BaseConnector
import com.pancoit.mod_main.Utils.GlobalControlUtils
import com.pancoit.mod_main.View.BottomBar
import com.pancoit.mod_main.ViewModel.BDVM
import com.pancoit.mod_main.ViewModel.DeviceVM
import com.pancoit.mod_main.databinding.ActivityMainBinding
import com.pancoit.mod_parse.CallBack.ParameterListener
import com.pancoit.mod_parse.Parameter.*
import com.pancoit.mod_parse.Protocol.ProtocolPackager
import com.pancoit.mod_parse.Protocol.ProtocolParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*


class MainActivity : BaseMVVMActivity<ActivityMainBinding, DeviceVM>(false) {

    lateinit var bdFragment: BDFragment
    lateinit var deviceFragment: DeviceFragment
    lateinit var bdvm: BDVM
    override fun beforeSetLayout() {}
    override fun enableEventBus(): Boolean { return true }
    override fun initView(savedInstanceState: Bundle?) {
        init_fragment()
    }
    override suspend fun initDataSuspend() {}
    override fun initData() {
        super.initData()  // 在父类初始化 viewModel ，必须调用
        bdvm = ApplicationUtils.getGlobalViewModel(BDVM::class.java)
        init_parser_callback()
    }

    // 初始化数据解析SDK回调
    fun init_parser_callback(){

        ProtocolParser.getInstance().showLog(true)  // 开启SDK日志打印
        // 设置参数解析监听处理
        ProtocolParser.getInstance().setParameterListener(object : ParameterListener {

            // 北斗参数变化
            override fun OnBDParameterChange(parameter: BD_Parameter?) {
                parameter?.let {  parameter->
                    bdvm?.let {
                        it.deviceCardID.postValue(parameter.CardID)
                        it.deviceCardLevel.postValue(parameter.CardLevel)
                        it.deviceCardFrequency.postValue(parameter.CardFrequency)
                        it.signal.postValue(parameter.Signal)
                    }
                    loge("北斗参数变化：${parameter.toString()}")
                }
            }

            // A型设备参数变化
            override fun OnDeviceAParameterChange(parameter: XYParameter?) {
                parameter?.let {
                    viewModel.XY_Version.postValue(parameter.Version)
                    viewModel.XY_RestartMode.postValue(parameter.RestartMode)
                    viewModel.XY_BatteryLevel.postValue(parameter.BatteryLevel)
                    viewModel.XY_ContentLength.postValue(parameter.ContentLength)
                    viewModel.XY_Temperature.postValue(parameter.Temperature)
                    viewModel.XY_Humidity.postValue(parameter.Humidity)
                    viewModel.XY_Pressure.postValue(parameter.Pressure)
                    viewModel.XY_LocationReportID.postValue(parameter.LocationReportID)
                    viewModel.XY_PositionMode.postValue(parameter.PositionMode)
                    viewModel.XY_CollectionFrequency.postValue(parameter.CollectionFrequency)
                    viewModel.XY_PositionCount.postValue(parameter.PositionCount)
                    viewModel.XY_ReportType.postValue(parameter.ReportType)
                    viewModel.XY_SOSID.postValue(parameter.SOSID)
                    viewModel.XY_SOSFrequency.postValue(parameter.SOSFrequency)
                    viewModel.XY_OKID.postValue(parameter.OKID)
                    viewModel.XY_OKContent.postValue(parameter.OKContent)
                    viewModel.XY_RDSSProtocolVersion.postValue(parameter.RDSSProtocolVersion)
                    viewModel.XY_RNSSProtocolVersion.postValue(parameter.RNSSProtocolVersion)
                    viewModel.XY_RDSSMode.postValue(parameter.RDSSMode)
                    viewModel.XY_RNSSMode.postValue(parameter.RNSSMode)
                    viewModel.XY_BLEMode.postValue(parameter.BLEMode)
                    viewModel.XY_NETMode.postValue(parameter.NETMode)
                    viewModel.XY_WorkMode.postValue(parameter.WorkMode)
                    viewModel.XY_GGAFrequency.postValue(parameter.GGAFrequency)
                    viewModel.XY_GSVFrequency.postValue(parameter.GSVFrequency)
                    viewModel.XY_GLLFrequency.postValue(parameter.GLLFrequency)
                    viewModel.XY_GSAFrequency.postValue(parameter.GSAFrequency)
                    viewModel.XY_RMCFrequency.postValue(parameter.RMCFrequency)
                    viewModel.XY_ZDAFrequency.postValue(parameter.ZDAFrequency)
                    viewModel.XY_TimeZone.postValue(parameter.TimeZone)
                    bdvm?.let {
                        it.deviceBatteryLevel.postValue(parameter.BatteryLevel)
                    }
                    loge("A型设备参数变化：${parameter.toString()}")
                }
            }

            // B型设备参数变化
            override fun OnDeviceBParameterChange(parameter: FDParameter?) {
                parameter?.let {
                    viewModel.FD_LocationReportID.postValue(parameter.LocationReportID)
                    viewModel.FD_LocationReportFrequency.postValue(parameter.LocationReportFrequency)
                    viewModel.FD_SOSID.postValue(parameter.SOSID)
                    viewModel.FD_SOSFrequency.postValue(parameter.SOSFrequency)
                    viewModel.FD_SOSContent.postValue(parameter.SOSContent)
                    viewModel.FD_OverboardID.postValue(parameter.OverboardID)
                    viewModel.FD_OverboardFrequency.postValue(parameter.OverboardFrequency)
                    viewModel.FD_OverboardContent.postValue(parameter.OverboardContent)
                    viewModel.FD_WorkMode.postValue(parameter.WorkMode)
                    viewModel.FD_BatteryVoltage.postValue(parameter.BatteryVoltage)
                    viewModel.FD_BatteryLevel.postValue(parameter.BatteryLevel)
                    viewModel.FD_PositioningModuleStatus.postValue(parameter.PositioningModuleStatus)
                    viewModel.FD_BDModuleStatus.postValue(parameter.BDModuleStatus)
                    viewModel.FD_SoftwareVersion.postValue(parameter.SoftwareVersion)
                    viewModel.FD_HardwareVersion.postValue(parameter.HardwareVersion)
                    viewModel.FD_LocationStoragePeriod.postValue(parameter.LocationStoragePeriod)
                    viewModel.FD_BluetoothName.postValue(parameter.BluetoothName)
                    viewModel.FD_ExternalVoltage.postValue(parameter.ExternalVoltage)
                    viewModel.FD_InternalVoltage.postValue(parameter.InternalVoltage)
                    viewModel.FD_Temperature.postValue(parameter.Temperature)
                    viewModel.FD_Humidity.postValue(parameter.Humidity)
                    viewModel.FD_LocationsCount.postValue(parameter.LocationsCount)
                    viewModel.FD_CardID.postValue(parameter.CardID)
                    viewModel.FD_NumberOfResets.postValue(parameter.NumberOfResets)
                    viewModel.FD_RNBleFeedback.postValue(parameter.RNBleFeedback)
                    viewModel.FD_Power.postValue(parameter.Power)
                    bdvm?.let {
                        it.deviceBatteryLevel.postValue(parameter.BatteryLevel)
                    }
                    loge("B型设备参数变化：${parameter.toString()}")
                }
            }

            // 北斗指令下发反馈
            override fun OnCommandFeedback(feedback: CommandFeedback?) {
                feedback?.let {
                    if(it.Type.equals("TCQ")){
                        if(it.Result){
                            GlobalControlUtils.showToast("消息发送成功",0)
                        }else{
                            GlobalControlUtils.showToast("消息发送失败",0)
                        }
                    }
                    loge("指令下发反馈：${it.toString()}")
                }
            }

            // 消息接收
            override fun OnMessageReceived(message: ReceivedMessage?) {
                message?.let {
                    loge("收到卫星消息：${it.toString()}")
                    GlobalControlUtils.showToast("收到来自 ${it.SendID} 的消息：${it.Content}",0)
                    GlobalControlUtils.ringBell()
                }
            }

            // 北斗定位参数变化
            override fun OnBDLocationChange(location: BD_Location?) {
                location?.let {
                    bdvm?.let {
                        it.deviceLongitude.postValue(location.Longitude)
                        it.deviceLatitude.postValue(location.Latitude)
                        it.deviceAltitude.postValue(location.EllipsoidHeight)
                        loge("北斗定位参数变化：${location.toString()}")
                    }
                }
            }

            // 卫星状态参数变化
            override fun OnSatelliteStatusChange(status: SatelliteStatus?) {
                status?.let {
                    loge("卫星状态改变：${it.toString()}")
                }
            }

            // 设备指令下发响应
            override fun OnCommandResponse(command: ResponseCommand?) {
                command?.let {
                    loge("收到响应指令，指令类型：${it.Command} 响应结果：${it.Result} 其它说明：${it.Remark}")
                }
            }

            // 未解析指令接收
            override fun OnCommandUnresolved(command: UnresolvedCommand?) {
                command?.let {
                    loge("收到未解析指令：${Arrays.toString(it.RawCommand)} ")
                }
            }

        })
    }


    fun init_fragment() {
        bdFragment = BDFragment()
        deviceFragment = DeviceFragment()
        viewBinding.bottomBar.setContainer(R.id.fragment) // 设置容器控件
            .setOrientation(BottomBar.HORIZONTAL)
            .setFirstChecked(0)
            .setTitleIconMargin(8)
            .setTitleBeforeAndAfterColor("#7f7f7f", "#008577") // 设置标题选中和未选中的颜色
            .addItem(
                bdFragment,
                "北斗状态",
                R.mipmap.bd_unchecked_icon,
                R.mipmap.bd_checked_icon
            )
            .addItem(
                deviceFragment,
                "设备状态",
                R.mipmap.status_unchecked_icon,
                R.mipmap.status_checked_icon
            ) // 添加页面
            .buildWithEntity() // 构建

        viewBinding.bottomBar.setOnSwitchListener(object : BottomBar.OnSwitchListener{
            override fun result(position: Int,currentFragment: Fragment?) {
                loge("页面切换：$position")
            }
        })

// 测试按键组 ---------------------------------------------
        viewBinding.test1.setOnClickListener {
            loge("当前设置的蓝牙过滤规则："+com.pancoit.mod_bluetooth.Global.Variable.matchingRules)
            BaseConnector.connector?.sendCommand(ProtocolPackager.FLYRN(1,1))
        }

        viewBinding.test2.setOnClickListener {
            BaseConnector.connector?.sendCommand(ProtocolPackager.FLYRN(1,0))
        }

        viewBinding.test3.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch{
                BaseConnector.connector?.let {
                    delay(300)
                    it.sendCommand(ProtocolPackager.CCICR(0,"00"))
                }
            }
        }

        viewBinding.test4.setOnClickListener {
            BaseConnector.connector?.sendMessage(bdvm.deviceCardID.value!!,2,"test")
        }

        viewBinding.test5.setOnClickListener {
            throw RuntimeException("崩它！")
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


    override fun onDestroy() {
        // 断开设备连接
        BaseConnector.connector?.disconnect()
        super.onDestroy()
    }
}