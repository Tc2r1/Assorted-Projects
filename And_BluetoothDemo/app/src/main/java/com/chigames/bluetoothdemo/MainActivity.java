package com.chigames.bluetoothdemo;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_ENABLE_BT = 001;
    BluetoothAdapter bluetoothAdapter;

    ListView foundDevicesLV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        foundDevicesLV = (ListView) findViewById(R.id.listview_paired_devices);

        // Initiate bluetooth adapter.
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // Check if bluetooth is supported.
        if(bluetoothAdapter == null){
            // Device does not support bluetooth.
            Toast.makeText(this, "THIS DEVICE DOES NOT SUPPORT BLUETOOTH TECHNOLOGY.", Toast.LENGTH_SHORT).show();
            return;
        }


        // check if bluetooth is enabled
        if(bluetoothAdapter.isEnabled()){
            Toast.makeText(this, "BlueTooth is On!", Toast.LENGTH_SHORT).show();

        } else {
            // if bluetooth is not enabled, attempt to enable bluetooth.
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);

            startActivityForResult(intent, REQUEST_ENABLE_BT);

        }
    }

    public void TurnBluetoothOff(View view) {
        bluetoothAdapter.disable();
        if (bluetoothAdapter.isEnabled()){
            Toast.makeText(this, "BlueTooth could not be disabled.", Toast.LENGTH_LONG).show();

            return;
        } else {
            Toast.makeText(this, "BlueTooth Is Now Off", Toast.LENGTH_LONG).show();

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_ENABLE_BT:
                if (resultCode == RESULT_OK) {
                    if(bluetoothAdapter.isEnabled()){
                        Toast.makeText(this, "Bluetooth is now turned on", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(this, "Bluetooth Was Denied!", Toast.LENGTH_LONG).show();

                }
        }
    }

    public void FindDiscoverableDevices(View view)
    {

        // Turn on Discoverable mode for this device for 120 seconds.
        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        startActivity(intent);

        // Register for broadcasts when a device is discovered.
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver, filter);

    }

    public void ViewPairedDevices(View view)
    {

        // Gets the list of paired devices.
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

        ListView pairedDevicesLV = (ListView) findViewById(R.id.listview_paired_devices);

        // convert set into arraylist.
        ArrayList pairedDevicesArrayList = new ArrayList();

        if(pairedDevices.size() > 0) {

            // There are paired devices. Get the name and address of each paired device.
            for (BluetoothDevice bluetoothDevice : pairedDevices) {

                // There are devices, list them.
                pairedDevicesArrayList.add(bluetoothDevice.getName() + ",  Mac Address: "+ bluetoothDevice.getAddress());

            }
        }

        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, pairedDevicesArrayList);
        pairedDevicesLV.setAdapter(arrayAdapter);
    }

    // Create a BroadcastReceiver for ACTION_FOUND.
    private final BroadcastReceiver receiver = new BroadcastReceiver()
    {
        ArrayList foundDevicesArrayList = new ArrayList();
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action))
            {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
                Log.wtf("DEVICE FOUND:", deviceName + ",  Mac Address: "+ deviceHardwareAddress);
                foundDevicesArrayList.add(deviceName + ",  Mac Address: "+ deviceHardwareAddress);

            }

            ArrayAdapter arrayAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, foundDevicesArrayList);
            foundDevicesLV.setAdapter(arrayAdapter);
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // unregister the ACTION_FOUND receiver.
        unregisterReceiver(receiver);
    }
}
