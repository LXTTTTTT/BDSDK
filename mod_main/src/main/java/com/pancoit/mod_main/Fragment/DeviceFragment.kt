package com.pancoit.mod_main.Fragment

import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.pancoit.mod_main.Base.BaseMVVMFragment
import com.pancoit.mod_main.Utils.ApplicationUtils
import com.pancoit.mod_main.ViewModel.BDVM
import com.pancoit.mod_main.ViewModel.DeviceVM
import com.pancoit.mod_main.databinding.FragmentDeviceBinding

class DeviceFragment : BaseMVVMFragment<FragmentDeviceBinding,DeviceVM>(false) {
    override fun beforeSetLayout() {}
    override fun initView(view: View) {}
    override fun initData() {
        super.initData()
        init_xy_parameter()
        init_fd_parameter()
    }
    override suspend fun initDataSuspend() {}

    // 数据变化监听
    fun init_xy_parameter(){

        viewModel.XY_Version.observe(this,object : Observer<String?> {
            override fun onChanged(version: String?) {
                version?.let {
                    viewBinding.xyVersion.text = it
                }
            }
        })

        viewModel.XY_RestartMode.observe(this,object : Observer<Int?> {
            override fun onChanged(mode: Int?) {
                mode?.let {
                    viewBinding.xyRestartmode.text = it.toString()
                }
            }
        })

        viewModel.XY_BatteryLevel.observe(this,object : Observer<Int?> {
            override fun onChanged(value: Int?) {
                value?.let {
                    viewBinding.xyBatterylevel.text = it.toString()
                }
            }
        })

        viewModel.XY_ContentLength.observe(this,object : Observer<Int?> {
            override fun onChanged(value: Int?) {
                value?.let {
                    viewBinding.xyContentlength.text = it.toString()
                }
            }
        })

        viewModel.XY_Temperature.observe(this,object : Observer<Double?> {
            override fun onChanged(value: Double?) {
                value?.let {
                    viewBinding.xyTemperature.text = it.toString()
                }
            }
        })

        viewModel.XY_Humidity.observe(this,object : Observer<Int?> {
            override fun onChanged(value: Int?) {
                value?.let {
                    viewBinding.xyHumidity.text = it.toString()
                }
            }
        })

        viewModel.XY_Pressure.observe(this,object : Observer<Int?> {
            override fun onChanged(value: Int?) {
                value?.let {
                    viewBinding.xyPressure.text = it.toString()
                }
            }
        })

        viewModel.XY_LocationReportID.observe(this,object : Observer<String?> {
            override fun onChanged(version: String?) {
                version?.let {
                    viewBinding.xyLocationreportid.text = it
                }
            }
        })

        viewModel.XY_PositionMode.observe(this,object : Observer<Int?> {
            override fun onChanged(value: Int?) {
                value?.let {
                    viewBinding.xyPositionmode.text = it.toString()
                }
            }
        })

        viewModel.XY_CollectionFrequency.observe(this,object : Observer<Int?> {
            override fun onChanged(value: Int?) {
                value?.let {
                    viewBinding.xyCollectionfrequency.text = it.toString()
                }
            }
        })

        viewModel.XY_PositionCount.observe(this,object : Observer<Int?> {
            override fun onChanged(value: Int?) {
                value?.let {
                    viewBinding.xyPositioncount.text = it.toString()
                }
            }
        })

        viewModel.XY_ReportType.observe(this,object : Observer<Int?> {
            override fun onChanged(value: Int?) {
                value?.let {
                    viewBinding.xyReporttype.text = it.toString()
                }
            }
        })

        viewModel.XY_SOSID.observe(this,object : Observer<String?> {
            override fun onChanged(version: String?) {
                version?.let {
                    viewBinding.xySosid.text = it
                }
            }
        })

        viewModel.XY_SOSFrequency.observe(this,object : Observer<Int?> {
            override fun onChanged(value: Int?) {
                value?.let {
                    viewBinding.xySosfrequency.text = it.toString()
                }
            }
        })

        viewModel.XY_OKID.observe(this,object : Observer<String?> {
            override fun onChanged(version: String?) {
                version?.let {
                    viewBinding.xyOkid.text = it
                }
            }
        })

        viewModel.XY_OKContent.observe(this,object : Observer<String?> {
            override fun onChanged(version: String?) {
                version?.let {
                    viewBinding.xyOkcontent.text = it
                }
            }
        })

        viewModel.XY_RDSSProtocolVersion.observe(this,object : Observer<Int?> {
            override fun onChanged(value: Int?) {
                value?.let {
                    viewBinding.xyRdssprotocolversion.text = it.toString()
                }
            }
        })

        viewModel.XY_RNSSProtocolVersion.observe(this,object : Observer<Int?> {
            override fun onChanged(value: Int?) {
                value?.let {
                    viewBinding.xyRnssprotocolversion.text = it.toString()
                }
            }
        })

        viewModel.XY_RDSSMode.observe(this,object : Observer<Int?> {
            override fun onChanged(value: Int?) {
                value?.let {
                    viewBinding.xyRdssmode.text = it.toString()
                }
            }
        })

        viewModel.XY_RNSSMode.observe(this,object : Observer<Int?> {
            override fun onChanged(value: Int?) {
                value?.let {
                    viewBinding.xyRnssmode.text = it.toString()
                }
            }
        })

        viewModel.XY_BLEMode.observe(this,object : Observer<Int?> {
            override fun onChanged(value: Int?) {
                value?.let {
                    viewBinding.xyBlemode.text = it.toString()
                }
            }
        })

        viewModel.XY_NETMode.observe(this,object : Observer<Int?> {
            override fun onChanged(value: Int?) {
                value?.let {
                    viewBinding.xyNetmode.text = it.toString()
                }
            }
        })

        viewModel.XY_WorkMode.observe(this,object : Observer<Int?> {
            override fun onChanged(value: Int?) {
                value?.let {
                    viewBinding.xyWorkmode.text = it.toString()
                }
            }
        })

        viewModel.XY_GGAFrequency.observe(this,object : Observer<Int?> {
            override fun onChanged(value: Int?) {
                value?.let {
                    viewBinding.xyGgafrequency.text = it.toString()
                }
            }
        })

        viewModel.XY_GSVFrequency.observe(this,object : Observer<Int?> {
            override fun onChanged(value: Int?) {
                value?.let {
                    viewBinding.xyGsvfrequency.text = it.toString()
                }
            }
        })

        viewModel.XY_GLLFrequency.observe(this,object : Observer<Int?> {
            override fun onChanged(value: Int?) {
                value?.let {
                    viewBinding.xyGllfrequency.text = it.toString()
                }
            }
        })

        viewModel.XY_GSAFrequency.observe(this,object : Observer<Int?> {
            override fun onChanged(value: Int?) {
                value?.let {
                    viewBinding.xyGsafrequency.text = it.toString()
                }
            }
        })

        viewModel.XY_RMCFrequency.observe(this,object : Observer<Int?> {
            override fun onChanged(value: Int?) {
                value?.let {
                    viewBinding.xyRmcfrequency.text = it.toString()
                }
            }
        })

        viewModel.XY_ZDAFrequency.observe(this,object : Observer<Int?> {
            override fun onChanged(value: Int?) {
                value?.let {
                    viewBinding.xyZdafrequency.text = it.toString()
                }
            }
        })

        viewModel.XY_TimeZone.observe(this,object : Observer<Int?> {
            override fun onChanged(value: Int?) {
                value?.let {
                    viewBinding.xyTimezone.text = it.toString()
                }
            }
        })

    }

    fun init_fd_parameter(){

        viewModel.FD_LocationReportID.observe(this,object : Observer<String?> {
            override fun onChanged(value: String?) {
                value?.let {
                    viewBinding.fdLocationreportid.text = it
                }
            }
        })

        viewModel.FD_LocationReportFrequency.observe(this,object : Observer<Int?> {
            override fun onChanged(value: Int?) {
                value?.let {
                    viewBinding.fdLocationreportfrequency.text = it.toString()
                }
            }
        })

        viewModel.FD_SOSID.observe(this, object : Observer<String?> {
            override fun onChanged(value: String?) {
                value?.let {
                    viewBinding.fdSosid.text = it
                }
            }
        })

        viewModel.FD_SOSFrequency.observe(this, object : Observer<Int?> {
            override fun onChanged(value: Int?) {
                value?.let {
                    viewBinding.fdSosfrequency.text = it.toString()
                }
            }
        })

        viewModel.FD_SOSContent.observe(this, object : Observer<String?> {
            override fun onChanged(value: String?) {
                value?.let {
                    viewBinding.fdSoscontent.text = it
                }
            }
        })

        viewModel.FD_OverboardID.observe(this, object : Observer<String?> {
            override fun onChanged(value: String?) {
                value?.let {
                    viewBinding.fdOverboardid.text = it
                }
            }
        })

        viewModel.FD_OverboardFrequency.observe(this, object : Observer<Int?> {
            override fun onChanged(value: Int?) {
                value?.let {
                    viewBinding.fdOverboardfrequency.text = it.toString()
                }
            }
        })

        viewModel.FD_OverboardContent.observe(this, object : Observer<String?> {
            override fun onChanged(value: String?) {
                value?.let {
                    viewBinding.fdOverboardcontent.text = it
                }
            }
        })

        viewModel.FD_WorkMode.observe(this, object : Observer<Int?> {
            override fun onChanged(value: Int?) {
                value?.let {
                    viewBinding.fdWorkmode.text = it.toString()
                }
            }
        })

        viewModel.FD_BatteryVoltage.observe(this, object : Observer<Int?> {
            override fun onChanged(value: Int?) {
                value?.let {
                    viewBinding.fdBatteryvoltage.text = it.toString()
                }
            }
        })

        viewModel.FD_BatteryLevel.observe(this, object : Observer<Int?> {
            override fun onChanged(value: Int?) {
                value?.let {
                    viewBinding.fdBatterylevel.text = it.toString()
                }
            }
        })

        viewModel.FD_PositioningModuleStatus.observe(this, object : Observer<Int?> {
            override fun onChanged(value: Int?) {
                value?.let {
                    viewBinding.fdPositioningmodulestatus.text = it.toString()
                }
            }
        })

        viewModel.FD_BDModuleStatus.observe(this, object : Observer<Int?> {
            override fun onChanged(value: Int?) {
                value?.let {
                    viewBinding.fdBdmodulestatus.text = it.toString()
                }
            }
        })

        viewModel.FD_SoftwareVersion.observe(this, object : Observer<String?> {
            override fun onChanged(value: String?) {
                value?.let {
                    viewBinding.fdSoftwareversion.text = it
                }
            }
        })

        viewModel.FD_HardwareVersion.observe(this, object : Observer<String?> {
            override fun onChanged(value: String?) {
                value?.let {
                    viewBinding.fdHardwareversion.text = it
                }
            }
        })

        viewModel.FD_LocationStoragePeriod.observe(this, object : Observer<Int?> {
            override fun onChanged(value: Int?) {
                value?.let {
                    viewBinding.fdLocationstorageperiod.text = it.toString()
                }
            }
        })

        viewModel.FD_BluetoothName.observe(this, object : Observer<String?> {
            override fun onChanged(value: String?) {
                value?.let {
                    viewBinding.fdBluetoothname.text = it
                }
            }
        })

        viewModel.FD_ExternalVoltage.observe(this, object : Observer<Int?> {
            override fun onChanged(value: Int?) {
                value?.let {
                    viewBinding.fdExternalvoltage.text = it.toString()
                }
            }
        })

        viewModel.FD_InternalVoltage.observe(this, object : Observer<Int?> {
            override fun onChanged(value: Int?) {
                value?.let {
                    viewBinding.fdInternalvoltage.text = it.toString()
                }
            }
        })

        viewModel.FD_Temperature.observe(this, object : Observer<Double?> {
            override fun onChanged(value: Double?) {
                value?.let {
                    viewBinding.fdTemperature.text = it.toString()
                }
            }
        })

        viewModel.FD_Humidity.observe(this, object : Observer<Double?> {
            override fun onChanged(value: Double?) {
                value?.let {
                    viewBinding.fdHumidity.text = it.toString()
                }
            }
        })

        viewModel.FD_LocationsCount.observe(this, object : Observer<Int?> {
            override fun onChanged(value: Int?) {
                value?.let {
                    viewBinding.fdLocationscount.text = it.toString()
                }
            }
        })

        viewModel.FD_CardID.observe(this, object : Observer<String?> {
            override fun onChanged(value: String?) {
                value?.let {
                    viewBinding.fdCardid.text = it
                }
            }
        })

        viewModel.FD_NumberOfResets.observe(this, object : Observer<Int?> {
            override fun onChanged(value: Int?) {
                value?.let {
                    viewBinding.fdNumberofresets.text = it.toString()
                }
            }
        })

        viewModel.FD_RNBleFeedback.observe(this, object : Observer<Int?> {
            override fun onChanged(value: Int?) {
                value?.let {
                    viewBinding.fdRnblefeedback.text = it.toString()
                }
            }
        })

        viewModel.FD_Power.observe(this, object : Observer<String?> {
            override fun onChanged(value: String?) {
                value?.let {
                    viewBinding.fdPower.text = it
                }
            }
        })

    }




}