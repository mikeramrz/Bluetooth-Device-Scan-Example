package com.example.btexample

import com.example.btexample.domain.model.DiscoveredDevice

sealed interface DiscoverableDevicesUiState {
    data object NotStarted : DiscoverableDevicesUiState
    data object Loading : DiscoverableDevicesUiState
    data class Success(val devices: List<DiscoveredDevice>) : DiscoverableDevicesUiState
    data class Error(val message: String) : DiscoverableDevicesUiState
}