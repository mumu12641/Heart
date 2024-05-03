package io.github.mumu12641.ui.page.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Bluetooth
import androidx.compose.material.icons.outlined.BluetoothConnected
import androidx.compose.material.icons.outlined.BluetoothDisabled
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.GetApp
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.StopCircle
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import io.github.mumu12641.BLE.BLEState
import io.github.mumu12641.BLE.util.client.Device
import io.github.mumu12641.R
import io.github.mumu12641.ui.component.EcgChart
import io.github.mumu12641.util.Route
import io.github.mumu12641.util.Route.ABOUT
import io.github.mumu12641.util.Route.HISTORY

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController, homeViewModel: HomeViewModel = hiltViewModel()) {
    val uiState by homeViewModel.uiState.collectAsState()
    val bleState = uiState.bluetoothState.bleState
    val scanning = bleState == BLEState.Scanning
    val receiveCharacteristic = uiState.bluetoothState.receiveCharacteristic
    val running =
        bleState == BLEState.Fetching || bleState == BLEState.Stop || bleState == BLEState.Saving

    Scaffold(topBar = {
        LargeTopAppBar(modifier = Modifier.padding(horizontal = 8.dp), title = {
            Text(text = stringResource(id = R.string.app_name))
        }, navigationIcon = {
            IconButton(
                onClick = { navController.navigate(Route.PROFILE) },
            ) {
                Icon(
                    Icons.Outlined.Person,
                    contentDescription = null,
                )
            }
        }, actions = {
            Row {

                IconButton(
                    onClick = { navController.navigate(HISTORY) },
                ) {
                    Icon(
                        Icons.Outlined.History,
                        contentDescription = null,
                    )
                }
                IconButton(
                    onClick = { navController.navigate(ABOUT) },
                ) {
                    Icon(
                        Icons.Outlined.ErrorOutline,
                        contentDescription = null,
                    )
                }
            }


        })
    }, content = { paddingValues ->
        HomeContent(
            modifier = Modifier.padding(paddingValues), homeViewModel
        )
    }, floatingActionButton = {
        Column {
            AnimatedVisibility(visible = receiveCharacteristic != null) {
                if (running) {
                    FloatingActionButton(onClick = { homeViewModel.saveECG() }) {
                        Icon(Icons.Outlined.Save, contentDescription = null)
                    }
                } else {
                    FloatingActionButton(onClick = { homeViewModel.receiveData() }) {
                        Icon(Icons.Outlined.GetApp, contentDescription = null)
                    }
                }
            }
            FloatingActionButton(
                modifier = Modifier.padding(bottom = 18.dp, top = 18.dp),
                onClick = {
                    if (!scanning) {
                        homeViewModel.startScan()
                    } else {
                        homeViewModel.stopScan()
                    }
                }) {
                Icon(
                    if (!scanning) Icons.Outlined.Search else Icons.Outlined.StopCircle,
                    contentDescription = null
                )
            }
        }

    })

}

@Composable
fun HomeContent(
    modifier: Modifier, homeViewModel: HomeViewModel
) {
    val uiState by homeViewModel.uiState.collectAsState()
    val devices = uiState.bluetoothState.devices
    val bleState = uiState.bluetoothState.bleState
    val connectedDevice = uiState.bluetoothState.connectedDevice
    val logs = uiState.logs
    val saving = uiState.bluetoothState.bleState == BLEState.Saving
    val data = uiState.bluetoothState.ecgData
    val running = bleState == BLEState.Fetching || bleState == BLEState.Stop || saving
    val title by animateIntAsState(
        if (running)
            R.string.ecg_data else R.string.bluetooth_devices,
        label = ""
    )

    val corner by animateDpAsState(
        if (devices.isEmpty()) 32.dp else 0.dp, label = ""
    )
    Column(
        modifier.padding(horizontal = 10.dp)
    ) {
        AnimatedVisibility(visible = bleState != BLEState.None || bleState != BLEState.Scanning) {
            connectedDevice?.let {
                ConnectedDevice(
                    it
                ) {
                    homeViewModel.disconnect()
                }
            }
        }

        Text(
            text = stringResource(id = title),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 6.dp),
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.labelLarge
        )
        AnimatedContent(
            targetState = running,
            label = ""
        ) { running ->
            if (running) EcgChart(data, saving) { bitmap -> homeViewModel.saveBitmap(bitmap) } else
                SearchingDevices(
                    corner,
                    bleState,
                    devices,
                    homeViewModel
                )
        }

        Logs(logs = logs)
    }
}

@Composable
private fun SearchingDevices(
    corner: Dp,
    scanState: BLEState,
    devices: List<Device>,
    homeViewModel: HomeViewModel,
) {
    Column {
        Row(
            modifier = Modifier
                .padding(horizontal = 6.dp)
                .clip(
                    RoundedCornerShape(32.dp, 32.dp, corner, corner)
                )
                .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f))
                .clickable { }
                .padding(top = 22.dp, bottom = 22.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    modifier = Modifier
                        .padding(start = 28.dp),
                    text = when (scanState) {
                        BLEState.Scanning -> stringResource(id = R.string.scanning)
                        BLEState.None -> stringResource(
                            id = R.string.empty_device
                        )

                        else -> {
                            devices.size.toString() + stringResource(id = R.string.scanned)
                        }
                    },
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                )
                Column {
                    AnimatedVisibility(visible = scanState == BLEState.Scanning) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = MaterialTheme.colorScheme.secondary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant,
                        )
                    }
                }
            }
        }
        Column {
            AnimatedVisibility(visible = devices.isNotEmpty()) {
                LazyColumn {
                    for ((index, device) in devices.withIndex()) {
                        item {
                            DeviceItem(
                                device = device, isEnd = index == devices.size - 1
                            ) {
                                homeViewModel.connect(it)
                            }
                        }
                    }

                }
            }
        }
    }
}

@Composable
private fun ColumnScope.Logs(logs: List<LogInfo>) {
    AnimatedVisibility(
        visible = logs.isNotEmpty(),
        modifier = Modifier.padding(vertical = 8.dp, horizontal = 6.dp),
    ) {
        Text(
            modifier = Modifier.padding(10.dp),
            text = "[${logs.last().times}] ${logs.last().msg}",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
        )
    }
}

@Composable
private fun ConnectedDevice(
    device: Device,
    disconnect: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(88.dp),
        color = Color.Unspecified,
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 6.dp)
                .clip(
                    RoundedCornerShape(32.dp)
                )
                .background(MaterialTheme.colorScheme.primaryContainer)
                .clickable { }
                .padding(16.dp, 20.dp),
        ) {
            Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Outlined.BluetoothConnected,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(end = 10.dp)
                )
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = device.name!!,
                        style = MaterialTheme.typography.titleLarge.copy(fontSize = 20.sp),
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        text = device.address,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    )
                }
            }
            Row {
                IconButton(
                    onClick = { disconnect() }, modifier = Modifier
                        .padding(horizontal = 8.dp)
                ) {
                    Icon(
                        Icons.Outlined.BluetoothDisabled,
                        contentDescription = null,
                    )
                }
            }
        }
    }
}

@Composable
private fun DeviceItem(device: Device, isEnd: Boolean, connectDevice: (Device) -> Unit) {
    Row(
        modifier = Modifier
            .padding(horizontal = 6.dp)
            .clip(
                if (isEnd) RoundedCornerShape(0.dp, 0.dp, 32.dp, 32.dp)
                else RoundedCornerShape(0.dp)
            )
            .clickable { }
            .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f))
            .padding(vertical = 15.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Outlined.Bluetooth,
                modifier = Modifier.padding(start = 28.dp),
                contentDescription = null
            )
            Column {
                Text(
                    text = device.name ?: "",
                    modifier = Modifier.padding(start = 12.dp, end = 6.dp),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = device.address,
                    modifier = Modifier.padding(start = 12.dp, end = 6.dp, top = 2.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

        }
        IconButton(
            onClick = { connectDevice(device) },
            modifier = Modifier
                .padding(end = 20.dp)
        ) {
            Icon(Icons.Outlined.Link, contentDescription = null)
        }
    }
}
