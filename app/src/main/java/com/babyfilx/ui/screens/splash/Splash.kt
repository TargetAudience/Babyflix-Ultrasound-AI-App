package com.babyfilx.ui.screens.splash

import android.annotation.SuppressLint
import android.app.Activity
import android.net.Uri
import android.os.Build.VERSION.SDK_INT
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import androidx.navigation.NavController
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import com.babyfilx.base.App
import com.babyfilx.data.models.Screens
import com.babyfilx.ui.theme.*
import com.babyfilx.utils.commonviews.DrawableImage
import com.babyfilx.utils.commonviews.SplashLogoAnimation
import com.babyfilx.utils.findActivity
import com.babyfilx.utils.logs.loge
import com.babyfilx.utils.removeScreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun SplashScreen(navController: NavController) {

    val scope = rememberCoroutineScope()
    var isAnim by remember {
        mutableStateOf(false)
    }
    val context = LocalContext.current
    var appLinkData: Uri? = null
    LaunchedEffect(key1 = Unit) {
        val appLinkIntent = context.findActivity()?.intent
        val appLinkAction = appLinkIntent?.action
        appLinkData = appLinkIntent?.data
        loge("DeepLonking ${appLinkData?.path}")
    }



    Surface(
        modifier = Modifier.fillMaxSize(),
        color = white
    ) {
        ImageExample()
    }


    scope.launch {
        (1..3).forEach {
            delay(1500)
            if (it == 2)
                isAnim = true
        }
        if (appLinkData != null) {
            //navController.removeScreen(Screens.Reset.root)
            //navController.navigate(Screens.Reset.root)
            navController.navigate(
                "${Screens.DeepLinking.root}/${
                    appLinkData.toString().replace("/", "$")
                }"
            )

        } else if (App.data.isLogin) {
            navController.navigate(Screens.BottomBar.root)
        } else {
            navController.navigate(Screens.OnBoard.root)
        }
    }

}

@Composable
fun ImageExample() {
    val imageLoader = ImageLoader.Builder(LocalContext.current)
        .components {
            if (SDK_INT >= 28) {
                add(ImageDecoderDecoder.Factory())
            } else {
                add(GifDecoder.Factory())
            }
        }
        .build()

    Box(
        modifier = Modifier.fillMaxSize().wrapContentSize(Alignment.Center)
    ) {
        Image(
            painter = rememberAsyncImagePainter(com.babyflix.mobileapp.R.drawable.splash_logo_gif, imageLoader),
            contentDescription = null,
            modifier = Modifier.size(width = dp223, height = dp81) // Fill the entire Box with the image
        )
    }
}
