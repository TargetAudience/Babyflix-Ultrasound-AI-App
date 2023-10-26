package com.babyfilx.ui.screens.home

import android.app.Activity.RESULT_OK
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.NavController
import com.babyfilx.api.apistate.Response
import com.babyfilx.api.workmanager.UploadService
import com.babyfilx.base.App
import com.babyfilx.base.App.Companion.isFirst
import com.babyfilx.data.enums.BottomBarEnum
import com.babyfilx.data.enums.BottomSheetEnum
import com.babyfilx.data.enums.HomeEnum
import com.babyfilx.ui.theme.*
import com.babyfilx.utils.Constant.getRealPathFromURI
import com.babyfilx.utils.commonviews.*
import com.babyfilx.utils.extentions.download
import com.babyfilx.utils.extentions.toast
import com.babyfilx.utils.logs.loge
import com.babyfilx.utils.permissions.*
import com.babyflix.mobileapp.R
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch



@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalPagerApi::class, ExperimentalMaterialApi::class)
@Composable
fun HomeScreen(navController: NavController, viewModel: HomeViewModel) {

    val context = LocalContext.current
    val broadcastManager = LocalBroadcastManager.getInstance(context)

    val scope = rememberCoroutineScope()

    val scaffoldState = rememberScaffoldState()

    // Create a state to track if a broadcast has been received
    var isBroadcastReceived by remember { mutableStateOf(false) }

    val state = viewModel.response.collectAsState(initial = null)
    val deleteState = viewModel.deleteResponse.collectAsState(initial = null)
    val isDownload = viewModel.isDownload
    var isUpload by remember { mutableStateOf(false) }
    val bottomSheetScaffoldState =
        rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    val permissionRequest =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { resultsMap -> }
    val pullRefreshState =
        rememberPullRefreshState(viewModel.refreshing, {
            viewModel.refreshing = true
            viewModel.galleyApi()
        })
    val pagerState = rememberPagerState(
        pageCount = viewModel.tabData.size,
        initialOffscreenLimit = 2,
        initialPage = 0,
    )
    val tabIndex = pagerState.currentPage
    val coroutineScope = rememberCoroutineScope()

    val mainActivityScope = CoroutineScope(Dispatchers.Main)


    DisposableEffect(Unit) {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent?.action == "FCM_NOTIFICATION_RECEIVED") {
                    // Trigger the pull-to-refresh operation here
                    scope.launch {
                        delay(40000) // 2000 milliseconds = 2 seconds
                        viewModel.refreshing = true
                        viewModel.galleyApi()
                    }
                }
            }
        }
        val filter = IntentFilter("FCM_NOTIFICATION_RECEIVED")
        broadcastManager.registerReceiver(receiver, filter)

        onDispose {
            broadcastManager.unregisterReceiver(receiver)
        }
    }


    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            loge("PagerState $page")
            viewModel.type = page
        }
    }


    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .weight(9f)
        ) {

//            WebView1(url = "https://servedby.aqua-adserver.com/afr.php?zoneid=10179")

            // on below line adding admob banner ads.
//            AdView(adId = "ca-app-pub-4273229953550397~976499825")

            Surface( // Add padding as needed
                elevation = dp10
            ) {
                TabRow(backgroundColor = Color.White,
                    selectedTabIndex = tabIndex,
                    indicator = { tabPositions ->
                        TabRowDefaults.Indicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[tabIndex]),
                            color = pink_color
                        )
                    },
                    divider = {}
                ) {
                    viewModel.tabData.forEachIndexed { index, pair ->
                        Tab(selected = tabIndex == index, onClick = {
                            viewModel.type = index
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        }, text = {
                            Text(
                                text = pair,
                                fontFamily = fontsFamily,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = sp16
                            )
                        }, selectedContentColor = pink_color, unselectedContentColor = Color.Black)
                    }
                }
            }

            //WebView1(url = "https://servedby.aqua-adserver.com/afr.php?zoneid=10179")


            HorizontalPager(
                modifier = Modifier.fillMaxSize(),
                state = pagerState,
            ) { index ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .pullRefresh(pullRefreshState)
                        .padding(horizontal = dp15)
                ) {
                    //WebView1(url = "https://servedby.aqua-adserver.com/afr.php?zoneid=10179")
                    when (index) {
                        0 ->
                            HomeContent(
                                viewModel.all,
                                HomeEnum.All,
                                viewModel,
                                navController,
                                context
                            )
                        1 ->
                            HomeContent(
                                viewModel.images,
                                HomeEnum.Image,
                                viewModel,
                                navController,
                                context
                            )
                        2 ->
                            HomeContent(
                                viewModel.videos,
                                HomeEnum.Videos,
                                viewModel,
                                navController,
                                context
                            )
                    }
                }
                //if (viewModel.refreshing)
                PullRefreshIndicator(
                    viewModel.refreshing,
                    pullRefreshState,
                    Modifier
                        .align(Alignment.TopCenter)
                        .offset(y = (-50).dp),
                    contentColor = button_color,
                    backgroundColor = Color.White,
                )

                ErrorMessage(viewModel.mainMessage) {
                    viewModel.mainMessage = ""
                    viewModel.galleyApi()
                    viewModel.getLocationAPI()
                }
                when (state.value) {
                    is Response.Loading -> {
                        if (!viewModel.refreshing)
                            ProgressBar()

                    }
                    is Response.Success -> {}
                    else -> {}
                }
                when (deleteState.value) {
                    is Response.Loading -> ProgressBar()
                    is Response.Success -> {}
                    else -> {}
                }

            }

        }
        HomeBottomBar(modifier = Modifier.weight(1f), paddingBottom = dp60) {
            when (it) {
                BottomBarEnum.Download -> {
                    viewModel.downloadImageAndVideo { model ->
                        if (model == null) {
                            viewModel.message = "Please select the videos/images for download."
                        } else {
                            model.let { mode ->
                                val permission = context.writePermissions()
                                if (permission.isNotEmpty()) {
                                    loge("messagesss $permission")
                                    permissionRequest.launch(permission.toTypedArray())
                                } else {
                                    context.download(mode)
                                }
                            }
                        }
                    }
                }
                BottomBarEnum.Delete -> {
                    viewModel.delete = true
                }
                BottomBarEnum.Upload -> {
                    coroutineScope.launch {
                        bottomSheetScaffoldState.show()
                    }
                    viewModel.bottomSheet = true
                }

            }
        }
    }

    MessageAlert(name = viewModel.message) {
        viewModel.message = ""
    }


    context.uploadFiles(isUpload, viewModel) {
        isUpload = false
    }
    deleteCall(viewModel) {
        viewModel.delete = false
        viewModel.deleteApi()
    }
   // context.inAppPurchase()

    context.BottomSheetOpen(coroutineScope, viewModel, bottomSheetScaffoldState , mainActivityScope, scaffoldState) { isUpload = it }
    LifeCycle(viewModel = viewModel)
}





@Composable
fun LifeCycle(viewModel: HomeViewModel) {
    ComposableLifecycle { _, event ->
        when (event) {
            Lifecycle.Event.ON_RESUME -> {
                    viewModel.galleyApi()
            }
            else -> {}
        }
    }
}

fun Context.validFormat(isImage: Boolean, viewModel: HomeViewModel, onClick: () -> Unit) {
    val path = getRealPathFromURI(viewModel.uri.toUri())
    val temp = path!!.substring(path.toString().lastIndexOf(".") + 1)
    val list = listOf("mp4", "avi")
    loge("nsjdbfajb $isImage")
    if (!list.contains(temp) && isImage) {
        viewModel.message = getString(R.string.video_upload_message)
        return
    } else {
        onClick()
    }
}


/**
 * this is bottom sheet for images and videos picking
 */
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun Context.BottomSheetOpen(
    scope: CoroutineScope,
    viewModel: HomeViewModel,
    bottomSheetScaffoldState: ModalBottomSheetState,
    mainActivityScope: CoroutineScope,
    scaffoldState: ScaffoldState,
    upload: (Boolean) -> Unit
) {
    val permissionRequest =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { resultsMap -> }
    val isPicker = isPhotoPickerAvailable()

    LaunchedEffect(Unit) {
        val permission = pushNotificationPermission()
        if (permission.isNotEmpty()) {
            permissionRequest.launch(permission.toTypedArray())
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            loge("gjfh ${viewModel.uri}")
            if (success) {
                val message = if (viewModel.isVideo) {
                    getString(R.string.video_taken_successfully)
                } else {
                    getString(R.string.image_taken_successfully)
                }
                // TODO show toast msg image selected
                mainActivityScope.launch {
                    showSnackBar(message, scaffoldState )
                }
                upload(true)
            }
        }
    )

    loge("IsPicker  $isPicker")
    val gallery = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            viewModel.uri = uri.toString()
            //viewModel.callUploadApi()
            if (uri != null) {
                val message = if (viewModel.isVideo) {
                    getString(R.string.video_taken_successfully)
                } else {
                    getString(R.string.image_taken_successfully)
                }
                //TODO
                mainActivityScope.launch {
                    showSnackBar(message, scaffoldState)
                }
                validFormat(viewModel.isVideo, viewModel) {
                    upload(true)
                }
            }
        }
    )

    val galleryNotPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            viewModel.uri = uri.toString()
            //viewModel.callUploadApi()
            if (uri != null) {
                mainActivityScope.launch {
                    showSnackBar(getString(R.string.image_taken_successfully), scaffoldState)
                }
                validFormat(viewModel.isVideo, viewModel) {
                    upload(true)
                }
            }
        }
    )
    val pickVideoLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                result.data?.let {
                    viewModel.uri = it.data.toString()
                    //viewModel.callUploadApi()
                    mainActivityScope.launch {
                        showSnackBar(getString(R.string.video_taken_successfully), scaffoldState)
                    }
                    upload(true)
                } ?: mainActivityScope.launch {
                    showSnackBar(getString(R.string.something_went_wrong), scaffoldState)
                }
            }
        }

    if (viewModel.bottomSheet)
        BottomSheetForImagePicker(bottomSheetScaffoldState, viewModel) { it ->
            loge("messagesss ")
            val permission = updateOrRequestPermissions()
            if (permission.isNotEmpty() && (it != BottomSheetEnum.Any && it != BottomSheetEnum.Nothing)) {
                loge("messagesss $permission")
                permissionRequest.launch(permission.toTypedArray())
            } else {
                when (it) {
                    BottomSheetEnum.Image_Camera -> {
                        myClickHandler { uri ->
                            viewModel.uri = uri.toString()
                            cameraLauncher.launch(uri)
                        }
                    }
                    BottomSheetEnum.Image_Gallery -> {
                        viewModel.isVideo = false
                        if (isPicker)
                            gallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                        else
                            galleryNotPicker.launch("image/*")
                    }
                    BottomSheetEnum.Video_Camera -> {
                        cameraIntent { intent ->
                            pickVideoLauncher.launch(intent)
                        }
                    }
                    BottomSheetEnum.Video_Gallery -> {
                        viewModel.isVideo = true
                        if (isPicker)
                            gallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly))
                        else
                            galleryNotPicker.launch("video/*")
                    }
                    else -> {}
                }

                scope.launch {
                    if (it != BottomSheetEnum.Any) {
                        bottomSheetScaffoldState.hide()
                        viewModel.bottomSheet = false
                    }

                }
            }
        }

}


@Composable
fun deleteCall(viewModel: HomeViewModel, onClick: () -> Unit) {
    if (viewModel.delete)
        Alert(name = stringResource(id = R.string.delete_message),
            title = stringResource(id = R.string.delete),
            onDismiss = { viewModel.delete = !viewModel.delete },
            {
                onClick()
            }
        ) {

        }
}


//this  is for upload files
fun Context.uploadFiles(isUpload: Boolean, viewModel: HomeViewModel, onClick: () -> Unit) {
    if (isUpload) {
        // uploadFile(uri = viewModel.uri.toUri(),viewModel.locationModel){}
        val data = Intent(this, UploadService::class.java)
        data.putExtra("file", viewModel.uri)
        data.putExtra("company", App.data.companyId)
        data.putExtra("location", App.data.locationId)
        startService(data)
    }
    onClick()
}

private suspend fun showSnackBar(s: String, scaffoldState: ScaffoldState) {
    scaffoldState.snackbarHostState.showSnackbar(s)
}


