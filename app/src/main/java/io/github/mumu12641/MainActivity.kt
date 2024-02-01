package io.github.mumu12641

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import dagger.hilt.android.AndroidEntryPoint
import io.github.mumu12641.ui.page.home.HomeViewModel
import io.github.mumu12641.ui.page.home.MainScreen
import io.github.mumu12641.ui.theme.HeartTheme
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val homeViewModel: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.plant(Timber.DebugTree())
        setContent {
            Surface(
                color = MaterialTheme.colorScheme.background,
                modifier = Modifier.fillMaxSize()
            ) {
                HeartTheme {
                    MainScreen(homeViewModel = homeViewModel)
                }
            }
        }
    }

//    private val receiver = object : BroadcastReceiver() {
//        @SuppressLint("MissingPermission")
//        override fun onReceive(context: Context, intent: Intent) {
//            when (intent.action) {
//                BluetoothDevice.ACTION_FOUND -> {
//                    // Discovery has found a device. Get the BluetoothDevice
//                    // object and its info from the Intent.
//                    val device = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//                        intent.getParcelableExtra(
//                            BluetoothDevice.EXTRA_DEVICE,
//                            BluetoothDevice::class.java
//                        )
//                    } else {
//                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
//                    }
//                    val deviceName = device?.name
//                    val deviceHardwareAddress = device?.address // MAC address
//                    homeViewModel.foundNewDevice(deviceName, deviceHardwareAddress)
//                    Log.d("MainActivity", "onReceive: $deviceName, $deviceHardwareAddress")
//                }
//            }
//        }
//    }

}
