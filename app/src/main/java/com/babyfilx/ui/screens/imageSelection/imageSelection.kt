package com.babyfilx.ui.screens.imageSelection

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.NavController
import com.babyfilx.BottomBarScreen
import com.babyfilx.api.apistate.Response
import com.babyfilx.data.models.Screens
import com.babyfilx.data.models.response.HomeEntriesModel
import com.babyfilx.data.rememberAppState
import com.babyfilx.ui.screens.imageEnhancement.ProcessingImageUi
import com.babyfilx.ui.screens.imageEnhancement.SelectImagesViewModel
import com.babyfilx.ui.theme.*
import com.babyfilx.utils.Constant
import com.babyfilx.utils.commonviews.ImageUrlLoading
import com.babyfilx.utils.commonviews.ProgressBar
import com.babyfilx.utils.commonviews.TopAppBarsWithTextOnly
import com.babyfilx.utils.logs.loge
import com.babyfilx.utils.permissions.imageName
import com.babyfilx.utils.toast
import com.babyflix.mobileapp.R
import kotlinx.coroutines.*

@SuppressLint("UnusedMaterialScaffoldPaddingParameter", "CoroutineCreationDuringComposition")
@Composable
fun ImageSelectionScreen(navController: NavController, viewModel: SelectImagesViewModel) {
    val context = LocalContext.current
    val state = viewModel.response.collectAsState(initial = null)
    val selectState = viewModel.response2.collectAsState(initial = null)

    var selectedImage by remember { mutableStateOf<HomeEntriesModel?>(value = null) }

    val broadcastManager = LocalBroadcastManager.getInstance(context)

    val scope = rememberCoroutineScope()

    val mainActivityScope = CoroutineScope(Dispatchers.Main)

    val scaffoldState = rememberScaffoldState()

    var hasNavigated by remember { mutableStateOf(false) }

    val appState = rememberAppState()

    DisposableEffect(Unit) {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                intent?.let {
                    if (it.action == "FCM_NOTIFICATION_RECEIVED") {
                        // Trigger the pull-to-refresh operation here
                        viewModel.notificationTitle = it.getStringExtra("title") ?: ""
                        viewModel.notificationMessage = it.getStringExtra("body") ?: ""

                        if (!hasNavigated && viewModel.notificationTitle == "Babyflix") {
                            loge("url ${viewModel.notificationMessage}")
                            // Navigate only if it hasn't already navigated
                            navController.navigate(BottomBarScreen.EnhancementComplete.routes)
                            hasNavigated = true // Set the flag to true
                            viewModel.setLoadingState(false)
                        }
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

//    when(viewModel.notificationTitle){
//        "Babyflix" -> {
//            loge("url ${viewModel.notificationMessage}")
//            navController.navigate(Screens.EnhancementComplete.root)
//
//        }
//    }


    BackHandler(onBack = {
        // Perform your action here when the back button is pressed
        // For example, clear the selection
        if (!viewModel.isLoading.value) {
            if (viewModel.isRadioButtonActive.value) {
                viewModel.setRadioButtonActiveState(false)
                selectedImage = null
                //TODO
            } else {
                navController.navigateUp()
            }
        }
    })

    Scaffold(modifier = Modifier.fillMaxSize(), backgroundColor = Color.White, topBar = {
        TopAppBarsWithTextOnly(
            modifier = Modifier,
            title = if (viewModel.isLoading.value) stringResource(id = R.string.processing_image) else stringResource(
                id = R.string.flix10k
            ),
            isCancel = viewModel.isRadioButtonActive.value,
            isEnhance = true,
            isContinue = false,
        )
        {
            if(it == 1){
                if(selectedImage != null){
                    viewModel.enhanceImages(selectedImage)
                    viewModel.setRadioButtonActiveState(false)
                    selectedImage = null
                }else{
                    context.toast("Please select atleast one image")
                }
            }else if(it == 2){
                viewModel.setRadioButtonActiveState(false)
                selectedImage = null
                //TODO
            }else if(it == 3){
                if(selectedImage != null)
                    viewModel.enhanceImages(selectedImage)
                else
                    context.toast("Please select atleast one image")
            }
        }
    }) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.weight(1f)) {
                ImageContent(
                    images = viewModel.images,
                    onImageSelected = { image ->
                        selectedImage = if (selectedImage == image) null else image
                    },
                    selectedImage = selectedImage,
                    viewModel = viewModel
                )
                when (state.value) {
                    is Response.Loading -> {
                        // Show loading state
                        ProgressBar(modifier = Modifier.fillMaxSize())
                    }
                    is Response.Success -> {}
                    else -> {}
                }
                LaunchedEffect(selectState.value){
                    when (selectState.value) {
                        is Response.Loading -> {
                            // Show loading state
                        }
                        is Response.Success -> {
                            // Show toast message with the success response
//                            mainActivityScope.launch {
//                                showSnackBar("Image submitted for enhancement", scaffoldState)
//                            }
//                            viewModel.setLoadingState(false)
//                            navController.navigate(Screens.EnhancementComplete.root)
                        }
                        is Response.Error -> {
                            // Show toast message with the error message
                            context.toast(viewModel.message)
                        }
                        else -> {}
                    }
                }
            }
            if (viewModel.isLoading.value) {
//                ProgressBar(modifier = Modifier.fillMaxSize())
                ProcessingImageUi()
            }
        }
    }
}

@Composable
fun ImageContent(
    images: List<HomeEntriesModel>,
    onImageSelected: (HomeEntriesModel) -> Unit,
    selectedImage: HomeEntriesModel?,
    viewModel: SelectImagesViewModel
) {

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier.fillMaxSize()
    ) {
        items(images) { image ->
            viewModel.setItemSelectedState(selectedImage == image)
            //TODO

            Row(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f)
                        .padding(dp2)
                        .alpha(if (viewModel.isItemSelected.value) 0.5f else 1f)
                        .clickable {
                            if (viewModel.isRadioButtonActive.value)
                            viewModel.setItemSelectedState(selectedImage == image)
                            viewModel.setRadioButtonActiveState(true)
                            onImageSelected(image)

                        }
                ) {
                    ImageUrlLoading(
                        modifier = Modifier.fillMaxSize(),
                        url = image.thumb_url,
                        contentScale = ContentScale.FillBounds,
                        shape = RoundedCornerShape(dp0)
                    )
                    if(viewModel.isRadioButtonActive.value){
                        RadioButton(
                            selected = viewModel.isItemSelected.value,
                            onClick = {
                                // Select the image when the radio button is clicked
                                onImageSelected(image)
                            },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = pink_color,
                                unselectedColor = Color.White,
                                disabledColor = Color.White
                            ),
                            modifier = Modifier
                                .size(16.dp)
                                .align(Alignment.TopEnd)
                                .padding(end = 8.dp, top = 8.dp)
                        )
                    }
                }
            }
        }
    }
}


suspend fun showSnackBar(s: String, scaffoldState: ScaffoldState) {
    scaffoldState.snackbarHostState.showSnackbar(s)
}