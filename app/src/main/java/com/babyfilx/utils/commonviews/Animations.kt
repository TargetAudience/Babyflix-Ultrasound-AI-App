package com.babyfilx.utils.commonviews

import androidx.compose.animation.*
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp


@Composable
fun SplashLogoAnimation(onClick: @Composable () -> Unit) {

    val visible by remember { mutableStateOf(true) }
    AnimatedVisibility(
        visible = visible,
        enter = slideInHorizontally(
            initialOffsetX = { it }, // it == fullWidth
            animationSpec = tween(
                durationMillis = 1000,
                easing = LinearEasing
            )
        ),
        exit = slideOutHorizontally(
            targetOffsetX = { it },
            animationSpec = tween(
                durationMillis = 1000,
                easing = LinearEasing
            )
        )
    ) {
        onClick()
    }


}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun LoginVertical(onClick: @Composable () -> Unit) {
    val visible by remember { mutableStateOf(true) }
    AnimatedContent(
        targetState = visible,
        transitionSpec = {
            fadeIn() + slideInVertically(animationSpec = tween(400),
                initialOffsetY = { fullHeight -> fullHeight }) with
                    fadeOut(animationSpec = tween(200))
        }
    ) { targetState ->
        onClick()
    }
}


@OptIn(ExperimentalAnimationApi::class)
@Composable
fun LikeAndUnlike(isLive: Boolean, onTransition: @Composable () -> Unit) {
    AnimatedContent(targetState = isLive,
        transitionSpec = {
            if (targetState > initialState) {
                slideInVertically { fullHeight -> fullHeight } + fadeIn() with
                        slideOutVertically { fullHeight -> -fullHeight } + fadeOut()
            } else {
                slideInVertically { fullHeight -> -fullHeight } + fadeIn() with
                        slideOutVertically { fullHeight -> fullHeight } + fadeOut()
            }
        }
    ) {
        onTransition()
    }
}