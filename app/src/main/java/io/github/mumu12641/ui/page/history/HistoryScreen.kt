package io.github.mumu12641.ui.page.history

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.skydoves.landscapist.glide.GlideImage
import io.github.mumu12641.R
import io.github.mumu12641.data.local.model.ECGModel
import io.github.mumu12641.util.Route

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    navController: NavController, historyViewModel: HistoryViewModel = hiltViewModel()
) {
    val scrollBehavior =
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState(),
            canScroll = { true })
    Scaffold(modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection), topBar = {
        LargeTopAppBar(
            title = { Text(text = stringResource(id = R.string.history)) },
            scrollBehavior = scrollBehavior,
            colors = TopAppBarDefaults.largeTopAppBarColors(
                scrolledContainerColor = MaterialTheme.colorScheme.background
            ),
            navigationIcon = {
                IconButton(onClick = { navController.navigate(Route.HOME) }) {
                    Icon(
                        Icons.AutoMirrored.Outlined.ArrowBack,
                        contentDescription = null,
                    )
                }

            },
        )
    }) {
        HistoryContent(
            modifier = Modifier.padding(it), historyViewModel
        )
    }
}

@Composable
fun HistoryContent(modifier: Modifier, historyViewModel: HistoryViewModel) {
    val uiState by historyViewModel.uiState.collectAsState()
    val ecgModels = uiState.ecgModels
    val expandIndex = uiState.expandIndex
    LazyColumn(modifier = modifier) {
        for ((index, ecg) in ecgModels.withIndex()) {
            item {
                ECGCard(
                    ecg = ecg,
                    expandIndex == index,
                    { historyViewModel.setExpandIndex(index) }) {
                    historyViewModel.deleteECG(it)
                }
            }
        }
    }
}

@Composable
fun ECGCard(ecg: ECGModel, expand: Boolean, chooseIndex: () -> Unit, delete: (ECGModel) -> Unit) {
    val height by animateDpAsState(targetValue = if (expand) 260.dp else 125.dp, label = "")
    val corner by animateDpAsState(targetValue = if (expand) 32.dp else 16.dp, label = "")
    val imgHeight by animateDpAsState(targetValue = if (expand) 185.dp else 125.dp, label = "")
    Surface(modifier = Modifier
        .padding(horizontal = 10.dp, vertical = 5.dp)
        .fillMaxWidth()
        .height(height)
        .clip(RoundedCornerShape(corner))
        .background(MaterialTheme.colorScheme.secondary)
        .clickable { chooseIndex() }
    ) {
        Column(verticalArrangement = Arrangement.Center) {
            GlideImage(
                imageModel = { ecg.path }, modifier = Modifier
                    .fillMaxWidth()
                    .height(imgHeight)
                    .clip(RoundedCornerShape(corner))
            )
            AnimatedVisibility(visible = expand) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .fillMaxHeight()
                            .weight(1f)
                    ) {
                        Text(
                            text = ecg.time,
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.labelLarge,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        Text(
                            text = ecg.des ?: stringResource(id = R.string.no_des),
                            color = MaterialTheme.colorScheme.secondary,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                    IconButton(onClick = { delete(ecg) }) {
                        Icon(
                            Icons.Outlined.Delete,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }

            }
        }

    }
}