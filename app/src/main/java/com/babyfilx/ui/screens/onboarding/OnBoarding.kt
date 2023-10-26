package com.babyfilx.ui.screens.login

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.babyfilx.data.models.Screens
import com.babyfilx.ui.theme.*
import com.babyfilx.utils.findActivity
import com.babyflix.mobileapp.R
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState

@OptIn(ExperimentalPagerApi::class)
@Composable
fun OnBoardScreen(navController: NavController) {

    val scrollState = rememberScrollState()

    val context = LocalContext.current

    val pagerState = rememberPagerState(
        pageCount = 1,
        initialOffscreenLimit = 2,
        initialPage = 0,
    )

    val tabIndex = pagerState.currentPage

    BackHandler {
        context.findActivity()!!.finish()
    }

    HorizontalPager(
        modifier = Modifier.fillMaxSize(),
        state = pagerState,
    ) { index ->
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            when (index) {
                0 ->
                    onBoard(navController = navController, tabIndex = tabIndex, white)
                1 ->
                    onBoardFlix(
                        navController = navController,
                        tabIndex = tabIndex,
                        s = "Your babies’ memories\nstart with BabyFlix",
                        s1 = "Easily share or print your Flix 10K\nenhanced ultrasound photos with\nfriends and family.",
                        onboardBackground = R.drawable.onboard_background,
                        selectedColor = black
                    )
                2 ->
                    onBoardFlix(
                        navController = navController,
                        tabIndex = tabIndex,
                        s = "Ultrasounds enhanced\nwith Flix 10K",
                        s1 = " Introducing Flix 10K powered by AI.\nTransforming 3D/4D/5D ultrasounds to\nthe first photos of your baby.",
                        onboardBackground = R.drawable.onboard_background,
                        selectedColor = black
                    )
                3 ->
                    onBoardFlix(
                        navController = navController,
                        tabIndex = tabIndex,
                        s = "Transform printed ultrasounds\nwith FlixCam",
                        s1 = "Upload ultrasound printouts with\nFlixCam and get AI enhanced photos\nof your baby powered by Flix 10K.",
                        onboardBackground = R.drawable.onboard_background,
                        selectedColor = black
                    )

            }
        }
    }
}


@Composable
fun onBoard(navController: NavController, tabIndex: Int, white: Color){
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Background image
        Image(
            painter = painterResource(id = R.drawable.onboard_background),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )


        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = dp16)
            ,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            // Icon at top center
            Image(
                painter = painterResource(id = R.drawable.babyflix_full_icon),
                contentDescription = "Splash",
                modifier =  Modifier.size(228.dp, 75.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            // Text views in the center with reduced spacing
            Text(
                text = "Connect with your\nbaby before it's born",
                fontFamily = fontsFamilyPoppins,
                fontWeight = FontWeight.Bold,
                fontSize = sp28,
                textAlign = TextAlign.Center,
                color = com.babyfilx.ui.theme.white,
                lineHeight = sp30 // Adjust the line height as per your preference
            )
            Text(
                text = "Ultrasound imagery Live & On-demand",
                fontFamily = fontsFamilyPoppins,
                fontWeight = FontWeight.Normal,
                fontSize = sp16,
                textAlign = TextAlign.Center,
                color = com.babyfilx.ui.theme.white,
                lineHeight = sp20 // Adjust the line height as per your preference
            )

            Spacer(modifier = Modifier.weight(1.2f))

            // Dots for page indicators
            LazyRow(
                modifier = Modifier.padding( bottom = dp30),
                horizontalArrangement = Arrangement.Center
            ) {
                items(4) { pageIndex ->
                    val isSelected = pageIndex == tabIndex
                    DotIndicator(
                        isSelected = isSelected,
                        selected = white,
                        onClick = {

                        }
                    )
                }
            }

            // Button at the bottom
            Button(
                onClick = {
//                    navController.navigate(Screens.SignUp.root)
                          navController.navigate(Screens.Login.root)
                },

                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = dp25, end = dp25)
                    .height(dp60)
                    .align(Alignment.CenterHorizontally),
                colors = ButtonDefaults.buttonColors(backgroundColor = pink_button),
                shape = RoundedCornerShape(dp30)
                ) {
                Text(
                    text = stringResource(id = R.string.login),
                    fontFamily = fontsFamilyPoppins,
                    fontWeight = FontWeight.Bold,
                    fontSize = sp20,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(dp12))

//            Text(
//                text = "Already a member? Sign In",
//                fontFamily = fontsFamilyPoppins,
//                fontWeight = FontWeight.Normal,
//                fontSize = sp16,
//                color = Color.White,
//                modifier = Modifier
//                    .clickable {
//                        navController.navigate(Screens.Login.root)
//                    }
//            )

            Spacer(modifier = Modifier.height(dp30))

        }
    }
}


@Composable
fun onBoardFlix(
    navController: NavController,
    tabIndex: Int,
    s: String,
    s1: String,
    onboardBackground: Int,
    selectedColor: Color
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Background image
        Image(
            painter = painterResource(onboardBackground),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize().background(white)
                .padding(start = dp20, end = dp20),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Text views in the center with reduced spacing
            Text(
                text = s,
                fontFamily = fontsFamilyPoppins,
                fontWeight = FontWeight.Bold,
                fontSize = sp24,
                textAlign = TextAlign.Center,
                color = pink_button,
                lineHeight = sp30, // Adjust the line height as per your preference
                modifier = Modifier.fillMaxWidth().padding(bottom = dp10)
            )
            Text(
                text = s1,
                fontFamily = fontsFamilyPoppins,
                fontWeight = FontWeight.Normal,
                fontSize = sp16,
                textAlign = TextAlign.Center,
                color = new_black_text,
                lineHeight = sp20, // Adjust the line height as per your preference
                modifier = Modifier.fillMaxWidth().padding(bottom = dp10)
            )

            // Image in the middle
            Image(
                painter = painterResource(id = R.drawable.onboard_background),
                contentDescription = "OnBoard2",
                modifier = Modifier
                    .fillMaxSize(0.75f)
                    .padding(bottom = dp30),
                contentScale = ContentScale.FillBounds
            )

            // Dots for page indicators
            LazyRow(
                modifier = Modifier.padding(bottom = dp30),
                horizontalArrangement = Arrangement.Center
            ) {
                items(4) { pageIndex ->
                    val isSelected = pageIndex == tabIndex
                    DotIndicator(
                        isSelected = isSelected,
                        onClick = {

                        },
                        selected = selectedColor
                    )
                }
            }

            // Button at the bottom
            Button(
                onClick = {
                    navController.navigate(Screens.SignUp.root)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(dp60)
                    .align(Alignment.CenterHorizontally),
                colors = ButtonDefaults.buttonColors(backgroundColor = pink_button),
                shape = RoundedCornerShape(dp30)
            ) {
                Text(
                    text = stringResource(id = R.string.create_account),
                    fontFamily = fontsFamilyPoppins,
                    fontWeight = FontWeight.Bold,
                    fontSize = sp20,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(dp12))

            Text(
                text = "Already a member? Sign In",
                fontFamily = fontsFamilyPoppins,
                fontWeight = FontWeight.Normal,
                fontSize = sp16,
                color = new_black_text,
                modifier = Modifier
                    .clickable {
                        navController.navigate(Screens.Login.root)
                    }
            )

            Spacer(modifier = Modifier.height(dp30))
        }
    }
}

@Composable
fun DotIndicator(
    isSelected: Boolean,
    onClick: () -> Unit,
    selected: Color
) {
    val dotColor = if (isSelected) selected else Color.Gray
    val dotHeight = dp10 // You can adjust the size here
    val dotWidth = if (isSelected) dp25 else dp10
    val cornerRadius = dp12 // Rounded corners for selected dot

    Box(
        modifier = Modifier
            .height(dotHeight).width(dotWidth)
            .padding(dp2)
            .clickable {
                onClick()
            }
            .background(
                color = dotColor,
                shape = RoundedCornerShape(cornerRadius)
            )
    ) {
        // Content inside the Box (you can customize this)
    }
}

