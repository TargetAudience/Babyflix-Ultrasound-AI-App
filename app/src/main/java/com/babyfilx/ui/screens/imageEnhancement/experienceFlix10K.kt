package com.babyfilx.ui.screens.imageEnhancement

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.navigation.NavController
import com.babyfilx.BottomBarScreen
import com.babyfilx.ui.theme.*
import com.babyfilx.utils.commonviews.*
import com.babyflix.mobileapp.R


@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun ExperienceFlix10KScreen(
    navController: NavController,
    selectImagesViewModel: SelectImagesViewModel
) {

    selectImagesViewModel.setUser("premium")
    Scaffold(modifier = Modifier.fillMaxSize(), backgroundColor = Color.White, topBar = {
        TopAppBars(
            title = "Flix 10K",
            isMenu = false,
            isDone = false
        ) {
            navController.navigateUp()
        }
    })  {
        Column(modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())) {
            Image(
                painter = painterResource(id = R.drawable.image_baby),
                contentDescription = null,
                modifier = Modifier
                    .height(dp188)
                    .fillMaxSize(),
                contentScale = ContentScale.FillBounds
            )
            Box(modifier = Modifier
                .fillMaxWidth()
                .fillMaxSize()
                .padding(top = dp20)) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(start = dp30, end = dp30),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.icon_flix10k_magic_wand),
                        contentDescription = null,
                        modifier = Modifier,
                        contentScale = ContentScale.FillBounds
                    )
                    Text(
                        text = buildAnnotatedString {
                            withStyle(
                                style = SpanStyle(
                                    fontSize = sp24,
                                    fontFamily = fontsFamilyPoppins,
                                    fontWeight = FontWeight.Bold,
                                    color = pink_button
                                )
                            ) {
                                append("Experience the Magic\n")
                                append("of BabyFlix 10k")
                            }
                        },
                        modifier = Modifier
                            .padding(top = dp17),
                        maxLines = 2,
                        textAlign = TextAlign.Center // Center-align the entire text
                    )
                    Text(
                        text = buildAnnotatedString {
                            withStyle(
                                style = SpanStyle(
                                    fontSize = sp14,
                                    fontFamily = fontsFamilyPoppins,
                                    fontWeight = FontWeight.Normal,
                                    color = new_black_text
                                )
                            ) {
                                append("Our revolutionary AI-powered\n")
                                append("technology that enhances your\n")
                                append("ultrasounds and introduces you to\n")
                                append("your baby early.")
                            }
                        },
                        modifier = Modifier.padding(top = dp6),
                        maxLines = 4,
                        textAlign = TextAlign.Center // Center-align the entire text
                    )

                    Text(
                        text = buildAnnotatedString {
                            withStyle(
                                style = SpanStyle(
                                    fontSize = sp14,
                                    fontFamily = fontsFamilyPoppins,
                                    fontWeight = FontWeight.Normal,
                                    color = new_black_text
                                )
                            ) {
                                append("For optimal Flix 10K  AI enhancement,\n")
                                append("please select 'Enhance' for your 3D and 4D ultrasounds\n")
                                append("Note: Not all images, such as 2D Ultrasounds may\n")
                                append("be eligible for enhancement.")
                            }
                        },
                        modifier = Modifier.padding(top = dp6),
                        maxLines = 7,
                        textAlign = TextAlign.Center // Center-align the entire text
                    )

                }
            }
            Box(modifier = Modifier.fillMaxSize().padding(top = dp40)) {
                Button(
                    onClick = {
                        navController.navigate(BottomBarScreen.ImageSelection.routes)
//                              navController.navigate(Screens.EnhancementComplete.root)

                    },
                    colors = ButtonDefaults.buttonColors(backgroundColor = pink_button),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(dp60)
                        .padding(start = dp23, end = dp23)
                        .align(Alignment.Center), // Adjust padding values
                    shape = RoundedCornerShape(dp30)
                ) {
                    Text(
                        text = "Get Started",
                        fontSize = sp20,
                        fontFamily = fontsFamilyPoppins,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                    )
                }
            }
        }
    }
}

