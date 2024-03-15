package com.pancoit.mod_main.ViewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import java.util.*

// 全局使用 ViewModel
class DeviceVM : BaseViewModel() {

    val TAG = "DeviceVM"
    val XY_Version : MutableLiveData<String?> = MutableLiveData()
    val XY_RestartMode : MutableLiveData<Int?> = MutableLiveData()
    val XY_BatteryLevel : MutableLiveData<Int?> = MutableLiveData()
    val XY_ContentLength : MutableLiveData<Int?> = MutableLiveData()
    val XY_Temperature : MutableLiveData<Double?> = MutableLiveData()
    val XY_Humidity : MutableLiveData<Int?> = MutableLiveData()
    val XY_Pressure : MutableLiveData<Int?> = MutableLiveData()
    val XY_LocationReportID : MutableLiveData<String?> = MutableLiveData()
    val XY_PositionMode : MutableLiveData<Int?> = MutableLiveData()
    val XY_CollectionFrequency : MutableLiveData<Int?> = MutableLiveData()
    val XY_PositionCount : MutableLiveData<Int?> = MutableLiveData()
    val XY_ReportType : MutableLiveData<Int?> = MutableLiveData()
    val XY_SOSID : MutableLiveData<String?> = MutableLiveData()
    val XY_SOSFrequency : MutableLiveData<Int?> = MutableLiveData()
    val XY_OKID : MutableLiveData<String?> = MutableLiveData()
    val XY_OKContent : MutableLiveData<String?> = MutableLiveData()
    val XY_RDSSProtocolVersion : MutableLiveData<Int?> = MutableLiveData()
    val XY_RNSSProtocolVersion : MutableLiveData<Int?> = MutableLiveData()
    val XY_RDSSMode : MutableLiveData<Int?> = MutableLiveData()
    val XY_RNSSMode : MutableLiveData<Int?> = MutableLiveData()
    val XY_BLEMode : MutableLiveData<Int?> = MutableLiveData()
    val XY_NETMode : MutableLiveData<Int?> = MutableLiveData()
    val XY_WorkMode : MutableLiveData<Int?> = MutableLiveData()
    val XY_GGAFrequency : MutableLiveData<Int?> = MutableLiveData()
    val XY_GSVFrequency : MutableLiveData<Int?> = MutableLiveData()
    val XY_GLLFrequency : MutableLiveData<Int?> = MutableLiveData()
    val XY_GSAFrequency : MutableLiveData<Int?> = MutableLiveData()
    val XY_RMCFrequency : MutableLiveData<Int?> = MutableLiveData()
    val XY_ZDAFrequency : MutableLiveData<Int?> = MutableLiveData()
    val XY_TimeZone : MutableLiveData<Int?> = MutableLiveData()

    val FD_LocationReportID : MutableLiveData<String?> = MutableLiveData()
    val FD_LocationReportFrequency : MutableLiveData<Int?> = MutableLiveData()
    val FD_SOSID : MutableLiveData<String?> = MutableLiveData()
    val FD_SOSFrequency : MutableLiveData<Int?> = MutableLiveData()
    val FD_SOSContent : MutableLiveData<String?> = MutableLiveData()
    val FD_OverboardID : MutableLiveData<String?> = MutableLiveData()
    val FD_OverboardFrequency : MutableLiveData<Int?> = MutableLiveData()
    val FD_OverboardContent : MutableLiveData<String?> = MutableLiveData()
    val FD_WorkMode : MutableLiveData<Int?> = MutableLiveData()
    val FD_BatteryVoltage : MutableLiveData<Int?> = MutableLiveData()
    val FD_BatteryLevel : MutableLiveData<Int?> = MutableLiveData()
    val FD_PositioningModuleStatus : MutableLiveData<Int?> = MutableLiveData()
    val FD_BDModuleStatus : MutableLiveData<Int?> = MutableLiveData()
    val FD_SoftwareVersion : MutableLiveData<String?> = MutableLiveData()
    val FD_HardwareVersion : MutableLiveData<String?> = MutableLiveData()
    val FD_LocationStoragePeriod : MutableLiveData<Int?> = MutableLiveData()
    val FD_BluetoothName : MutableLiveData<String?> = MutableLiveData()
    val FD_ExternalVoltage : MutableLiveData<Int?> = MutableLiveData()
    val FD_InternalVoltage : MutableLiveData<Int?> = MutableLiveData()
    val FD_Temperature : MutableLiveData<Double?> = MutableLiveData()
    val FD_Humidity : MutableLiveData<Double?> = MutableLiveData()
    val FD_LocationsCount : MutableLiveData<Int?> = MutableLiveData()
    val FD_CardID : MutableLiveData<String?> = MutableLiveData()
    val FD_NumberOfResets : MutableLiveData<Int?> = MutableLiveData()
    val FD_RNBleFeedback : MutableLiveData<Int?> = MutableLiveData()
    val FD_Power : MutableLiveData<String?> = MutableLiveData()

    init {
        initDeviceParameter()
    }

    // 初始化默认参数
    fun initDeviceParameter(){
        XY_Version.postValue("-")
        XY_RestartMode.postValue(-1)
        XY_BatteryLevel.postValue(-1)
        XY_ContentLength.postValue(-1)
        XY_Temperature.postValue(0.0)
        XY_Humidity.postValue(-1)
        XY_Pressure.postValue(-1)
        XY_LocationReportID.postValue("-")
        XY_PositionMode.postValue(-1)
        XY_CollectionFrequency.postValue(-1)
        XY_PositionCount.postValue(-1)
        XY_ReportType.postValue(-1)
        XY_SOSID.postValue("-")
        XY_SOSFrequency.postValue(-1)
        XY_OKID.postValue("-")
        XY_OKContent.postValue("-")
        XY_RDSSProtocolVersion.postValue(-1)
        XY_RNSSProtocolVersion.postValue(-1)
        XY_RDSSMode.postValue(-1)
        XY_RNSSMode.postValue(-1)
        XY_BLEMode.postValue(-1)
        XY_NETMode.postValue(-1)
        XY_WorkMode.postValue(-1)
        XY_GGAFrequency.postValue(-1)
        XY_GSVFrequency.postValue(-1)
        XY_GLLFrequency.postValue(-1)
        XY_GSAFrequency.postValue(-1)
        XY_RMCFrequency.postValue(-1)
        XY_ZDAFrequency.postValue(-1)
        XY_TimeZone.postValue(-1)

        FD_LocationReportID.postValue("-")
        FD_LocationReportFrequency.postValue(-1)
        FD_SOSID.postValue("0")
        FD_SOSFrequency.postValue(-1)
        FD_SOSContent.postValue("-")
        FD_OverboardID.postValue("0")
        FD_OverboardFrequency.postValue(-1)
        FD_OverboardContent.postValue("-")
        FD_WorkMode.postValue(-1)
        FD_BatteryVoltage.postValue(-1)
        FD_BatteryLevel.postValue(-1)
        FD_PositioningModuleStatus.postValue(-1)
        FD_BDModuleStatus.postValue(-1)
        FD_SoftwareVersion.postValue("-")
        FD_HardwareVersion.postValue("-")
        FD_LocationStoragePeriod.postValue(-1)
        FD_BluetoothName.postValue("-")
        FD_ExternalVoltage.postValue(-1)
        FD_InternalVoltage.postValue(-1)
        FD_Temperature.postValue(0.0)
        FD_Humidity.postValue(0.0)
        FD_LocationsCount.postValue(-1)
        FD_CardID.postValue("0")
        FD_NumberOfResets.postValue(-1)
        FD_RNBleFeedback.postValue(-1)
        FD_Power.postValue("-")

    }
}