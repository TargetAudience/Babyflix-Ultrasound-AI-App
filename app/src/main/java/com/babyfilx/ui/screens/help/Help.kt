package com.babyfilx.ui.screens.help

import android.location.Location
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.babyfilx.utils.commonviews.TopAppBars
import com.babyfilx.utils.commonviews.WebView


@Composable
fun Help(navController: NavController) {
    val viewModel: HelpViewModel = hiltViewModel()

    Scaffold(modifier = Modifier.fillMaxSize(), backgroundColor = Color.White, topBar = {
        TopAppBars(
            modifier = Modifier,
            title = if (!viewModel.isHelp) "Help" else "Terms & Conditions",
            isIcons = Icons.Filled.ArrowBack,
            isDone = false
        ) {
            navController.navigateUp()
        }
    }) {
        WebView(
            modifier = Modifier.padding(it),
            url = if (!viewModel.isHelp) "https://babyflix.zendesk.com/hc/en-us" else "https://babyflix.net/home/dt_procedure/termsandcondition/"
        )
    }

}