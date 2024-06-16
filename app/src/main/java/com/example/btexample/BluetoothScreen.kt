package com.example.btexample

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun BluetoothScreen(
    modifier: Modifier = Modifier,
    viewModel: BluetoothViewModel = hiltViewModel(),
) {

    val bluetoothEnabled by viewModel.bluetoothEnabled.collectAsStateWithLifecycle()

    val discoveredDevicesUiState by viewModel.discoveredDevicesUiState.collectAsStateWithLifecycle()

    val permissionState = rememberMultiplePermissionsState(
        permissions = listOf(
            android.Manifest.permission.BLUETOOTH_SCAN,
            android.Manifest.permission.BLUETOOTH_CONNECT,
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.BLUETOOTH

        )
    )

    LaunchedEffect(key1 = permissionState) {
        if (permissionState.allPermissionsGranted) {
            viewModel.setPermissionGranted(true)
        } else {
            permissionState.launchMultiplePermissionRequest()
        }

    }

    // We can extract this into two separate composables.
    Column(
        modifier = modifier
            .padding(top = 32.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(36.dp)
    ) {
        when (bluetoothEnabled) {
            true -> Text(text = "Bluetooth is enabled. Nice!")
            false -> Text(text = "Bluetooth is disabled. Enable bluetooth to scan devices")
        }


        Button(
            onClick = { viewModel.startDiscovery() },
            enabled = bluetoothEnabled
        ) {
            Text(text = "Scan for devices")
        }

        when (discoveredDevicesUiState) {
            is DiscoverableDevicesUiState.Loading -> CircularProgressIndicator()
            is DiscoverableDevicesUiState.Success -> {
                val devices =
                    (discoveredDevicesUiState as DiscoverableDevicesUiState.Success).devices
                LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    items(devices) { device ->
                        Column {
                            if (device.name == null) Text(text = "Unknown")
                            else Text(text = device.name)
                            Text(text = device.address)
                        }
                    }
                }
            }

            is DiscoverableDevicesUiState.Error -> {
                val error = (discoveredDevicesUiState as DiscoverableDevicesUiState.Error).message
                Text(text = "Error: $error")
            }

            DiscoverableDevicesUiState.NotStarted -> {

            }
        }


    }
}
