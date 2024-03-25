package com.pancoit.mod_bluetooth.BLE;

import static java.lang.Thread.sleep;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.util.Log;


import androidx.annotation.NonNull;

import com.pancoit.mod_bluetooth.CallBack.OnBluetoothTransfer;
import com.pancoit.mod_bluetooth.CallBack.OnBluetoothWork;
import com.pancoit.mod_bluetooth.Global.Constant;
import com.pancoit.mod_bluetooth.Global.Variable;
import com.pancoit.mod_bluetooth.Utils.DataUtils;
import com.pancoit.mod_bluetooth.Utils.DispatcherExecutor;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BluetoothTransfer {

    private static String TAG = "BluetoothTransfer";
    private BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private Context context = null;
    private List<BluetoothDevice> devices = new ArrayList();
    private static final UUID ID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
    private int max_size = 20;  // 每次传输数据的最大大小，连接设备时拿到，如果发的数据大于这个大小就需要拆包
    private int max_mtu = 503;  // 设置传输数据的最大值
    private BluetoothGatt bluetoothGatt = null;
    private BluetoothGattCharacteristic writeCharacteristic = null;
    private boolean isSetNotification = false;  // 是否设置了可通知特征值

    private volatile boolean data_send_complete = true;  // 数据发送完成标识
    private Queue<byte[]> queue = new LinkedList();  // 消息队列
    private Queue<byte[]> remainQueue = null;
    private String deviceAddress = "";

    private static BluetoothTransfer bluetoothTransferUtil;
    public static BluetoothTransfer getInstance() {
        if (bluetoothTransferUtil == null) {
            bluetoothTransferUtil = new BluetoothTransfer();
        }
        return bluetoothTransferUtil;
    }

    private void loge(String log){
        if(Variable.showLog){
            Log.e(TAG, log);
        }
    }
    private void logi(String log){
        if(Variable.showLog){
            Log.i(TAG, log);
        }
    }

    public void showLog(boolean show){Variable.showLog = show;}
    public void setMatchingRules(String[] rules){Variable.setMatchingRules(rules);}
    public void enableFiltering(boolean enable){Variable.enableCommonInstructionFiltering = enable;}

    private BluetoothReceiver receiver = null;
    // 开始扫描
    public void startDiscovery(@NonNull Context context) {
        if(bluetoothAdapter==null){return;}
        this.receiver = new BluetoothReceiver();
        this.context = context;
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constant.ACTION_BLUETOOTH_FIND);
        filter.addAction(Constant.ACTION_DISCOVERY_FINISHED);
        filter.addAction(Constant.ACTION_ACL_CONNECTED);
        filter.addAction(Constant.ACTION_ACL_DISCONNECTED);
        context.registerReceiver(receiver, filter);
        loge("注册蓝牙监听广播");
        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }
        bluetoothAdapter.startDiscovery();
    }

    // 停止扫描
    public void stopDiscovery() {
        if (bluetoothAdapter!=null && bluetoothAdapter.isDiscovering()) {
            devices.clear();
            bluetoothAdapter.cancelDiscovery();
        }
        if(context!=null && receiver!=null){
            context.unregisterReceiver(receiver);
            receiver=null;
        }
    }

    private class BluetoothReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Constant.ACTION_BLUETOOTH_FIND.equals(action)) {
                BluetoothDevice device = (BluetoothDevice) intent.getParcelableExtra(Constant.CONSTANT_BLUETOOTH_DEVICE);
                if (device.getName() == null) {return;}
                if (!devices.contains(device)) {
                    devices.add(device);
                    if(onBluetoothWork!=null){onBluetoothWork.onScanningResult(devices);}
                }
            }
        }
    }

    // 蓝牙回调
    private final BluetoothGattCallback bluetoothGattCallback = new BluetoothGattCallback() {
        // 连接状态改变
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            loge("onConnectionStateChange：蓝牙状态改变 " + status + " // "+newState);
            if (status == BluetoothGatt.GATT_SUCCESS && newState == BluetoothGatt.STATE_CONNECTED) {
                loge("蓝牙连接");
                // 设置传输速率
                if(!setMtu()){
                    bluetoothGatt.discoverServices();
                };
            }
            // 蓝牙自动断开
            else if (status == BluetoothGatt.GATT_SERVER && newState == BluetoothGatt.STATE_DISCONNECTED) {
                // 重连
                disconnect();
                connectDevice(deviceAddress);
            }
            // 蓝牙手动断开
            else if (newState == BluetoothGatt.STATE_DISCONNECTED) {
                loge("蓝牙断开");
                disconnect();
            }

        }

        public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
            super.onMtuChanged(gatt, mtu, status);
            loge("onMtuChanged 改变 MTU ："+status + " // " + mtu);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                max_size = mtu - 3;  // 拿到最大的分包大小
            } else {
                loge("改变 MTU 失败");
            }
            gatt.discoverServices();
        }

        // 连接设备成功后发现服务时
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            loge("onServicesDiscovered 发现服务 ："+status);
            if(status == BluetoothGatt.GATT_SUCCESS){
                List<BluetoothGattService> servicesList = gatt.getServices();
                analysisGattService(gatt, servicesList);  // 根据设备选择读和写
                if (onBluetoothWork != null) {
                    onBluetoothWork.onConnect();
                }
            }else {
                loge("onServicesDiscovered：发现服务失败");
            }
        }

        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            loge("onDescriptorWrite 状态：" + status);
            super.onDescriptorWrite(gatt, descriptor, status);
            loge("onDescriptorWrite 写入描述符：" + status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                loge("onDescriptorWrite：写入描述符成功");
            } else {
                loge("onDescriptorWrite：写入描述符失败");
            }
        }

        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            // 数据接收
            ExecutorService executorService = DispatcherExecutor.INSTANCE.getIOExecutor();
            if(executorService!=null){
                executorService.execute(new Runnable() {
                    @Override
                    public void run() {
                        receiveData(characteristic);
                    }
                });
            }
        }

        // 写入数据 write(byte[] data) 方法里面 bluetoothGatt.writeCharacteristic 时被调用，每次下发数据后判断 remainQueue 拆包后的数据有没有下发完
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            loge( "onCharacteristicWrite: 下发数据 "+status );
            if (remainQueue != null && !remainQueue.isEmpty()) {
                byte[] send_bytes = (byte[]) remainQueue.remove();  // 从队列取出消息
                write(send_bytes);
            } else {
                try {
                    Thread.sleep(150L);
                } catch (InterruptedException var5) {
                    var5.printStackTrace();
                }
                data_send_complete = true;  // 队列消息发送完了，修改标识
                sendNext();
            }
        }

        public void onPhyUpdate(BluetoothGatt gatt, int txPhy, int rxPhy, int status) {
            super.onPhyUpdate(gatt, txPhy, rxPhy, status);
            loge("onPhyUpdate: 状态 "+status );
        }
    };

    // 通过设备连接
    public void connectDevice(final BluetoothDevice device) {
        stopDiscovery();
        deviceAddress = device.getAddress();
        // 子线程连接
        new Thread() {
            public void run() {
                if (bluetoothGatt != null) {
                    bluetoothGatt.close();
                    bluetoothGatt = null;
                }
                if (Build.VERSION.SDK_INT >= 23) {
                    bluetoothGatt = device.connectGatt(context, false, bluetoothGattCallback, BluetoothDevice.TRANSPORT_LE);  // 低功耗
                } else {
                    bluetoothGatt = device.connectGatt(context, false, bluetoothGattCallback);
                }
            }
        }.start();
    }

    // 通过地址连接
    public void connectDevice(final String address) {
        stopDiscovery();
        deviceAddress = address;
        // 子线程连接
        new Thread() {
            public void run() {
                if (bluetoothAdapter != null && deviceAddress != null && !deviceAddress.equals("")) {
                    BluetoothDevice device;
                    if ((device = bluetoothAdapter.getRemoteDevice(deviceAddress)) != null) {
                        if (bluetoothGatt != null) {
                            bluetoothGatt.close();
                            bluetoothGatt = null;
                        }
                        if (Build.VERSION.SDK_INT >= 23) {
                            bluetoothGatt = device.connectGatt(context, false, bluetoothGattCallback, BluetoothDevice.TRANSPORT_LE);  // 低功耗
                        } else {
                            bluetoothGatt = device.connectGatt(context, false, bluetoothGattCallback);
                        }
                    }
                }
            }
        }.start();
    }

    private boolean setMtu() {
        if(bluetoothGatt==null) return false;
        if (Build.VERSION.SDK_INT >= 21) {
            return bluetoothGatt.requestMtu(max_mtu);
        }
        return false;
    }

    private void write(byte[] data_bytes) {
        if(writeCharacteristic==null || bluetoothGatt==null) return;
        writeCharacteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
        writeCharacteristic.setValue(data_bytes);
        bluetoothGatt.writeCharacteristic(writeCharacteristic);
        String data_str = DataUtils.bytes2string(data_bytes);
        loge("Bluetooth 下发数据: " + data_str );
        if(onBluetoothTransfer!=null){onBluetoothTransfer.onDateSend(data_str);}
    }

    public void writeHex(String data_hex) {
        if (writeCharacteristic == null || bluetoothGatt == null) return;
        byte[] data_bytes = DataUtils.hex2bytes(data_hex);
        queue.offer(data_bytes);
        sendNext();
    }

    public void writeBytes(byte[] data_bytes) {
        if (writeCharacteristic == null || bluetoothGatt == null) return;
        queue.offer(data_bytes);
        sendNext();
    }

    private synchronized void sendNext() {
        if (data_send_complete) {
            if (queue.size() == 0) return;
            data_send_complete = false;
            send((byte[]) queue.poll());
        }
    }

    private synchronized void send(byte[] bytes) {
        ExecutorService executorService = DispatcherExecutor.INSTANCE.getIOExecutor();
        if(executorService!=null){
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    if (bytes != null) {
                        // 判断有没有超出最大的限制
                        if (bytes.length > max_size) {
                            List<byte[]> list = splitPackage(bytes, max_size);
                            remainQueue = new ConcurrentLinkedQueue();
                            remainQueue.addAll(list);
                            byte[] send_bytes = (byte[]) remainQueue.remove();
                            write(send_bytes);
                        } else {
                            write(bytes);
                            try {Thread.sleep(150L);} catch (Exception var4) {}
                            data_send_complete = true;
                            sendNext();
                        }
                    }
                }
            });
        }
    }

    // 将字节数组按照指定的大小拆分成一个数组列表
    private static List<byte[]> splitPackage(byte[] src, int size) {
        List<byte[]> list = new ArrayList<>();
        int loop = (src.length + size - 1) / size;

        for (int i = 0; i < loop; i++) {
            int from = i * size;
            int to = Math.min(from + size, src.length);
            byte[] chunk = new byte[to - from];
            System.arraycopy(src, from, chunk, 0, to - from);
            list.add(chunk);
        }

        return list;
    }

    private ByteArrayOutputStream baos = new ByteArrayOutputStream();
    private byte[] readBuffer = new byte[1024 * 2];  // 缓冲区
    // 这里处理接收到的数据
    private void receiveData(BluetoothGattCharacteristic characteristic) {
        byte[] data = characteristic.getValue();
        if(onBluetoothTransfer!=null){onBluetoothTransfer.onDateReceive(data);}
        baos.write(data,0,data.length);
        readBuffer = baos.toByteArray();
        // 停止位，当最后两位为 \r\n
        if (readBuffer.length >= 2 && readBuffer[readBuffer.length - 2] == (byte)'\r' && readBuffer[readBuffer.length - 1] == (byte)'\n') {
            String data_str = DataUtils.bytes2string(readBuffer);
            String data_hex = DataUtils.bytes2Hex(readBuffer);
            logi("收到蓝牙数据: " + data_str);
            String[] data_hex_array = data_hex.split("0d0a");  // 分割后处理
            for (String s : data_hex_array) {
                String s_str = DataUtils.hex2String(s);
                if(Variable.enableCommonInstructionFiltering){
                    String matchingRules = Variable.matchingRules;
                    Pattern pattern = Pattern.compile(matchingRules);
                    Matcher matcher = pattern.matcher(s_str);
                    if (matcher.find()) {
                        if(onBluetoothTransfer!=null){onBluetoothTransfer.onProtocolReceive(s_str);}
                    }
                } else {
                    if(onBluetoothTransfer!=null){onBluetoothTransfer.onProtocolReceive(s_str);}
                }
            }
            baos.reset();
        }

    }

    // 断开连接
    public void disconnect(){
        try {
            if(remainQueue!=null){
                remainQueue.clear();
                remainQueue = null;
            }
            queue.clear();
            data_send_complete = true;
            isSetNotification = false;
            writeCharacteristic = null;
            devices.clear();
            if(bluetoothGatt!=null){
                bluetoothGatt.disconnect();
                bluetoothGatt.close();
                bluetoothGatt = null;
            }
            if (onBluetoothWork != null) {
                onBluetoothWork.onDisconnect();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


// 设备特征值兼容 --------------------------------------------------------------------
    private void analysisGattService(BluetoothGatt gatt, List<BluetoothGattService> servicesList) {
        Iterator var3 = servicesList.iterator();
        while (var3.hasNext()) {
            BluetoothGattService service = (BluetoothGattService) var3.next();
            String uuid = service.getUuid().toString();
            // 如果以 "0000180" 开头就不要
            if (uuid.startsWith("0000180")) {
                continue;
            }
            loge("服务UUID:" + uuid);
            List writes = (List) writeSC.get(uuid);
            List notifys = (List) notifySC.get(uuid);
            if (writes == null || notifys == null) {
                continue;
            }
            List<BluetoothGattCharacteristic> characteristics = service.getCharacteristics();
            Iterator var9 = characteristics.iterator();
            while (var9.hasNext()) {
                BluetoothGattCharacteristic characteristic = (BluetoothGattCharacteristic) var9.next();
                String characteristicUUID = characteristic.getUuid().toString();
                loge("特征值:" + characteristicUUID);
                int charaProp = characteristic.getProperties();
                if (((charaProp & 4) > 0 || (charaProp & 8) > 0) && writes.contains(characteristicUUID) && writeCharacteristic == null) {
                    writeCharacteristic = characteristic;
                    loge("可写特征值：" + characteristicUUID);
                }
                if (((charaProp & 16) > 0 || (charaProp & 32) > 0) && notifys.contains(characteristicUUID) && !isSetNotification) {
                    BluetoothGattDescriptor descriptor = characteristic.getDescriptor(ID);
                    if (descriptor != null) {
                        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);  // 启用特征值的通知功能
                        gatt.writeDescriptor(descriptor);
                        gatt.setCharacteristicNotification(characteristic, true);
                        isSetNotification = true;
                        loge("可通知特征值：" + characteristicUUID);
                    }
                }
            }
        }
    }
    private static Map<String, List<String>> writeSC = new HashMap();  // 写
    private static Map<String, List<String>> notifySC = new HashMap();  // 读
    // 键
    private static String FFE0 = "0000ffe0-0000-1000-8000-00805f9b34fb";
    private static String FFF0 = "0000fff0-0000-1000-8000-00805f9b34fb";
    private static String FFE5 = "0000ffe5-0000-1000-8000-00805f9b34fb";
    private static String A002 = "0000a002-0000-1000-8000-00805f9b34fb";
    private static String E400001 = "6e400001-b5a3-f393-e0a9-e50e24dcca9e";
    // 值
    private static String FFE1 = "0000ffe1-0000-1000-8000-00805f9b34fb";  // 写：FFE0  读：FFE0、FFE5
    private static String FFF2 = "0000fff2-0000-1000-8000-00805f9b34fb";  // 写：FFF0
    private static String FFF3 = "0000fff3-0000-1000-8000-00805f9b34fb";  // 写：FFF0
    private static String FFE9 = "0000ffe9-0000-1000-8000-00805f9b34fb";  // 写：FFE5
    private static String FFF1 = "0000fff1-0000-1000-8000-00805f9b34fb";  // 写：FFE5  读：FFF0
    private static String C302 = "0000c302-0000-1000-8000-00805f9b34fb";  // 写：A002
    private static String E400002 = "6e400002-b5a3-f393-e0a9-e50e24dcca9e";  // 写：E400001
    private static String FFE2 = "0000ffe2-0000-1000-8000-00805f9b34fb";  // 读：FFE0
    private static String FFE4 = "0000ffe4-0000-1000-8000-00805f9b34fb";  // 读：FFE0
    private static String FFF4 = "0000fff4-0000-1000-8000-00805f9b34fb";  // 读：FFF0
    private static String C305 = "0000c305-0000-1000-8000-00805f9b34fb";  // 读：A002
    private static String E400003 = "6e400003-b5a3-f393-e0a9-e50e24dcca9e";  // 读：E400001
    static {
        List<String> w_ffe0 = new ArrayList();
        w_ffe0.add(FFE1);
        writeSC.put(FFE0, w_ffe0);
        List<String> w_fff0 = new ArrayList();
        w_fff0.add(FFF2);
        w_fff0.add(FFF3);
        writeSC.put(FFF0, w_fff0);
        List<String> w_ffe5 = new ArrayList();
        w_ffe5.add(FFE9);
        w_ffe5.add(FFE1);
        writeSC.put(FFE5, w_ffe5);
        List<String> w_a002 = new ArrayList();
        w_a002.add(C302);
        writeSC.put(A002, w_a002);
        List<String> w_6E400001 = new ArrayList();
        w_6E400001.add(E400002);
        writeSC.put(E400001, w_6E400001);

        List<String> r_ffe0 = new ArrayList();
        r_ffe0.add(FFE1);
        r_ffe0.add(FFE2);
        r_ffe0.add(FFE4);
        notifySC.put(FFE0, r_ffe0);
        List<String> r_fff0 = new ArrayList();
        r_fff0.add(FFF4);
        r_fff0.add(FFF1);
        notifySC.put(FFF0, r_fff0);
        List<String> r_6E400001 = new ArrayList();
        r_6E400001.add(E400003);
        notifySC.put(E400001, r_6E400001);
        List<String> r_a002 = new ArrayList();
        r_a002.add(C305);
        notifySC.put(A002, r_a002);
        List<String> r_ffe5 = new ArrayList();
        r_ffe5.add(FFE1);
        notifySC.put(FFE5, r_ffe5);
    }

// 接口 ---------------------------------------
    private OnBluetoothWork onBluetoothWork;
    public void setOnBluetoothWork(OnBluetoothWork onBluetoothWork){
        this.onBluetoothWork = onBluetoothWork;
    }
    private OnBluetoothTransfer onBluetoothTransfer;
    public void setOnBluetoothTransfer(OnBluetoothTransfer onBluetoothTransfer){
        this.onBluetoothTransfer = onBluetoothTransfer;
    }

}
