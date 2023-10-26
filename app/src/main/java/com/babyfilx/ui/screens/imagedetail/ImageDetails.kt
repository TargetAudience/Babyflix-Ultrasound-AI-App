package com.babyfilx.ui.screens.imagedetail

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerId
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.babyfilx.api.apistate.Response
import com.babyfilx.data.enums.BottomBarEnum
import com.babyfilx.ui.screens.home.HomeViewModel
import com.babyfilx.ui.screens.home.deleteCall
import com.babyfilx.ui.theme.dp1
import com.babyfilx.utils.commonviews.*
import com.babyfilx.utils.extentions.download
import com.babyfilx.utils.logs.loge
import com.babyfilx.utils.shareData
import com.babyfilx.utils.toast
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState

@OptIn(ExperimentalPagerApi::class)
@Composable
fun ImageDetails(navController: NavController, viewModel: HomeViewModel) {

    loge("Data of details ${viewModel.detailsModel}")
    val data = viewModel.detailsModel
    var image by remember {
        mutableStateOf(data.list[data.index].thumb_url)
    }
    var id by remember {
        mutableStateOf(data.list[data.index].node_id)
    }
    var imageIndex by remember {
        mutableStateOf(data.index)
    }

    var scale by remember {
        mutableStateOf(1f)
    }

    val pagerState = PagerState(
        pageCount = data.list.size,
        currentPage = imageIndex
    )
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            if (data.list.isNotEmpty()) {
                loge("PagerState $page")
                imageIndex = page
                data.index = page
                id = data.list[page].node_id
            }
        }
    }

    val deleteState = viewModel.deleteResponse.collectAsState(initial = null)
    val context = LocalContext.current
    Scaffold(modifier = Modifier.fillMaxSize(), backgroundColor = Color.White, topBar = {
        TopAppBars(
            title = data.title,
            isIcons = Icons.Filled.ArrowBack,
            isDone = false
        ) {
            navController.navigateUp()
        }
    }) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {


            Column(
                modifier = Modifier
                    .weight(0.83f)
                    .fillMaxSize()
            ) {


                Box(
                    modifier = Modifier
                        .weight(0.7f, fill = true)
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    if (data.list.size > 0)
                        HorizontalPager(
                            state = pagerState,
                        ) {
                            loge("imageIndex $it")

                            ImageUrlLoading(
                                url = data.list[it].download_url,
                                shape = RoundedCornerShape(0.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .fillMaxHeight())
                            /*      .pointerInput(Unit) {
                                      detectTransformGestures { _, _, zoom, _ ->
                                          scale *= zoom
                                      }

                                  }
                                  .graphicsLayer {
                                      scaleX = maxOf(.5f, minOf(3f,scale))
                                      scaleY = maxOf(.5f, minOf(3f,scale))
                                  }*//*,
                                contentScale = ContentScale.FillWidth
                            )*/
                        }
                }


                LazyHorizontalGrid(
                    modifier = Modifier
                        .weight(if (data.list.size > 20) 0.1f else 0.05f)
                        .fillMaxSize(),
                    rows = GridCells.Fixed(if (data.list.size > 20) 2 else 1),
                    content = {
                        itemsIndexed(data.list) { index, item ->
                            ImageUrlLoading(
                                url = item.thumb_url,
                                shape = RoundedCornerShape(0.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        imageIndex = index
                                        image = item.thumb_url
                                        data.index = index
                                        id = data.list[index].node_id
                                    }
                                    .padding(end = dp1, bottom = dp1),
                                height = 58.dp,
                                width = 34.dp
                            )
                        }
                    })

            }

            HomeBottomBar(modifier = Modifier.weight(1f), isShare = false) {
                when (it) {
                    BottomBarEnum.Download -> {
                        if (data.list.isNotEmpty())
                            context.download(data.list[imageIndex])
                        else
                            context.toast("Not have a item for Download.")
                    }
                    BottomBarEnum.Delete -> {
                        if (data.list.isNotEmpty())
                            viewModel.delete = true
                        else
                            context.toast("Not have a item for delete.")
                    }
                    BottomBarEnum.Upload -> {
                        if (data.list.isNotEmpty())
                            context.shareData(
                                "Check out my imagery shared from BabyFlix " + "https://www.babyflix.net/" + "m/" + Integer.toHexString(
                                    data.list[imageIndex].node_id.toInt()
                                )
                            )
                        else
                            context.toast("Not have a item for Share.")
                    }
                }
            }
        }
    }
    when (deleteState.value) {
        is Response.Loading -> ProgressBar()
        is Response.Success -> {}
        else -> {}
    }

    MessageAlert(name = viewModel.message) {
        viewModel.message = ""
        if (deleteState.value?.data?.code == 200) {
            data.list.removeAt(data.index)
            if (data.list.isNotEmpty()) {
                imageIndex = 0
                data.index = 0
                id = data.list[0].node_id
                image = data.list[0].thumb_url
            }
        }
    }
    deleteCall(viewModel = viewModel) {
        viewModel.delete = false
        viewModel.deleteAPiCall(id, 1)
    }


}