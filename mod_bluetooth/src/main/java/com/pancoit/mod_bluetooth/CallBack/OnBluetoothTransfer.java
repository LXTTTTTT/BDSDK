package com.pancoit.mod_bluetooth.CallBack;

import android.bluetooth.BluetoothDevice;

import java.util.List;

public interface OnBluetoothTransfer {
    void onDateReceive(byte[] data_bytes);
    void onProtocolReceive(String data_str);
    void onDateSend(String data_str);
}
