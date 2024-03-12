package com.pancoit.mod_bluetooth.CallBack;

import android.bluetooth.BluetoothDevice;

import java.util.List;

public interface OnBluetoothWork {
    void onScanningResult(List<BluetoothDevice> devices);
    void onConnect();
    void onDisconnect();
}
