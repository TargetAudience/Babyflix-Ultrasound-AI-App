package com.babyfilx.ui.screens.deeplinking

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.babyfilx.ui.theme.sp12
import com.babyfilx.ui.theme.sp14
import com.babyfilx.ui.theme.sp9
import com.babyfilx.utils.commonviews.WebView
import com.babyfilx.utils.shareData
import com.babyflix.mobileapp.BuildConfig


@Composable
fun URlForBabyFilx(navController: NavController) {

    val viewModel: DeepLinkingViewModel = hiltViewModel()
    val url = viewModel.url.replace("$", "/")
    val context = LocalContext.current
    Scaffold(topBar = {
        TopAppBar(
            title = {
                Column {
                    Text(text = url, maxLines = 1, fontSize = sp14)
                    Text(
                        text = BuildConfig.BASE_URL.removePrefix("https://").removeSuffix("/"),
                        maxLines = 1,
                        fontSize = sp9
                    )

                }
            },
            navigationIcon = {
                IconButton(onClick = {
                    navController.navigateUp()
                }) {
                    Icon(Icons.Filled.Close, "")
                }
            },
            actions = {
                IconButton(onClick = {
                    context.shareData(url)
                }) {
                    Icon(Icons.Filled.Share, "")
                }
            },
            backgroundColor = Color.White,

            )
    }, backgroundColor = Color.White) {

        WebView(
            modifier = Modifier.fillMaxSize().padding(it),
            url = url
        )
    }
}