package io.github.mumu12641.ui.page.home

import android.Manifest
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import io.github.mumu12641.ui.page.welcome.WelcomeScreen
import io.github.mumu12641.util.Route


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MainScreen(homeViewModel: HomeViewModel) {
    val navController = rememberNavController()
    val state =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            rememberMultiplePermissionsState(
                permissions = listOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.BLUETOOTH_ADVERTISE,
                    Manifest.permission.BLUETOOTH_CONNECT
                )
            )
        } else {
            rememberMultiplePermissionsState(
                permissions = listOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                )
            )
        }


    NavHost(
        navController = navController,
        startDestination = if (!state.allPermissionsGranted) Route.WELCOME else Route.HOME
    ) {
        composable(Route.WELCOME) {
            WelcomeScreen(navController) {
                state.launchMultiplePermissionRequest()
            }
        }
        composable(Route.HOME) {
            HomeScreen(homeViewModel = homeViewModel)
        }
    }
}
