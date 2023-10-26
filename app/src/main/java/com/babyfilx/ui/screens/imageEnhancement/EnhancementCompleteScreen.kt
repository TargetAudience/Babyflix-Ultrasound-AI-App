package com.babyfilx.ui.screens.imageEnhancement

import android.annotation.SuppressLint
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.babyfilx.BottomBarScreen
import com.babyfilx.ui.theme.*
import com.babyfilx.utils.commonviews.ImageUrlLoading
import com.babyfilx.utils.commonviews.TopAppBarsWithTextOnly
import com.babyfilx.utils.logs.loge
import com.babyfilx.utils.shareData
import com.babyfilx.utils.toast
import com.babyflix.mobileapp.R

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun EnhancementCompleteUi(
    navController: NavController,
    imageUrl:String,
    selectImagesViewModel: SelectImagesViewModel?
) {

    val context = LocalContext.current

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(white),
        topBar = {
            TopAppBarsWithTextOnly(
                title = stringResource(id = R.string.enhancement_complete),
                onClick = {

                },
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize().padding(bottom = dp60)
                .verticalScroll(rememberScrollState())
        ) {
            loge("Url 2 ${selectImagesViewModel?.notificationMessage}")
            if (imageUrl.isEmpty()) {
                selectImagesViewModel?.notificationMessage?.let {
                    ImageUrlLoading(
                        url = it,
                        contentScale = ContentScale.FillBounds,
                        modifier = Modifier
                            .fillMaxSize()
                            .height(dp376),
                        shape = RoundedCornerShape(dp0)
                    )
                }
            } else {
                ImageUrlLoading(
                    url = imageUrl,
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier
                        .fillMaxSize()
                        .height(dp376),
                    shape = RoundedCornerShape(dp0)
                )
            }
            Spacer(modifier = Modifier.height(dp36))

            Column(
                modifier = Modifier.fillMaxSize().background(white),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.magic_wand_black),
                    contentDescription = null,
                    modifier = Modifier.size(dp33, dp33)
                )

                Spacer(modifier = Modifier.height(dp20))

                Text(
                    text = stringResource(id = R.string.congratulation),
                    fontSize = sp22,
                    fontFamily = fontsFamilyPoppins,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                )

                Text(
                    text = stringResource(id = R.string.image_enhancement_complete),
                    fontSize = sp16,
                    fontFamily = fontsFamilyPoppins,
                    fontWeight = FontWeight.Normal,
                    color = Color.Black,
                )

                Spacer(modifier = Modifier.height(dp44))

//                Box(modifier = Modifier.fillMaxSize(1f)) {
//                    Button(
//                        onClick = {
//                            selectImagesViewModel?.notificationMessage?.let { it ->
//                                context.shareData("Check out my imagery shared from BabyFlix ${it}")
//                            }
//                        },
//                        colors = ButtonDefaults.buttonColors(backgroundColor = pink_button),
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .height(dp60)
//                            .padding(start = dp24, end = dp24)
//                            .align(Alignment.Center), // Adjust padding values
//                        shape = RoundedCornerShape(dp30)
//                    ) {
//                        Row(
//                            modifier = Modifier.padding(end = dp4), // Adjust padding as needed
//                            verticalAlignment = Alignment.CenterVertically
//                        ) {
//                            Image(
//                                painter = painterResource(id = R.drawable.icon_share),
//                                contentDescription = null,
//                                modifier = Modifier
//                                    .padding(end = dp10)
//                                    .size(dp24)
//                            )
//                            Text(
//                                text = stringResource(id = R.string.share_image),
//                                fontSize = sp20,
//                                fontFamily = fontsFamilyPoppins,
//                                fontWeight = FontWeight.Bold,
//                                color = Color.White,
//                            )
//                        }
//                    }
//                }
                Spacer(modifier = Modifier.height(dp12))
                Box(modifier = Modifier.fillMaxSize(1f)) {
                    Button(
                        onClick = {
                            selectImagesViewModel?.setLoadingState(false)
                            navController.popBackStack() // Remove the last screen from the back stack
                            navController.navigate(BottomBarScreen.ImageSelection.routes) // Navigate to the new screen

                        },
                        colors = ButtonDefaults.buttonColors(backgroundColor = pink_button),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(dp60)
                            .padding(start = dp24, end = dp24)
                            .align(Alignment.Center), // Adjust padding values
                        shape = RoundedCornerShape(dp30)
                    ) {
                        Row(
                            modifier = Modifier.padding(end = dp4), // Adjust padding as needed
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.icon_gallery),
                                contentDescription = null,
                                modifier = Modifier
                                    .padding(end = dp10)
                                    .size(dp24)
                            )
                            Text(
                                text = stringResource(id = R.string.enhance_more),
                                fontSize = sp20,
                                fontFamily = fontsFamilyPoppins,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(dp41))
            }
        }
    }
}

fun maskUrl(originalUrl: String): String {
    val uri = Uri.parse(originalUrl)
    val scheme = uri.scheme
    val host = uri.host
    val path = uri.path

    // Create a new Uri without sensitive query parameters
    val maskedUri = Uri.Builder()
        .scheme(scheme)
        .authority(host)
        .path(path)
        .build()

    loge("Mask url : ${maskedUri}")

    return maskedUri.toString()
}
