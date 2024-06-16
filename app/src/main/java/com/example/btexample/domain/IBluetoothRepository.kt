package com.example.btexample.domain

import android.bluetooth.BluetoothDevice
import com.example.btexample.domain.model.DiscoveredDevice
import kotlinx.coroutines.flow.Flow

interface IBluetoothRepository {
    fun observeBluetoothEnabled(): Flow<Boolean>
    fun startDiscovery()
    fun getDiscoveredDevices(): Flow<List<BluetoothDevice>>
}