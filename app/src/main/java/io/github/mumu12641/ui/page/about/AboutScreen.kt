package io.github.mumu12641.ui.page.about

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.skydoves.landscapist.glide.GlideImage
import io.github.mumu12641.MainActivity.Companion.context
import io.github.mumu12641.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(navController: NavController) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState(),
        canScroll = { true })
    Scaffold(modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection), topBar = {
        LargeTopAppBar(
            title = { Text(text = stringResource(id = R.string.about)) },
            colors = TopAppBarDefaults.largeTopAppBarColors(
                scrolledContainerColor = MaterialTheme.colorScheme.background
            ),
            navigationIcon = {
                IconButton(onClick = {
                    navController.popBackStack()
                }) {
                    Icon(
                        Icons.AutoMirrored.Outlined.ArrowBack,
                        contentDescription = null,
                    )
                }

            },
            scrollBehavior = scrollBehavior,
        )
    }
    ) {
        AboutContent(Modifier.padding(it))
    }
}

@Composable
fun AboutContent(modifier: Modifier) {
    LazyColumn(
        modifier
            .padding(horizontal = 10.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center
    ) {
        item {
            GlideImage(
                imageModel = { R.mipmap.ic_launcher }, modifier = Modifier
                    .size(100.dp)
                    .clip(
                        RoundedCornerShape(30.dp)
                    )

            )
        }
        item {
            Text(
                text = "心音信号采集与处理系统",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(top = 10.dp)
            )
        }
        item {
            ElevatedCard(
                modifier = Modifier
                    .width(300.dp)
                    .padding(top = 20.dp)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(
                        text = stringResource(id = R.string.intro),
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(text = stringResource(id = R.string.introduction))
                }
            }
        }
        item {
            ElevatedCard(
                modifier = Modifier
                    .width(300.dp)
                    .padding(top = 10.dp)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(
                        text = stringResource(id = R.string.version),
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = String.format(
                            "2024/5/3 version %s", context.packageManager.getPackageInfo(
                                context.packageName,
                                0
                            ).versionName
                        )
                    )
                }
            }
        }
        item {
            ElevatedCard(
                modifier = Modifier
                    .width(300.dp)
                    .padding(
                        top = 10.dp,
                        bottom = 2.dp
                    )
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(
                        text = stringResource(id = R.string.copyright),
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(text = "Copyright © 2024 SCHOOL OF ARTIFICIAL INTELLIGENCE AND AUTOMATION.HUST")
                }
            }
        }
    }
}
