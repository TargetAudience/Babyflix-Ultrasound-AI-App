package com.babyfilx.ui.screens.player

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavController
import com.babyfilx.data.models.Screens
import com.babyfilx.ui.screens.home.HomeViewModel
import com.babyfilx.ui.theme.dp16
import com.babyfilx.ui.theme.dp20
import com.babyfilx.utils.commonviews.ComposableLifecycle
import com.babyfilx.utils.commonviews.ShareAndCopy
import com.babyfilx.utils.commonviews.TopAppBars
import com.babyfilx.utils.commonviews.VideoDetailList
import com.babyfilx.utils.logs.loge
import com.babyfilx.utils.shareData
import com.babyfilx.utils.toast
import com.babyflix.mobileapp.R
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.StyledPlayerView


@Composable
fun VideoPLayerScreen(navController: NavController, viewModel: HomeViewModel) {

    val context = LocalContext.current
    val mediaItem = MediaItem.fromUri(viewModel.detailsModel.url)
    val clipboard: ClipboardManager =
        context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val playerView = StyledPlayerView(context)
    val player = provideExoPlayer(context = context, mediaItem = mediaItem)
    playerView.player = player

    var id by remember {
        mutableStateOf(viewModel.detailsModel.nodeId)
    }



    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBars(title = "", isDone = false, isIcons = Icons.Filled.ArrowBack) {
                navController.navigateUp()
            }
        },
        backgroundColor = Color.White
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            VideoPlayer(playerView = playerView, player = player)


            share(context = context, viewModel = viewModel, clipboard)

            VideoDetailList(l = viewModel.detailsModel.list, viewModel.detailsModel.url) {
                loge("videoDetails  ${it.download_url}")
                viewModel.detailsModel.url = it.download_url
                viewModel.detailsModel.nodeId = it.node_id
                player.releasePlayer()
                navController.navigate(Screens.VideoDetails.root){
                    popUpTo(Screens.Home.root) {
                        inclusive = false
                    }
                }
                id = it.node_id
            }
        }
    }

    player.LifeCycle()
}


@Composable
fun VideoPlayer(playerView: StyledPlayerView, player: ExoPlayer) {
  DisposableEffect(AndroidView(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.3f),
        factory = {
            playerView
        }
    )) {
        player.prepare()
        player.playWhenReady = true
        onDispose {
            player.release()
        }
    }


}


@Composable
fun share(context: Context, viewModel: HomeViewModel, clipboard: ClipboardManager) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = dp16, vertical = dp20)
    ) {
        ShareAndCopy {
            clipboard.setPrimaryClip(ClipData.newPlainText("", viewModel.detailsModel.url))
            context.toast("Copied")

        }
        ShareAndCopy(
            R.drawable.video_share,
            stringResource(id = R.string.share),
            modifier = Modifier.padding(start = dp20)
        ) {
            val message =
                "Check out my imagery shared from BabyFlix \n\n\n https://www.babyflix.net/" + "m/" + Integer.toHexString(
                    Integer.parseInt(viewModel.detailsModel.nodeId)
                )
            context.shareData(message)
        }
    }
}

@Composable
fun ExoPlayer.LifeCycle() {
    ComposableLifecycle { source, event ->
        when (event) {
            Lifecycle.Event.ON_STOP, Lifecycle.Event.ON_PAUSE -> {
                pause()
            }
            Lifecycle.Event.ON_DESTROY -> {
                releasePlayer()
            }
            else -> {}
        }
    }


}

fun provideExoPlayer(context: Context, mediaItem: MediaItem): ExoPlayer {

    val trackSelector = DefaultTrackSelector(context).apply {
        setParameters(buildUponParameters().setMaxVideoSizeSd())
    }
    val player = ExoPlayer.Builder(context)
        .setTrackSelector(trackSelector)
        .setSeekBackIncrementMs(10000)
        .setSeekForwardIncrementMs(10000)
        .build().also { exoPlayer ->
            exoPlayer.setMediaItem(mediaItem)
            //binding.player.player = exoPlayer

        }
    return player
}

fun ExoPlayer.releasePlayer() {
    let { exoPlayer ->
        // exoPlayer.removeListener(playbackStateListener)
        exoPlayer.pause()
        exoPlayer.stop()
        exoPlayer.release()
    }
}