package com.babyfilx.utils.commonviews

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.babyfilx.data.enums.BottomSheetEnum
import com.babyfilx.data.enums.HomeEnum
import com.babyfilx.data.models.DetailsModel
import com.babyfilx.data.models.Screens
import com.babyfilx.data.models.response.HomeEntriesModel
import com.babyfilx.ui.screens.home.HomeViewModel
import com.babyfilx.ui.screens.player.VideoPlayerActivity
import com.babyfilx.ui.theme.*
import com.babyflix.mobileapp.R


@Composable
fun HomeContent(
    map: Map<String, List<HomeEntriesModel>>,
    homeEnum: HomeEnum,
    viewModel: HomeViewModel,
    navController: NavController,
    context: Context,

    ) {

    var d by remember {
        mutableStateOf(false)
    }
    if (d) {
        Divider()
    }

//    Box(modifier = Modifier.fillMaxWidth().height(144.dp)) {
//        AndroidView(
//            factory = { context ->
//                android.webkit.WebView(context).apply {
//                    settings.javaScriptEnabled = true
//                    loadUrl("https://servedby.aqua-adserver.com/afr.php?zoneid=10180")
//                }
//            },
//            update = { view ->
//                // update view if needed
//            },
//            modifier = Modifier.fillMaxSize()
//        )
//    }

//    AquaAdGallery()

    val isDataEmpty = viewModel.all.isEmpty()
    if(isDataEmpty && !viewModel.isLoading.value) {
      //  viewModel.refreshing = true
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "You don't have any data yet",
                color = Color.Black,
                textAlign = TextAlign.Center,
                fontSize = sp20
            )
        }
    } else {
        LazyVerticalGrid(modifier = Modifier.fillMaxSize(), columns = GridCells.Fixed(4)) {
            map.onEachIndexed { index, entry ->
                val initial = entry.key
                val list = entry.value
//            if(index == 0){
//                item(
//                    span = { GridItemSpan(4) }
//                ) { WebView1(url = "https://servedby.aqua-adserver.com/afr.php?zoneid=10179") }
//            }
                item(
                    span = { GridItemSpan(4) }
                ) {
                    HeaderRow(initial) {
                        d = !d
                        viewModel.selectAll(initial, homeEnum, d)
                    }
                }
                itemsIndexed(list) { it, item ->
                    if (it < 8)
                        ImageWithPlayAndSelect(item) { s, click ->
                            if (click == 1) {
                                viewModel.detailsModel = DetailsModel(
                                    title = initial, index = it,
                                    list = list as MutableList<HomeEntriesModel>,
                                    url = item.download_url,
                                    nodeId = item.node_id,
                                )
                                if (item.mediaType == "Video") {
                                    viewModel.videoList()
                                    val intent = Intent(context, VideoPlayerActivity::class.java)
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    intent.putExtra("data", viewModel.detailsModel)
                                    intent.putExtra("url", item.download_url)
                                    intent.putExtra("nodeId", item.node_id)
                                    context.startActivity(intent)

                                    /*navController.navigate(Screens.VideoDetails.root) {
                                    popUpTo(Screens.Home.root) {
                                        inclusive = false
                                    }
                                }*/
                                } else
                                    navController.navigate(Screens.Details.root)
                            } else {
                                d = !d
                                viewModel.selectParticularItem(it, initial, homeEnum, item.isSelect)
                            }
                        }
                    if (list.size > 7 && it == 7) {
                        Box(
                            modifier = Modifier
                                .size(dp92, dp92)
                                .background(black_tranparent)
                                .clickable {
                                    viewModel.detailsModel = DetailsModel(
                                        title = initial, index = 0,
                                        list = list as MutableList<HomeEntriesModel>,

                                        )
                                    navController.navigate(Screens.Details.root)
                                },
                            contentAlignment = Alignment.Center,
                        ) {
                            StringTextContent(
                                message = "+${list.size}",
                                color = Color.White,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun HeaderRow(initial: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = dp15, bottom = dp8),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        StringTextContent(
            modifier = Modifier,
            message = initial,
            fontSize = sp16,
            fontWeight = FontWeight.SemiBold
        )
        StringTextContent(
            modifier = Modifier.clickable {
                onClick()
            },
            message = stringResource(id = R.string.select_all),
            color = pink_color
        )
    }
} //AquaAdGallery()

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun AquaAdGallery() {
    AndroidView(
        factory = { context ->
            android.webkit.WebView(context).apply {
                settings.javaScriptEnabled = true
                loadUrl("https://servedby.aqua-adserver.com/afr.php?zoneid=10179")
            }
        },
        update = { webView ->
            // Update logic here if needed
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(144.dp)
    )
}

@Composable
fun ImageWithPlayAndSelect(item: HomeEntriesModel, onClick: (String, Int) -> Unit) {
    Box(modifier = Modifier.padding(0.dp)) {
        ImageUrlLoading(
            url = item.thumb_url,
            shape = RoundedCornerShape(0.dp),
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    onClick(item.node_id, 1)
                }
                .padding(end = dp1, bottom = dp1),
            height = dp92
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = dp8, top = dp8, start = dp4),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (item.mediaType == "Video")
                Image(
                    painter = painterResource(id = R.drawable.ic_baseline_play_circle_outline_24),
                    contentDescription = null,
                    modifier = Modifier.size(dp20, dp20)
                )
            else {
                Divider(modifier = Modifier.width(dp10))
            }
            RadioButton(
                selected = item.isSelect,
                onClick = {
                    onClick(item.node_id, 0)
                },
                colors = RadioButtonDefaults.colors(
                    selectedColor = pink_color,
                    unselectedColor = Color.White,
                    disabledColor = Color.White
                ),
                modifier = Modifier.size(dp16, dp16)
            )
        }
    }
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BottomSheetForImagePicker(
    bottomSheetScaffoldState: ModalBottomSheetState,
    viewModel: HomeViewModel,
    onClick: (BottomSheetEnum) -> Unit
) {
    ModalBottomSheetLayout(
        modifier = Modifier.padding(horizontal = dp8), sheetShape = RoundedCornerShape(dp4),
        sheetState = bottomSheetScaffoldState,
        sheetContent = {
            Column(
                Modifier
                    .fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally
            ) {
                viewModel.imageAndVideoList.onEachIndexed { index, it ->
                    TextContent(
                        text = it.key,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = dp10),
                        fontWeight = if (index == 0) FontWeight.Normal else FontWeight.Bold,
                        fontSize = if (index == 0) sp11 else sp16,
                        color = if (index == 0) hint_color else Color.Black,
                        textAlign = TextAlign.Center
                    ) {
                        onClick(it.value)
                    }
                    Divider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(border_color)
                            .height(dp1)
                    )
                }
            }
        },
        sheetBackgroundColor = Color.White
    ) {}
}