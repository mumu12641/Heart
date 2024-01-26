package io.github.mumu12641.ui.page.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Bluetooth
import androidx.compose.material.icons.outlined.BluetoothConnected
import androidx.compose.material.icons.outlined.ExpandLess
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Badge
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.mumu12641.R
import io.github.mumu12641.service.Device
import io.github.mumu12641.service.SearchState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(homeViewModel: HomeViewModel) {
    Scaffold(topBar = {
        LargeTopAppBar(modifier = Modifier.padding(horizontal = 8.dp), title = {
            Text(text = stringResource(id = R.string.app_name))
        }, navigationIcon = {
            Icon(Icons.Outlined.Home, contentDescription = null)
        }, actions = {
            Icon(Icons.Outlined.Settings, contentDescription = null)

        })
    }, content = { paddingValues ->
        HomeContent(homeViewModel, modifier = Modifier.padding(paddingValues))
    }, floatingActionButton = {
        FloatingActionButton(modifier = Modifier.padding(bottom = 18.dp), onClick = {
            homeViewModel.check()
        }) {
            Icon(Icons.Outlined.Search, contentDescription = null)
        }
    })

}

@Composable
fun HomeContent(homeViewModel: HomeViewModel, modifier: Modifier) {
    val uiState by homeViewModel.uiState.collectAsState()
    val isExpanded = uiState.isExpanded
    val bluetoothState by homeViewModel.bluetoothState.collectAsState()
    val devices  = bluetoothState.devices
    val searchState = bluetoothState.searchState

    val corner by animateDpAsState(
        if (!isExpanded || devices.isEmpty()) 32.dp else 0.dp,
        label = ""
    )
    Column(
        modifier.padding(horizontal = 10.dp)
    ) {
        ConnectedDevice()

        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "bluetooth devices",
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 6.dp),
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.labelLarge
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .padding(horizontal = 6.dp)
                .clip(
                    RoundedCornerShape(32.dp, 32.dp, corner, corner)
                )
                .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f))
                .padding(top = 22.dp, bottom = 22.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                modifier = Modifier
                    .weight(1f)
            ) {
                Text(
                    modifier = Modifier
                        .padding(start = 28.dp),
                    text = "Searching",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                )
                Column {
                    AnimatedVisibility(visible = searchState == SearchState.Searching) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = MaterialTheme.colorScheme.secondary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant,
                        )

                    }
                }
            }

            Row(
                modifier = Modifier
                    .padding(end = 20.dp)
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceTint.copy(alpha = 0.2f))
                    .clickable {
                        homeViewModel.flipExpanded()
                    },
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = if (!isExpanded) Icons.Outlined.ExpandMore else Icons.Outlined.ExpandLess,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                )
            }
        }
        Column {
            AnimatedVisibility(visible = isExpanded) {
                LazyColumn {
                    for ((index, device) in devices.withIndex()) {
                        item {
                            DeviceItem(
                                device = device,
                                isEnd = index == devices.size - 1
                            )
                        }
                    }

                }
            }
        }

    }

}

@Composable
private fun ConnectedDevice() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(88.dp),
        color = Color.Unspecified,
    ) {
        Row(modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 6.dp)
            .clip(
                RoundedCornerShape(32.dp)
            )
            .background(MaterialTheme.colorScheme.primaryContainer)
            .clickable {}
            .padding(16.dp, 20.dp), verticalAlignment = Alignment.CenterVertically) {
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
                    text = "BLE-02",
                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 20.sp),
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = "connected",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeviceItem(device: Device, isEnd: Boolean) {
    Row(
        modifier = Modifier
            .padding(horizontal = 6.dp)
            .clip(
                if (isEnd) RoundedCornerShape(0.dp, 0.dp, 32.dp, 32.dp)
                else RoundedCornerShape(0.dp)

            )
            .clickable { }
            .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f))
            .padding(bottom = 22.dp, top = 22.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Outlined.Bluetooth,
                modifier = Modifier.padding(start = 28.dp),
                contentDescription = null
            )
            Text(
                text = device.name, modifier = Modifier.padding(start = 12.dp, end = 6.dp),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Badge(
            modifier = Modifier.padding(end = 20.dp),
            containerColor = MaterialTheme.colorScheme.surfaceTint.copy(alpha = 0.2f),
            contentColor = MaterialTheme.colorScheme.outline,
            content = {
                Text(
                    text = "90",
                    style = MaterialTheme.typography.labelSmall,
                )
            },
        )
    }
}
