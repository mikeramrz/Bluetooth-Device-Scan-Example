package com.example.btexample

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.btexample.ui.theme.BtExampleTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BtExampleTheme {
                val navController = rememberNavController()
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    BtExampleNavGraph(
                        navController = navController,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun BtExampleNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {

    NavHost(navController = navController, startDestination = "main", modifier = modifier) {
        composable("main") {
            MainScreen(navController = navController)
        }
        composable("bluetooth") {
            BluetoothScreen()
        }

    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun MainScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val permissionsState = rememberMultiplePermissionsState(
        permissions =
        listOf(
            android.Manifest.permission.BLUETOOTH_SCAN,
            android.Manifest.permission.BLUETOOTH_CONNECT,
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.BLUETOOTH

        )
    )

    LaunchedEffect(key1 = permissionsState) {
        if (permissionsState.allPermissionsGranted) {
            navController.navigate("bluetooth")
        }
    }



    Box(modifier = modifier.fillMaxSize()) {

        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(all = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            if (permissionsState.allPermissionsGranted){
                Text(
                    text = "All permissions granted. Continue to Bluetooth screen.",
                    style = MaterialTheme.typography.displayMedium
                )
            }
            else{
                Text(
                    text = "Please grant permissions to continue.",
                    style = MaterialTheme.typography.displayMedium
                )
            }

            Button(onClick = {
                if (permissionsState.allPermissionsGranted) {
                    navController.navigate("bluetooth")
                } else {
                    permissionsState.launchMultiplePermissionRequest()
                }
            }) {
                if (permissionsState.allPermissionsGranted) {
                    Text("Continue to Bluetooth")
                } else {
                    Text("Grant Permissions")
                }
            }
        }
    }

}


