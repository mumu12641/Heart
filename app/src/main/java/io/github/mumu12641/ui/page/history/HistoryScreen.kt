package io.github.mumu12641.ui.page.history

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.CloudUpload
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.PlayCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
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
import com.pushpal.jetlime.ItemsList
import com.pushpal.jetlime.JetLimeColumn
import com.pushpal.jetlime.JetLimeEvent
import com.pushpal.jetlime.JetLimeEventDefaults
import com.skydoves.landscapist.glide.GlideImage
import io.github.mumu12641.R
import io.github.mumu12641.data.local.model.ECGModel
import io.github.mumu12641.util.FileUtil

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
                IconButton(onClick = { navController.popBackStack() }) {
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
    val ecgModels = uiState.ecgModels.reversed()
    val expandIndex = uiState.expandIndex
    val loadState = uiState.loadState
    JetLimeColumn(
        modifier = modifier.padding(horizontal = 10.dp),
        itemsList = ItemsList(ecgModels),
        key = { _, item -> item.id },
    ) { index, ecg, position ->
        JetLimeEvent(
            style = JetLimeEventDefaults.eventStyle(
                position = position,
            ),
        ) {
            ECGCard(
                ecg = ecg,
                loadState,
                expandIndex == index,
                { historyViewModel.setExpandIndex(index) },
                { historyViewModel.updateECG(it) }) {
                historyViewModel.deleteECG(it)
            }
        }
    }

}

@Composable
fun ECGCard(
    ecg: ECGModel,
    loadState: HistoryViewModel.LoadState,
    expand: Boolean,
    chooseIndex: () -> Unit,
    update: (ECGModel) -> Unit,
    delete: (ECGModel) -> Unit
) {
    val height by animateDpAsState(targetValue = if (expand) 380.dp else 125.dp, label = "")
    val corner by animateDpAsState(targetValue = if (expand) 32.dp else 16.dp, label = "")
    val imgHeight by animateDpAsState(targetValue = if (expand) 300.dp else 125.dp, label = "")

    Card(modifier = Modifier
        .padding(vertical = 5.dp)
        .fillMaxWidth()
        .height(height)
        .clip(RoundedCornerShape(corner)),
        onClick = {}
    ) {
        Column(verticalArrangement = Arrangement.Center) {
            GlideImage(
                imageModel = { ecg.jpgPath },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(imgHeight)
                    .clip(RoundedCornerShape(corner))
                    .clickable {
                        chooseIndex()
                        if (expand) FileUtil.openFile(ecg.jpgPath)
                    },
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
//                            text = FileUtil.getFileSize(ecg.wavPath).toString(),
                            color = MaterialTheme.colorScheme.secondary,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        AnimatedContent(targetState = loadState, label = "") {
                            if (it == HistoryViewModel.LoadState.Loading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = MaterialTheme.colorScheme.secondary,
                                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                                )
                            } else {
                                IconButton(onClick = { update(ecg) }) {
                                    Icon(
                                        Icons.Outlined.CloudUpload,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }

                    IconButton(onClick = { FileUtil.openFile(ecg.wavPath) }) {
                        Icon(
                            Icons.Outlined.PlayCircle,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
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