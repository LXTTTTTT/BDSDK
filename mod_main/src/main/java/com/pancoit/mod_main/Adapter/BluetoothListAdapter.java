package com.pancoit.mod_main.Adapter;

import android.bluetooth.BluetoothDevice;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.pancoit.mod_main.databinding.AdapterBluetoothItemBinding;


public class BluetoothListAdapter extends BaseRecyclerViewAdapter<BluetoothDevice, AdapterBluetoothItemBinding> {

    @NonNull
    @Override
    public AdapterBluetoothItemBinding getViewBinding(@NonNull LayoutInflater layoutInflater, @NonNull ViewGroup parent, int viewType) {
        return AdapterBluetoothItemBinding.inflate(layoutInflater,parent,false);
    }

    @Override
    protected void onBindDefViewHolder(@NonNull BaseBindViewHolder<AdapterBluetoothItemBinding> holder, @Nullable BluetoothDevice item, int position) {
        if(item==null){return;}
        AdapterBluetoothItemBinding binding = holder.getBinding();
        if(binding!=null){
            binding.deviceName.setText(item.getName());
            binding.deviceAddr.setText(item.getAddress());
        }
    }


}
