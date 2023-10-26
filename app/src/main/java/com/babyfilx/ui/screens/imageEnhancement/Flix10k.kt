package com.babyfilx.ui.screens.freePrints

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.babyfilx.BottomBarScreen
import com.babyfilx.api.apistate.Response
import com.babyfilx.base.App
import com.babyfilx.data.models.Screens
import com.babyfilx.ui.screens.selectPlan.SelectPlanVIewModel
import com.babyfilx.ui.theme.*
import com.babyfilx.utils.commonviews.ProgressBar
import com.babyfilx.utils.commonviews.TopAppBarsWithButton
import com.babyfilx.utils.extentions.toast
import com.babyfilx.utils.payment.inAppPurchase
import com.babyflix.mobileapp.R

@Composable
fun Flix10k(navController: NavController, viewModel: SelectPlanVIewModel) {

    val context = LocalContext.current

    var currentFeedbackIndex by remember { mutableStateOf(0) }

    val responseState by viewModel.response.collectAsState(initial = Response.Loading())

    val feedbacks = listOf(
        Feedback("I was already emotional about seeing my baby, but Flix 10K powered by AI took it to another level! The clarity and detail were astonishing.  A truly unforgettable experience!", "- Isabella M -"),
        Feedback("Never did I imagine that technology could make a mother's bond with her unborn child even stronger. With Flix 10K powered by AI, each photo felt like a real-time view of my baby. The detail was so profound, it felt like I was holding her in my arms.", "- Lena K -"),
        Feedback("As a second-time mom, I thought I knew what to expect from ultrasounds. But Flix 10K powered by AI completely transformed the experience for me. The clarity was unlike anything I'd ever seen before – Instead of an ultrasound this truly has become the first photo of my son.", "- Rebecca G -"),
        Feedback("Every expecting mother deserves the magic of Flix 10K. Seeing my baby in such vivid detail made all the challenges of pregnancy worth it. Those moments, looking at my baby with such detail, will stay with me forever.", "- Mia T -"),
        Feedback("I had my reservations initially - how different could it really be? But when I saw my baby's face, so clear and detailed through Flix 10K, it felt like a dream. It was an emotional, heartwarming journey I'll cherish forever.", "- Jasmine R -"),
        )


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {

        TopAppBarsWithButton(
            title = stringResource(id = R.string.flix10k),
            isMenu = false,
            isDone = false
        ) {
//            navController.navigate(BottomBarScreen.ExperienceFlix10K.routes)
            inAppSubscribe(context, viewModel)
        }
        if (viewModel.isUpgrading.value)
            ProgressBar(withText = true)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.flix10k_banner),
                        contentDescription = null,
                        contentScale = ContentScale.FillBounds,
                        modifier = Modifier
                            .fillMaxSize()
                            .height(dp260)
                    )
                    Text(
                        text = "Introducing Flix 10K",
                        color = Color.White,
                        fontSize = sp22,
                        fontFamily = fontsFamilyPoppins,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(top = dp90, start = dp21)
                    )
                    Text(
                        text = "Your baby's first photo",
                        color = Color.White,
                        fontSize = sp17,
                        fontFamily = fontsFamilyPoppins,
                        fontWeight = FontWeight.Normal,
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(top = dp120, start = dp21)
                    )
                }
            }


            Spacer(modifier = Modifier.height(33.dp))

            Text(
                text = "Flix 10K Powered by AI",
                fontSize = 22.sp,
                fontFamily = fontsFamilyPoppins,
                fontWeight = FontWeight.Bold,
                color = pink_button,
                modifier = Modifier
                    .padding(start = dp20)
            )

            Spacer(modifier = Modifier.height(3.dp))

            Text(
                text = "Flix 10k your baby's first photo",
                fontSize = 17.sp,
                fontFamily = fontsFamilyPoppins,
                fontWeight = FontWeight.Normal,
                modifier = Modifier
                    .padding(start = dp20)
            )

            Spacer(modifier = Modifier.height(28.dp))

            Image(
                painter = painterResource(id = R.drawable.image_baby),
                contentDescription = null,
                modifier = Modifier.height(dp168)
                    .fillMaxSize().padding(start = dp20, end = dp20),
                contentScale = ContentScale.FillBounds
            )

//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(start = dp20, end = dp20),
//                horizontalArrangement = Arrangement.SpaceBetween
//            ) {
//                Box(
//                    modifier = Modifier
//                        .weight(1f)
//                        .height(180.dp)
//                        .clip(RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp))
//                ) {
//                    Image(
//                        painter = painterResource(id = R.drawable.before),
//                        contentDescription = null,
//                        modifier = Modifier
//                            .fillMaxHeight(1f)
//                            .fillMaxSize()
//                            .clip(RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp))
//                    )
//                    Text(
//                        text = "Before",
//                        color = Color.White,
//                        fontSize = 11.sp,
//                        fontFamily = fontsFamilyPoppins,
//                        fontWeight = FontWeight.SemiBold,
//                        modifier = Modifier
//                            .align(Alignment.TopStart)
//                            .padding(12.dp)
//                    )
//                }
//                Box(
//                    modifier = Modifier
//                        .weight(1f)
//                        .height(180.dp)
//                        .clip(RoundedCornerShape(topEnd = 12.dp, bottomEnd = 12.dp))
//                ) {
//                    Image(
//                        painter = painterResource(id = R.drawable.after),
//                        contentDescription = null,
//                        modifier = Modifier
//                            .fillMaxSize()
//                            .clip(RoundedCornerShape(topEnd = 12.dp, bottomEnd = 12.dp))
//                    )
//                    Text(
//                        text = "After",
//                        color = Color.White,
//                        fontSize = 11.sp,
//                        fontFamily = fontsFamilyPoppins,
//                        fontWeight = FontWeight.SemiBold,
//                        modifier = Modifier
//                            .align(Alignment.TopStart)
//                            .padding(12.dp)
//                    )
//                }
//            }


            Spacer(modifier = Modifier.height(7.dp))

            Text(
                text = "Your baby brought to life with Flix 10k",
                fontSize = 14.sp,
                fontFamily = fontsFamilyPoppins,
                fontWeight = FontWeight.Light,
                modifier = Modifier
                    .padding(start = dp20)
                    .height(20.dp)
            )

            Spacer(modifier = Modifier.height(30.dp))

            Text(
                text = "Why upgrade to Flix 10K \nPremium:",
                fontSize = 18.sp,
                fontFamily = fontsFamilyPoppins,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(start = dp20)
                    .height(50.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Column(
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.padding(start = dp15, end = dp10)
            ) {

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(18.dp)
                            .background(pink_button, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier
                                .padding(6.dp)
                                .size(14.dp)
                        )
                    }
                    Text(
                        text = "Instant, Heartfelt moments",
                        fontSize = 16.sp,
                        fontFamily = fontsFamilyPoppins,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(start = 8.dp),
                        color = pink_button
                    )
                }
                Text(
                    text = "No waiting, within moments, witness the unparalleled clarity that introduces you to your child.",
                    fontSize = 18.sp,
                    fontFamily = fontsFamilyPoppins,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(start = 28.dp),
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Column(
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.padding(start = dp15, end = 10.dp)
            ) {

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(18.dp)
                            .background(pink_color, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier
                                .padding(6.dp)
                                .size(14.dp)
                        )
                    }
                    Text(
                        text = "Beginning a Connection",
                        fontSize = 18.sp,
                        fontFamily = fontsFamilyPoppins,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(start = 8.dp),
                        color = pink_color
                    )
                }
                Text(
                    text = "Share photos with friends and family, allowing them to partake in the joy and anticipation of your baby's arrival.",
                    fontSize = 18.sp,
                    fontFamily = fontsFamilyPoppins,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(start = 28.dp),
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Column(
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.padding(start = dp15, end = 10.dp)
            ) {

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(18.dp)
                            .background(pink_color, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier
                                .padding(6.dp)
                                .size(14.dp)
                        )
                    }
                    Text(
                        text = "Value Without Compromise",
                        fontSize = 18.sp,
                        fontFamily = fontsFamilyPoppins,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(start = 8.dp),
                        color = pink_color
                    )
                }
                Text(
                    text = "Begin your baby's photo journey without stretching your budget.",
                    fontSize = 18.sp,
                    fontFamily = fontsFamilyPoppins,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(start = 28.dp),
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Column(
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.padding(start = dp15, end = 10.dp)
            ) {

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(18.dp)
                            .background(pink_color, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier
                                .padding(6.dp)
                                .size(14.dp)
                        )
                    }
                    Text(
                        text = "Harness the Power of BabyFlix Cam",
                        fontSize = 18.sp,
                        fontFamily = fontsFamilyPoppins,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(start = 8.dp),
                        color = pink_color
                    )
                }
                Text(
                    text = "Your thermal ultrasounds aren't just simple images anymore. Upload them through the specialized FlixCam, and watch as Flix 10K enhancement brings them to life.",
                    fontSize = 18.sp,
                    fontFamily = fontsFamilyPoppins,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(start = 28.dp),
                )
            }

            Spacer(modifier = Modifier.height(25.dp))

            Button(
                onClick = {
                    // Handle the click event for the "Upgrade to Premium" button
                    inAppSubscribe(context, viewModel)

                },
                colors = ButtonDefaults.buttonColors(backgroundColor = pink_button),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .padding(start = dp20, end = dp20),
                shape = RoundedCornerShape(dp30)
            ) {
                Text(
                    text = "Upgrade to Premium",
                    fontSize = 20.sp,
                    fontFamily = fontsFamilyPoppins,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
              //      modifier = Modifier.padding(top = 16.dp, bottom = 16.dp, start = 57.dp, end = 56.dp)
                    )
            }

            Column(
                modifier = Modifier.fillMaxSize().padding(top = dp48)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize().background(pink_button)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().align(Alignment.Center)
                    ) {
                        if (currentFeedbackIndex > 0) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier
                                    .padding(dp6)
                                    .clickable {
                                        if (currentFeedbackIndex > 0) {
                                            currentFeedbackIndex--
                                        }
                                    }
                            )
                        }

                        Spacer(modifier = Modifier.weight(1f)) // Add this spacer to center-align the arrows

                        if (currentFeedbackIndex < feedbacks.size - 1) {
                            Icon(
                                imageVector = Icons.Default.ArrowForward,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier
                                    .padding(dp6)
                                    .clickable {
                                        if (currentFeedbackIndex < feedbacks.size - 1) {
                                            currentFeedbackIndex++
                                        }
                                    }
                            )
                        }
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(start = dp35, top = dp21, bottom = dp26, end = dp35),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Text(
                            text = buildAnnotatedString {
                                withStyle(
                                    style = SpanStyle(
                                        fontSize = sp17,
                                        fontFamily = fontsFamilyPoppins,
                                        fontWeight = FontWeight.Normal,
                                        color = Color.White
                                    )
                                ) {
                                    append(feedbacks[currentFeedbackIndex].text) // Use the dynamic text content here
                                }
                            },
                            modifier = Modifier
                                .padding(top = dp17),
                            maxLines = 11,
                            textAlign = TextAlign.Center // Center-align the entire text
                        )
                        Spacer(modifier = Modifier.height(dp10))
                        Text(
                            text = feedbacks[currentFeedbackIndex].author,
                            color = Color.White,
                            fontSize = sp17,
                            fontFamily = fontsFamilyPoppins,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(dp60))

        }

        LaunchedEffect(responseState) {
            when (responseState) {
                is Response.Success -> {
                    val response =
                        responseState.data // Assuming 'data' holds the API response object
//                    Log.i("Testing Payment", "Response : $response")
//                findActivity(context).shareData(response.toString())
                    if (response?.code == "200") {
//                        Log.i("Testing Payment", "Response200 : $response")
                        // Display toast with success message
                        context.toast(response.message.toString())

                        // Navigate to the ImageSelection screen
                        navController.navigate(BottomBarScreen.ExperienceFlix10K.routes) {
                            popUpTo(BottomBarScreen.Flix10KSubscription.routes) { inclusive = true }
                        }
                    } else {
//                        Log.e("Testing Payment", "ResponseOther : $response")
                        // Handle other cases if needed for non-200 responses
                        context.toast(response?.message.toString())
                    }
                }

                is Response.Error -> {
//                    Log.e("Testing Payment", "ResponseError : Error")
                    context.toast("Something is wrong")
                    // Handle error, show a toast or take any required action
                }

                else -> {}
            }
        }

//        // Handle API response and navigation
//        LaunchedEffect(responseState) {
//        }

    }
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
//            // Handle failed purchase gracefully (optional)
//            // You can display a message to the user or take appropriate action
        }
    }
}

data class Feedback(val text: String, val author: String)


@Preview
@Composable
fun TwoImagesCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth(1f)
            .padding(16.dp)
            .clip(shape = MaterialTheme.shapes.medium) // Set corner radius to 10dp
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Image(
                painter = painterResource(id = R.drawable.before), // Replace with your image resource
                contentDescription = null, // Optional content description
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .clip(shape = MaterialTheme.shapes.medium)
                    .background(Color.Gray)
                    .padding(4.dp),
                contentScale = ContentScale.Crop
            )

            Image(
                painter = painterResource(id = R.drawable.after), // Replace with your image resource
                contentDescription = null, // Optional content description
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .clip(shape = MaterialTheme.shapes.medium)
                    .background(Color.Gray)
                    .padding(4.dp),
                contentScale = ContentScale.Crop
            )
        }
    }
}
