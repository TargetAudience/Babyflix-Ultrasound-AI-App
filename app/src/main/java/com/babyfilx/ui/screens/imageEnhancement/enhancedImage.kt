package com.babyfilx.ui.screens.imageEnhancement

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import androidx.navigation.NavController
import com.babyfilx.api.apistate.Response
import com.babyfilx.data.enums.BottomBarEnum
import com.babyfilx.data.models.Screens
import com.babyfilx.ui.screens.home.HomeViewModel
import com.babyfilx.ui.theme.*
import com.babyfilx.utils.commonviews.*
import com.babyfilx.utils.extentions.download
import com.babyfilx.utils.shareData
import com.babyfilx.utils.toast
import com.babyflix.mobileapp.R


@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun EnhancedImageScreen(navController: NavController, viewModel: HomeViewModel) {
    var scale by remember { mutableStateOf(1f) }
    var rotation by remember { mutableStateOf(0f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    val minScale = 1f
    val maxScale = 3f

    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp

    var lastTapTimestamp by remember { mutableStateOf(0L) }

    val doubleTapScaleTarget = if (scale == minScale) maxScale else minScale

    Scaffold(modifier = Modifier.fillMaxSize(), backgroundColor = Color.White, topBar = {
        TopAppBars(
            title = "Enhanced Image",
            isIcons = Icons.Filled.ArrowBack,
            isDone = false
        ) {
            navController.navigateUp()
        }
    })  {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .pointerInput(Unit) {
                        detectTransformGestures { _, pan, zoom, rotationDelta ->
                            // Handle pinch-to-zoom gesture
                            scale *= zoom
                            scale = scale.coerceIn(minScale, maxScale)

                            // Handle rotation gesture
                            rotation += rotationDelta

                            // Calculate the restricted translation bounds
                            val maxWidthOffset = (screenWidth.toPx() / 2) * (scale - 1)
                            val maxHeightOffset = (screenHeight.toPx() / 2) * (scale - 1)

                            // Handle panning gesture with restricted bounds
                            offset += pan
                            offset = Offset(
                                x = offset.x.coerceIn(-maxWidthOffset, maxWidthOffset),
                                y = offset.y.coerceIn(-maxHeightOffset, maxHeightOffset)
                            )
                        }
                    }
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onDoubleTap = {
                                val currentTimestamp = System.currentTimeMillis()
                                val elapsed = currentTimestamp - lastTapTimestamp
                                lastTapTimestamp = currentTimestamp

                                if (elapsed < 1200L) {
                                    if (scale != doubleTapScaleTarget) {
                                        // Scale to the target scale value
                                        scale = doubleTapScaleTarget
                                    } else {
                                        // Reset scale and position
                                        scale = minScale
                                        offset = Offset.Zero
                                    }
                                }
                            }
                        )
                    }
                    .graphicsLayer(
                        scaleX = scale,
                        scaleY = scale,
                        translationX = offset.x,
                        translationY = offset.y
                    )
            ) {
                Image(
                    painter = painterResource(id = R.drawable.cuate),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            }
            EnhancedBottomBar(modifier = Modifier.weight(1f), isShare = false) {
                when (it) {
                    BottomBarEnum.Download -> {
                    }

                    BottomBarEnum.Upload -> {
                    }
                    else -> {}
                }
            }
        }
    }
}




@Composable
fun EnhancedBottomBar(
    modifier: Modifier = Modifier,
    isIcon: Boolean = true,
    isShare: Boolean = true,
    onClick: (BottomBarEnum) -> Unit
) {
    Card(
        backgroundColor = Color.White, modifier = Modifier
            .fillMaxWidth()
            .height(dp60), elevation = dp15
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = if (isIcon) 0.dp else 30.dp)
                .height(dp60),
            horizontalArrangement = if (isIcon) Arrangement.SpaceAround else Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,

            ) {
            IconButton(onClick = {
                onClick(BottomBarEnum.Upload)
            }) {
                Image(
                    painter = if (isShare) painterResource(id = R.drawable.upload) else painterResource(
                        id = R.drawable.share
                    ), contentDescription = null
                )
            }
            if (isIcon)
                IconButton(onClick = {
                    onClick(BottomBarEnum.Download)
                }) {
                    Box(contentAlignment = Alignment.Center) {
                        Image(
                            painter = painterResource(id = R.drawable.download),
                            contentDescription = null
                        )
                        Image(
                            modifier = Modifier.padding(top = dp10),
                            painter = painterResource(id = R.drawable.download1),
                            contentDescription = null
                        )
                    }
                }
        }
    }
}