package com.babyfilx.ui.screens.imageEnhancement

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import androidx.navigation.NavController
import com.babyfilx.data.models.Screens
import com.babyfilx.ui.screens.home.HomeViewModel
import com.babyfilx.ui.theme.*
import com.babyfilx.utils.commonviews.ImageUrlLoading
import com.babyfilx.utils.commonviews.StringTextContent
import com.babyflix.mobileapp.R


@Composable
fun ImageEnhancementScreen(navController: NavController) {
    Box(modifier = Modifier.fillMaxSize()) {
        // Load your image here
        val backgroundImage = painterResource(id = R.drawable.sample)
        val beforeImg = painterResource(id = R.drawable.before)
        val afterImg = painterResource(id = R.drawable.after)

//        Image(
//            painter = backgroundImage,
//            contentDescription = null,
//            modifier = Modifier.fillMaxSize(),
//            contentScale = ContentScale.Crop
//        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 28.dp, end = 28.dp, bottom = 100.dp)
                .verticalScroll(rememberScrollState())
                .align(Alignment.Center)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(2f) // You can adjust the aspect ratio as needed
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Before",
                        fontFamily = fontsFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = sp14,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )

                    Image(
                        painter = beforeImg,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp),
                        contentScale = ContentScale.Fit
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "After",
                        fontFamily = fontsFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = sp14,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )

                    Image(
                        painter = afterImg,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp),
                        contentScale = ContentScale.Fit
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Welcome to Flix 10K!",
                fontFamily = fontsFamily,
                fontWeight = FontWeight.Bold,
                fontSize = sp20,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()

            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Witness the wonder of your little one's development with AI-powered precision.",
                fontFamily = fontsFamily,
                fontWeight = FontWeight.Normal,
                fontSize = sp14,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(30.dp))

            Text(
                text = "AI Fetal Image Enhancement ",
                fontFamily = fontsFamily,
                fontWeight = FontWeight.Bold,
                fontSize = sp16
            )

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append("• ")
                    }
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append("Clarity Unveiled: ")
                    }
                    append(text = "Our cutting-edge AI technology works to transform your fetal ultrasound images into stunning high-definition visuals.")
                },
                fontFamily = fontsFamily,
                fontSize = sp14
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append("• ")
                    }
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append("Incredible Detail: ")
                    }
                    append(text = "See your baby with amazing clarity.")
                },
                fontFamily = fontsFamily,
                fontSize = sp14
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append("• ")
                    }
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append("Cherished Moments: ")
                    }
                    append(text = "Witness the intricacies of your baby’s development, and bond with your little miracle even before they’re in your arms.")
                },
                fontFamily = fontsFamily,
                fontSize = sp14
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append("• ")
                    }
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append("Unlimited Enhancements: ")
                    }
                    append(text = "Enhance as many ultrasound images as you like during your subscription.")
                },
                fontFamily = fontsFamily,
                fontSize = sp14
            )
            Spacer(modifier = Modifier.height(30.dp))
//            Text(
//                text = "Complimentary 4x6 Prints ",
//                fontFamily = fontsFamily,
//                fontWeight = FontWeight.Bold,
//                fontSize = sp16
//            )
//            Spacer(modifier = Modifier.height(18.dp))
//            Text(
//                text = buildAnnotatedString {
//                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
//                        append("• ")
//                    }
//                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
//                        append("Tangible Memories: ")
//                    }
//                    append(text = "Get 10 premium quality 4x6 prints of your enhanced images.")
//                },
//                fontFamily = fontsFamily,
//                fontSize = sp14
//            )
//            Spacer(modifier = Modifier.height(8.dp))
//            Text(
//                text = buildAnnotatedString {
//                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
//                        append("• ")
//                    }
//                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
//                        append("Perfect for Sharing: ")
//                    }
//                    append(text = "Whether it's for your keepsake or to share with family, these prints are the ideal memento.")
//                },
//                fontFamily = fontsFamily,
//                fontSize = sp14
//            )
//            Spacer(modifier = Modifier.height(8.dp))
//            Text(
//                text = buildAnnotatedString {
//                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
//                        append("• ")
//                    }
//                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
//                        append("Delivered to Your Doorstep: ")
//                    }
//                    append(text = "Enjoy the convenience of having your prints delivered straight to your home.")
//                },
//                fontFamily = fontsFamily,
//                fontSize = sp14
//            )
//            Spacer(modifier = Modifier.height(20.dp))
//            // Rest of the text components...
//
//            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Tap below to subscribe to BabyBlink Premium and unlock the magic.",
                fontFamily = fontsFamily,
                fontWeight = FontWeight.Normal,
                fontSize = sp14,
                textAlign = TextAlign.Center
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 28.dp, vertical = 40.dp)
                .align(Alignment.BottomCenter)
        ) {
            Button(
                onClick = { navController.navigate(Screens.SelectPlan.root) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = pink_dark, contentColor = white)
            ) {
                Text(
                    text = stringResource(id = R.string.continue_text),
                    fontFamily = fontsFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = sp16
                )
            }
        }
    }
}
