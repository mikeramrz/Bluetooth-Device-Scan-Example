package com.example.btexample.data

import android.Manifest
import android.app.Application
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import com.example.btexample.domain.IBluetoothRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class BluetoothRepository @Inject constructor(
    private val application: Application
) : IBluetoothRepository {
    private val bluetoothManager: BluetoothManager =
        application.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager

    private val bluetoothAdapter = bluetoothManager.adapter

    private val _bluetoothEnabled = MutableStateFlow(bluetoothAdapter.isEnabled)
    //Expose if bluetooth is enabled via flow
    override fun observeBluetoothEnabled(): Flow<Boolean> = _bluetoothEnabled.asStateFlow()

    private val bluetoothStateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                BluetoothAdapter.ACTION_STATE_CHANGED -> {
                    val state =
                        intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)
                    _bluetoothEnabled.value = (state == BluetoothAdapter.STATE_ON)
                }
            }
        }
    }

    //We would usually not set up broadcast recievers in the repository, but for the sake of this example we will.
    private val bluetoothDiscoveryReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                BluetoothDevice.ACTION_FOUND -> {
                    //We would normally check our version code >=33 here and use getParcelableExtra(BluetoothDevice.EXTRA_DEVICE, BluetoothDevice::class.java)
                    val device: BluetoothDevice? = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    device?.let {
                        val currentList = _discoveredDevices.value.toMutableList()
                        if (!currentList.contains(it)) {
                            currentList.add(it)
                            _discoveredDevices.value = currentList
                        }
                    }
                }
                BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                    // Discovery finished
                }
            }
        }
    }

    private val _discoveredDevices = MutableStateFlow<List<BluetoothDevice>>(emptyList())
    override fun getDiscoveredDevices(): Flow<List<BluetoothDevice>> = _discoveredDevices.asStateFlow()

    init {
        val filter = IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
        application.registerReceiver(bluetoothStateReceiver, filter)

        val discoveryFilter = IntentFilter().apply {
            addAction(BluetoothDevice.ACTION_FOUND)
            addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
        }
        application.registerReceiver(bluetoothDiscoveryReceiver, discoveryFilter)
    }


    override fun startDiscovery() {

        if (ActivityCompat.checkSelfPermission(
                application,
                Manifest.permission.BLUETOOTH_SCAN
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        if (bluetoothAdapter.isDiscovering) {
            bluetoothAdapter.cancelDiscovery()
        }
    }
}

















