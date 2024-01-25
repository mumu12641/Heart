package io.github.mumu12641.ui.page.welcome

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircleOutline
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import io.github.mumu12641.R
import io.github.mumu12641.ui.svg.SvgImage
import io.github.mumu12641.ui.svg.svgimage.Welcome
import io.github.mumu12641.util.Route


@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun WelcomeScreen(navController: NavController,request:()->Unit) {
//    val permissionState =
//        rememberPermissionState(permission = Manifest.permission.ACCESS_BACKGROUND_LOCATION)
    Scaffold(topBar = { LargeTopAppBar(title = { Text(text = stringResource(id = R.string.welcome)) }) },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                modifier = Modifier.padding(bottom = 30.dp),
                text = { Text(text = stringResource(id = R.string.agree_continue)) },
                icon = { Icon(Icons.Outlined.CheckCircleOutline, contentDescription = null) },
                onClick = { request() })
        }
    ) {
        LazyColumn(modifier = Modifier.padding(it))
        {
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Image(
                        modifier = Modifier
                            .weight(1f)
                            .size(300.dp),
                        imageVector = SvgImage.Welcome,
                        contentDescription = "",
                    )
                }
            }
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Info,
                        contentDescription = "",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = stringResource(id = R.string.welcome_tip),
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Light),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

