package com.bdtx.mod_data.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.*
import java.util.*

// 全局使用 ViewModel
class MainVM : BaseViewModel() {

    val TAG = "MainVM"
    val isConnectDevice : MutableLiveData<Boolean?> = MutableLiveData()  // 是否连接蓝牙
    val deviceCardID : MutableLiveData<String?> = MutableLiveData()  // 卡号
    val deviceCardFrequency : MutableLiveData<Int?> = MutableLiveData()  // 频度
    val deviceCardLevel : MutableLiveData<Int?> = MutableLiveData()  // 通信等级
    val deviceBatteryLevel : MutableLiveData<Int?> = MutableLiveData()  // 电量
    val signal : MutableLiveData<IntArray?> = MutableLiveData()  // 信号
    val deviceLongitude : MutableLiveData<Double?> = MutableLiveData()  // 设备经度
    val deviceLatitude : MutableLiveData<Double?> = MutableLiveData()  // 设备纬度
    val deviceAltitude : MutableLiveData<Double?> = MutableLiveData()  // 设备高度
    val waitTime : MutableLiveData<Int?> = MutableLiveData()  // 等待时间
    val unreadMessageCount : MutableLiveData<Int?> = MutableLiveData()  // 总未读消息数量

    val systemLongitude : MutableLiveData<Double?> = MutableLiveData()  // 系统经度
    val systemLatitude : MutableLiveData<Double?> = MutableLiveData()  // 系统纬度
    val systemAltitude : MutableLiveData<Double?> = MutableLiveData()  // 系统高度




    init {
        initDeviceParameter()
        initSystemParameter()
    }

    // 初始化默认参数
    fun initDeviceParameter(){
        isConnectDevice.postValue(false)
        deviceCardID.postValue("-")
        deviceCardFrequency.postValue(-1)
        deviceCardLevel.postValue(-1)
        deviceBatteryLevel.postValue(-1)
        signal.postValue(intArrayOf(0,0,0,0,0,0,0,0,0,0))
//        unreadMessageCount.postValue(0)
        waitTime.postValue(0)
        deviceLongitude.postValue(0.0)
        deviceLatitude.postValue(0.0)
        deviceAltitude.postValue(0.0)
    }

    fun initSystemParameter(){
        systemLongitude.postValue(0.0)
        systemLatitude.postValue(0.0)
        systemAltitude.postValue(0.0)
    }

    // 当前信号是否良好
    fun isSignalWell():Boolean{
        if(isConnectDevice.value==false){return false}
        val max_single = Arrays.stream(signal.value).max().orElse(0) // 拿到所有信号中的最大值
        return max_single>40  // 大于40可发消息
    }



    private var countDownJob: Job? = null
    fun startCountDown(){
        if(deviceCardFrequency.value==-1){return}
        val frequency = deviceCardFrequency.value!!.plus(2)  // 总倒计时：频度+2秒
        countDownJob = countDownCoroutines(frequency, viewModelScope,
            onTick = { countDownSeconds ->
                waitTime.postValue(countDownSeconds)
            },
            onStart = {},
            onFinish = {countDownJob?.cancel()}
        )
    }




}