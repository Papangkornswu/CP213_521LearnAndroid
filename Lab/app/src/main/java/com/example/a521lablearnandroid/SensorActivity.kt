package com.example.a521lablearnandroid

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel

class SensorActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SensorScreen()
        }
    }
}

@Composable
fun SensorScreen(viewModel: SensorViewModel = viewModel()) {
    val context = LocalContext.current
    val accelerometerData by viewModel.accelerometerData.collectAsState()
    val locationData by viewModel.locationData.collectAsState()

    val permissionsToRequest = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions.entries.all { it.value }
        if (granted) {
            viewModel.startLocationUpdates()
        } else {
            Toast.makeText(context, "Location permission denied!", Toast.LENGTH_SHORT).show()
        }
    }

    DisposableEffect(Unit) {
        viewModel.startSensors()

        val hasPermission = permissionsToRequest.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }

        if (hasPermission) {
            viewModel.startLocationUpdates()
        } else {
            permissionLauncher.launch(permissionsToRequest)
        }

        onDispose {
            viewModel.stopSensors()
            viewModel.stopLocationUpdates()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Accelerometer Data", modifier = Modifier.padding(bottom = 8.dp))
        Text(text = "X: ${accelerometerData[0]}")
        Text(text = "Y: ${accelerometerData[1]}")
        Text(text = "Z: ${accelerometerData[2]}")

        Spacer(modifier = Modifier.height(32.dp))

        Text(text = "Location Data (GPS)", modifier = Modifier.padding(bottom = 8.dp))
        if (locationData != null) {
            Text(text = "Latitude: ${locationData?.latitude}")
            Text(text = "Longitude: ${locationData?.longitude}")
        } else {
            Text(text = "Waiting for location...")
        }
    }
}
