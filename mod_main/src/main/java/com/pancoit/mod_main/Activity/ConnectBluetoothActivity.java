package com.pancoit.mod_main.Activity;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.pancoit.mod_bluetooth.BLE.BluetoothTransfer;
import com.pancoit.mod_bluetooth.CallBack.OnBluetoothTransfer;
import com.pancoit.mod_bluetooth.CallBack.OnBluetoothWork;
import com.pancoit.mod_main.Adapter.BluetoothListAdapter;
import com.pancoit.mod_main.Base.BaseViewBindingActivity;
import com.pancoit.mod_main.Global.Constant;
import com.pancoit.mod_main.Utils.ApplicationUtils;
import com.pancoit.mod_main.Utils.Connection.BaseConnector;
import com.pancoit.mod_main.Utils.GlobalControlUtils;
import com.pancoit.mod_main.ViewModel.BDVM;
import com.pancoit.mod_main.databinding.ActivityConnectBluetoothBinding;
import com.pancoit.mod_parse.Protocol.ProtocolParser;

import java.util.ArrayList;
import java.util.List;

import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.jvm.functions.Function2;

@Route(path = Constant.CONNECT_BLUETOOTH_ACTIVITY)
public class ConnectBluetoothActivity extends BaseViewBindingActivity<ActivityConnectBluetoothBinding> {

    private String TAG = "ConnectBluetoothActivity";
    private List<BluetoothDevice> bluetoothDevices = new ArrayList<>();
    private BluetoothListAdapter bluetoothListAdapter;

    @Override public void beforeSetLayout() {}
    @Nullable @Override public Object initDataSuspend(@NonNull Continuation<? super Unit> $completion) {return null;}

    @Override
    public void initView(@Nullable Bundle savedInstanceState) {
        init_bluetooth_list();
        init_bluetooth_callback();
    }

    @Override
    public void initData() {
        // 开启蓝牙扫描
        BluetoothTransfer.getInstance().startDiscovery(this);
    }

    private void init_bluetooth_list(){
        bluetoothListAdapter = new BluetoothListAdapter();
        // 列表点击
        bluetoothListAdapter.setOnItemClickListener(new Function2<View, Integer, Unit>() {
            @Override
            public Unit invoke(View view, Integer integer) {
                loge("点击蓝牙项数："+integer);
                BaseConnector.Companion.getConnector().connect(bluetoothListAdapter.getItem(integer));
                return null;
            }
        });
        getViewBinding().bluetoothList.setLayoutManager(new LinearLayoutManager(my_context, LinearLayoutManager.VERTICAL, false));
        getViewBinding().bluetoothList.setAdapter(bluetoothListAdapter);
    }

    private void init_bluetooth_callback(){
        // 蓝牙工作回调
        BluetoothTransfer.getInstance().setOnBluetoothWork(new OnBluetoothWork() {
            @Override
            public void onScanningResult(List<BluetoothDevice> devices) {
                bluetoothDevices = devices;
                bluetoothListAdapter.setData(bluetoothDevices);
            }

            @Override public void onConnect() {
                GlobalControlUtils.INSTANCE.hideLoadingDialog();
                GlobalControlUtils.INSTANCE.showToast("连接成功",0);
                ApplicationUtils.INSTANCE.getGlobalViewModel(BDVM.class).isConnectDevice().postValue(true);  // 修改连接参数
                // 下发初始化指令
                if(BaseConnector.Companion.getConnector()!=null){
                    BaseConnector.Companion.getConnector().initDevice();
                }
                finish();
            }
            @Override public void onDisconnect() {
                GlobalControlUtils.INSTANCE.hideLoadingDialog();
                GlobalControlUtils.INSTANCE.showToast("断开连接",0);
                ApplicationUtils.INSTANCE.getGlobalViewModel(BDVM.class).initDeviceParameter();  // 初始化连接参数
                ProtocolParser.getInstance().Init();  // 初始化解析器参数
            }
        });
        // 蓝牙数据回调
        BluetoothTransfer.getInstance().setOnBluetoothTransfer(new OnBluetoothTransfer() {
            @Override
            public void onDateReceive(byte[] data_bytes) {
//                String data_str = DataUtils.bytes2string(data_bytes);
//                Log.i(TAG, "接收到蓝牙反馈原始数据："+data_str);
            }

            @Override
            public void onProtocolReceive(String data_str) {
//                loge("接收到蓝牙反馈指令："+data_str);
                ProtocolParser.getInstance().parseData(data_str);// 解析
            }

            @Override
            public void onDateSend(String data_str) {
                loge("蓝牙下发数据："+data_str);
            }
        });
        // 开启蓝牙工作日志打印
        BluetoothTransfer.getInstance().showLog(false);
        // 设置指令过滤规则
        BluetoothTransfer.getInstance().setMatchingRules(new String[]{"FKI","ICP","ZDX"});
        // 启用指令过滤
        BluetoothTransfer.getInstance().enableFiltering(false);
    }


    @Override
    protected void onDestroy() {
        // 退出扫描页面时记得关闭蓝牙扫描功能
        BluetoothTransfer.getInstance().stopDiscovery();
        super.onDestroy();
    }
}
