package com.babyfilx.ui.screens.dashboard

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavController
import coil.compose.ImagePainter
import coil.compose.rememberImagePainter
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.mobile.client.AWSMobileClient
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3Client
import com.babyfilx.BottomBarScreen
import com.babyfilx.api.apistate.Response
import com.babyfilx.base.App
import com.babyfilx.data.models.DetailsModel
import com.babyfilx.data.models.Screens
import com.babyfilx.data.models.response.HomeEntriesModel
import com.babyfilx.ui.screens.home.HomeViewModel
import com.babyfilx.ui.screens.home.LifeCycle
import com.babyfilx.ui.screens.news.NewsViewModel
import com.babyfilx.ui.screens.player.VideoPlayerActivity
import com.babyfilx.ui.screens.selectPlan.SelectPlanVIewModel
import com.babyfilx.ui.theme.*
import com.babyfilx.utils.commonviews.*
import com.babyfilx.utils.extentions.toast
import com.babyfilx.utils.findActivity
import com.babyfilx.utils.logs.loge
import com.babyfilx.utils.payment.inAppPurchase
import com.babyfilx.utils.shareData
import com.babyfilx.utils.toast
import com.babyflix.mobileapp.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DashboardScreen(
    navController: NavController,
    viewModel: DashboardViewModel,
    newsViewModel: NewsViewModel,
    homeViewModel: HomeViewModel
) {

    val context = LocalContext.current

    val scope = rememberCoroutineScope()

    val scrollState = rememberScrollState()
    val state = viewModel.response.collectAsState(initial = null)

    val responseState by viewModel.response3.collectAsState(initial = Response.Loading())

    val pullRefreshState =
        rememberPullRefreshState(viewModel.refreshing, {
            viewModel.refreshing = true
            viewModel.recentScans()
        })


    BackHandler {
        context.findActivity()!!.finish()
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .pullRefresh(pullRefreshState)
            .verticalScroll(scrollState)
    ) {

        Column(modifier = Modifier.padding(start = dp20, end = dp20)) {

            val currentDate = remember {
                SimpleDateFormat("EEEE d, MMMM", Locale.getDefault()).format(Date())
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Current Date and Text "My Babyflix" on the left
                Column(
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(-dp10)
                ) {
                    Text(
                        text = currentDate,
                        fontSize = sp14,
                        fontWeight = FontWeight.Normal,
                        fontFamily = fontsFamilyPoppins,
                        color = date_text_color
                    )
                    Text(
                        text = "My BabyFlix",
                        fontSize = sp30,
                        fontWeight = FontWeight.Bold,
                        fontFamily = fontsFamilyPoppins,
                        color = pink_button,
                        modifier = Modifier.height(41.dp)
                    )
                }

                // Circular Image View (Logo) on the right
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = null,
                    modifier = Modifier
                        .size(dp40)
                        .clip(CircleShape) // Apply circular clipping
                        .clickable {
//                            context.shareData(App.data.tokens)
                            navController.navigate(BottomBarScreen.More.routes)
                        }
                )
            }

            PullRefreshIndicator(
                viewModel.refreshing,
                pullRefreshState,
                Modifier
                    .align(Alignment.CenterHorizontally)
                    .offset(y = (-50).dp),
                contentColor = button_color,
                backgroundColor = Color.White,
            )

            when (state.value) {
                is Response.Loading -> {
                    // Show loading state
                    if (!viewModel.refreshing)
                        ProgressBar()
                }
                is Response.Success -> {}
                else -> {}
            }

            if (!App.data.userType.equals("premium")) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        //  .height(491.dp)
                        .padding(bottom = 39.dp)
                ) {

                    val gradientColors = listOf(Color(0xFFFA55A0), Color(0xFFFB8DBF))

                    Card(
                        modifier = Modifier
                            .fillMaxSize(),
                        elevation = dp4,
                        shape = RoundedCornerShape(18.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    brush = Brush.linearGradient(
                                        colors = gradientColors,
                                        start = Offset(0f, 0f),
                                        end = Offset(1f, 0f)
                                    )
                                ),
                        ) {

                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(top = 26.dp),
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 20.dp, end = 20.dp, bottom = 23.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {

                                    Text(
                                        text = "Upgrade now \nto Flix 10K",
                                        color = Color.White,
                                        fontSize = 26.sp,
                                        fontFamily = fontsFamilyPoppins,
                                        fontWeight = FontWeight.Bold,
                                    )
                                    Text(
                                        text = "$19.99 MO",
                                        color = Color.White,
                                        fontSize = sp12,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier
                                            .background(
                                                color = Color.Black,
                                                shape = RoundedCornerShape(dp30) // Rounded corners
                                            )
                                            .padding(
                                                top = dp5,
                                                bottom = dp5,
                                                start = dp13,
                                                end = dp13
                                            )
                                            .clickable {
                                                context.inAppPurchase(0) { purchaseToken ->
                                                    if (purchaseToken.isNotEmpty()) {
                                                        viewModel.upgradeUser(
                                                            App.data.id,
                                                            "premium",
                                                            "monthly",
                                                            purchaseToken,
                                                            "ai_subscription"
                                                        )
                                                    } else {
//                                                        // Handle failed purchase gracefully (optional)
//                                                        // You can display a message to the user or take appropriate action
                                                        context.toast("Something is wrong!!!")
                                                    }
                                                }
                                            }
                                    )
                                }

                                Image(
                                    painter = painterResource(id = R.drawable.image_baby),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .height(dp168)
                                        .fillMaxSize()
                                        .padding(start = dp20, end = dp20),
                                    contentScale = ContentScale.FillBounds
                                )

                                Spacer(modifier = Modifier.height(24.dp))

                                Column(
                                    horizontalAlignment = Alignment.Start,
                                    modifier = Modifier.padding(start = dp20, end = dp15)
                                ) {

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(dp20)
                                                .background(white, CircleShape)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = null,
                                                tint = pink_button,
                                                modifier = Modifier
                                                    .padding(6.dp)
                                                    .size(dp20)
                                            )
                                        }
                                        Text(
                                            text = "Flix 10K powered by AI captures your baby's photo journey from the beginning",
                                            fontSize = 16.sp,
                                            fontFamily = fontsFamilyPoppins,
                                            fontWeight = FontWeight.Medium,
                                            modifier = Modifier.padding(start = 8.dp),
                                            color = white
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Column(
                                    horizontalAlignment = Alignment.Start,
                                    modifier = Modifier.padding(start = dp20, end = 36.dp)
                                ) {

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(dp20)
                                                .background(white, CircleShape)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = null,
                                                tint = pink_button,
                                                modifier = Modifier
                                                    .padding(6.dp)
                                                    .size(dp20)
                                            )
                                        }
                                        Text(
                                            text = "Within moments, witness the unparalleled clarity that introduces you to your child.",
                                            fontSize = 16.sp,
                                            fontFamily = fontsFamilyPoppins,
                                            fontWeight = FontWeight.Medium,
                                            modifier = Modifier.padding(start = 8.dp),
                                            color = white
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(23.dp))

                                Button(
                                    onClick = {
                                        context.inAppPurchase(0) { purchaseToken ->
                                            if (purchaseToken.isNotEmpty()) {
                                                viewModel.upgradeUser(
                                                    App.data.id,
                                                    "premium",
                                                    "monthly",
                                                    purchaseToken,
                                                    "ai_subscription"
                                                )
                                            } else {
                                                // Handle failed purchase gracefully (optional)
                                                // You can display a message to the user or take appropriate action
                                                context.toast("Something is wrong!!!")
                                            }
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(backgroundColor = white),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(60.dp)
                                        .padding(start = 23.dp, end = 22.dp),
                                    shape = RoundedCornerShape(dp30)
                                ) {
                                    Text(
                                        text = "Upgrade to Premium",
                                        fontSize = 20.sp,
                                        fontFamily = fontsFamilyPoppins,
                                        fontWeight = FontWeight.Bold,
                                        color = upgrade_button,
                                        //      modifier = Modifier.padding(top = 16.dp, bottom = 16.dp, start = 57.dp, end = 56.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(30.dp))


                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(dp5))

            HeaderRowDashboard(initial = "Recent Scans") {
                navController.navigate(Screens.Home.root)
            }

            HorizontalLine()

            RecentCards(viewModel.images, homeViewModel, navController, context)

            HeaderRowDashboard(initial = "Recent News") {
                navController.navigate(Screens.News.root)
            }

            HorizontalLine()

            RecentNewsCards(newsViewModel,viewModel, onClick = {
                loge("Data of news ")
                newsViewModel.pos = it
                navController.navigate(Screens.NewsDetails.root)
            })
            ErrorMessage(viewModel.mainMessage) {
                viewModel.mainMessage = ""
                viewModel.recentScans()
                newsViewModel.getBlogApi()
//                viewModel.recentBlogs()
            }
        }
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(top = dp100)) {
            Text(
                text = "Flix 10k patent pending",
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = bottom_background,
                    )
                    .padding(top = dp17, bottom = dp17),
                color = version_text,
                fontSize = sp15,
                fontWeight = FontWeight.Light,
                textAlign = TextAlign.Center,
            )
        }

        Spacer(modifier = Modifier.height(dp60))

        loge("List of items in viewmodeldash ${viewModel.items.size}")
    }

    if(viewModel.isUpgrading.value)
        ProgressBar(withText = true)

    LaunchedEffect(responseState) {
        when (responseState) {
            is Response.Success -> {
                val response = responseState.data // Assuming 'data' holds the API response object
                if (response?.code == "200") {
                    // Display toast with success message
                    context.toast(response.message.toString())
                    // Navigate to the ExperienceFlix10K screen
                    navController.navigate(BottomBarScreen.ExperienceFlix10K.routes)
                } else {
                    // Handle other cases if needed for non-200 responses
                    context.toast(response?.message.toString())
                }
            }
            is Response.Error -> {
                // Handle error, show a toast or take any required action
                context.toast("Something is wrong")
            }
            else -> {}
        }
    }

    LifeCycle(viewModel = newsViewModel)
}

@Composable
fun LifeCycle(viewModel: NewsViewModel) {
    ComposableLifecycle { _, event ->
        when (event) {
            Lifecycle.Event.ON_RESUME -> {
//                viewModel.getBlogApi()
                viewModel.likesStatusApi()
            }
            else -> {}
        }
    }
}


@Composable
fun RecentCards(
    items: List<HomeEntriesModel>,
    homeViewModel: HomeViewModel,
    navController: NavController,
    context: Context
) {
        LazyRow {
            itemsIndexed(items.take(5)) { index, item ->
                Column(
                    modifier = Modifier
                        .padding(top = dp20, end = dp10)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .requiredHeight(height = 265.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(340.dp)
                                    .height(200.dp)
                            ) {
                                Card(
                                    modifier = Modifier.fillMaxSize(),
                                    elevation = dp4,
                                    shape = RoundedCornerShape(dp15)
                                ) {
                                    ImageUrlLoading(
                                        shape = RoundedCornerShape(dp15),
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clickable {
                                                homeViewModel.detailsModel = DetailsModel(
                                                    title = item.created_at,
                                                    index = index,
                                                    list = items as MutableList<HomeEntriesModel>,
                                                    url = item.download_url,
                                                    nodeId = item.node_id,
                                                )
                                                if (item.mediaType == "Video") {
                                                    homeViewModel.videoList()
                                                    val intent =
                                                        Intent(
                                                            context,
                                                            VideoPlayerActivity::class.java
                                                        )
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                                    intent.putExtra(
                                                        "data",
                                                        homeViewModel.detailsModel
                                                    )
                                                    intent.putExtra("url", item.download_url)
                                                    intent.putExtra("nodeId", item.node_id)
                                                    context.startActivity(intent)
                                                } else {
                                                    navController.navigate(Screens.Details.root)
                                                }
                                            },
                                        url = item.thumb_url,
                                        contentScale = ContentScale.FillBounds
                                    )
                                    if (item.mediaType == "Video")
                                        Image(
                                            painter = painterResource(id = R.drawable.ic_baseline_play_circle_outline_24),
                                            contentDescription = null,
                                            modifier = Modifier
                                                .size(53.dp)
                                                .align(Alignment.Center)
                                        )
                                }
                            }
                            Text(
                                text = item.created_at,
                                color = Color(0xff909090),
                                style = TextStyle(
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = dp9)
                            )
                        }
                    }
                }
            }
        }
    }


@Composable
fun RecentNewsCards(items: NewsViewModel,dashboardViewModel: DashboardViewModel, onClick: (Int) -> Unit) {
    val state = items.states
    LazyRow {
        itemsIndexed(state.items.take(5)) { index, item ->
            Column(
                modifier = Modifier
                    .padding(top = dp20, end = dp10)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(height = 265.dp)
                        .clickable { onClick(index) }
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Card(
                            modifier = Modifier
                                .width(width = 340.dp)
                                .height(height = 200.dp),
                            elevation = dp4,
                            shape = RoundedCornerShape(dp15)
                        ) {
                            ImageUrlLoading(
                                shape = RoundedCornerShape(dp15),
                                modifier = Modifier
                                    .width(width = 340.dp)
                                    .height(height = 200.dp),
                                url = item.blogImagePath,
                                contentScale = ContentScale.FillBounds
                            )
                        }
                        Text(
                            text = item.blogTitle,
                            color = Color.Black,
                            style = TextStyle(
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            modifier = Modifier
                                .width(340.dp)
                                .padding(top = dp9),
                            maxLines = 1
                        )

                        Text(
                            text = convertMiliSeconds(item.createdAt),
                            color = Color(0xff909090),
                            style = TextStyle(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

        }
    }
}

fun convertMiliSeconds(miliSeconds: String): String {
    val simpleDateFormat = SimpleDateFormat("HH:mm a | MMMM dd,yyyy", Locale("en"))
    return simpleDateFormat.format(miliSeconds.toLong() * 1000L)
}


fun inAppSubscribe(context: Context, viewModel: SelectPlanVIewModel) {
    context.inAppPurchase(0){ purchaseToken ->
        if (purchaseToken.isNotEmpty()) {
            viewModel.upgradeUser(
                App.data.id,
                "premium",
                "monthly",
                purchaseToken,
                "ai_subscription"
            )
        } else {
            // Handle failed purchase gracefully (optional)
            // You can display a message to the user or take appropriate action
        }
    }
}