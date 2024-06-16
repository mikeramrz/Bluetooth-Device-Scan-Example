package com.example.btexample

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.btexample.domain.IBluetoothRepository
import com.example.btexample.domain.model.DiscoveredDevice
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BluetoothViewModel
@Inject constructor(
    private val bluetoothRepository: IBluetoothRepository
): ViewModel(){

    private val _discoveredDevicesUiState = MutableStateFlow<DiscoverableDevicesUiState>(DiscoverableDevicesUiState.NotStarted)
    val discoveredDevicesUiState = _discoveredDevicesUiState.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = DiscoverableDevicesUiState.NotStarted
    )

    val bluetoothEnabled: StateFlow<Boolean> = bluetoothRepository.observeBluetoothEnabled().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = false
    )

    private val _permissionGranted = MutableStateFlow(false)

    fun startDiscovery() {
        if (_permissionGranted.value) {
            _discoveredDevicesUiState.value = DiscoverableDevicesUiState.Loading
            bluetoothRepository.startDiscovery()
            collectDiscoveredDevices()
        }
        else{
            _discoveredDevicesUiState.value = DiscoverableDevicesUiState.Error("Permission not granted")
        }
    }

    private fun collectDiscoveredDevices() {
        viewModelScope.launch {
            bluetoothRepository.getDiscoveredDevices().collectLatest { devices ->
                if (devices.isNotEmpty()) {
                    _discoveredDevicesUiState.value = DiscoverableDevicesUiState.Success(devices.map { DiscoveredDevice(it.name, it.address) })
                } else {
                    _discoveredDevicesUiState.value = DiscoverableDevicesUiState.Error("No devices found")
                }
            }
        }
    }



    fun setPermissionGranted(granted: Boolean) {
        _permissionGranted.value = granted
    }

}


