package io.github.mumu12641.ui.page.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import io.github.mumu12641.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController, profileViewModel: ProfileViewModel = hiltViewModel()
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState(),
        canScroll = { true })
    Scaffold(modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection), topBar = {
        LargeTopAppBar(
            title = { Text(text = stringResource(id = R.string.profile)) },
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
            scrollBehavior = scrollBehavior,
        )
    }, floatingActionButton = {
        FloatingActionButton(
            onClick = { /*TODO*/ },
            modifier = Modifier.padding(bottom = 18.dp),
        ) {
            Icon(Icons.Outlined.Save, contentDescription = null)
        }
    }) {
        ProfileContent(modifier = Modifier.padding(it))
    }

}


@Composable
fun ProfileContent(modifier: Modifier) {


    LazyColumn(
        modifier.padding(horizontal = 10.dp),

        ) {
        item {
            PersonalInfoCard()
        }
        item {
            OwnDiseaseCard()

        }
        item {
            GeneticDiseaseCard()
        }
    }

}

@Composable
private fun OwnDiseaseCard() {
    Card(
        onClick = { },
        modifier = Modifier
            .clip(RoundedCornerShape(24.dp))
            .padding(bottom = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(
                alpha = 0.5f
            )
        )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(10.dp)
        ) {
            Row {
                Text(
                    color = MaterialTheme.colorScheme.primary,
                    text = stringResource(id = R.string.own_disease),
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.weight(1f))
            }
            Row {
                FilterChip(selected = false, modifier = Modifier.padding(horizontal = 4.dp),
                    onClick = { /*TODO*/ },
                    label = { Text(text = "高脂血症") })
                FilterChip(
                    modifier = Modifier.padding(horizontal = 4.dp),
                    selected = false,
                    onClick = { /*TODO*/ },
                    label = { Text(text = "糖尿病") })

                FilterChip(selected = false, modifier = Modifier.padding(horizontal = 4.dp),
                    onClick = { /*TODO*/ },
                    label = { Text(text = "高血压") })

            }
            Row {
                FilterChip(selected = false, modifier = Modifier.padding(horizontal = 4.dp),
                    onClick = { /*TODO*/ },
                    label = { Text(text = "房颤心脏瓣膜病") })
                FilterChip(selected = false, modifier = Modifier.padding(horizontal = 4.dp),
                    onClick = { /*TODO*/ },
                    label = { Text(text = "冠心病发生急性冠脉综合征") })
            }
        }
    }
}

@Composable
private fun GeneticDiseaseCard() {
    Card(
        onClick = { },
        modifier = Modifier
            .clip(RoundedCornerShape(24.dp))
            .padding(bottom = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(
                alpha = 0.5f
            )
        )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(10.dp)
        ) {
            Row {
                Text(
                    color = MaterialTheme.colorScheme.primary,
                    text = stringResource(id = R.string.genetic_disease),
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.weight(1f))
            }
            Row {
                FilterChip(selected = false, modifier = Modifier.padding(horizontal = 4.dp),
                    onClick = { /*TODO*/ },
                    label = { Text(text = "冠心病") })
                FilterChip(selected = false, modifier = Modifier.padding(horizontal = 4.dp),
                    onClick = { /*TODO*/ },
                    label = { Text(text = "急性心梗") })
                FilterChip(selected = false, modifier = Modifier.padding(horizontal = 4.dp),
                    onClick = { /*TODO*/ },
                    label = { Text(text = "脑猝") })
            }
        }
    }
}


@Composable
private fun PersonalInfoCard() {
    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(
            R.raw.profile
        )
    )
    Card(
        onClick = { },
        modifier = Modifier
            .clip(RoundedCornerShape(24.dp))
            .padding(bottom = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(
                alpha = 0.5f
            )
        )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(10.dp)
        ) {
            Row {
                Text(
                    color = MaterialTheme.colorScheme.primary,
                    text = stringResource(id = R.string.personal_info),
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.weight(1f))
            }
            LottieAnimation(
                modifier = Modifier.size(150.dp),
                iterations = LottieConstants.IterateForever,
                composition = composition,
            )
            Row {
                OutlinedTextField(
                    value = "",
                    onValueChange = {},
                    label = { Text(text = stringResource(id = R.string.name)) },
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 10.dp)
                )
                OutlinedTextField(
                    value = "",
                    onValueChange = {},
                    modifier = Modifier.weight(1f),
                    label = { Text(text = stringResource(id = R.string.age)) },
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = "",
                    onValueChange = {},
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 10.dp),
                    label = { Text(text = stringResource(id = R.string.bmi)) },
                )
                SuggestionChip(onClick = { /*TODO*/ }, label = { Text(text = "BMI健康") })
            }
            Row {
                OutlinedTextField(
                    value = "",
                    onValueChange = {},
                    label = { Text(text = stringResource(id = R.string.smoking_age)) },
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 10.dp)
                )
                OutlinedTextField(
                    value = "",
                    onValueChange = {},
                    modifier = Modifier.weight(1f),
                    label = { Text(text = stringResource(id = R.string.smoking_frequency)) },
                )
            }
        }

    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PrevProfile() {
    val navController = rememberNavController()
    ProfileScreen(navController)
}