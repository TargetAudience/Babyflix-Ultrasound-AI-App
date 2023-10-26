package com.babyfilx.ui.screens.imageEnhancement

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.babyfilx.ui.theme.*
import com.babyfilx.utils.commonviews.ImageUrlLoading
import com.babyfilx.utils.commonviews.ProgressBar
import com.babyfilx.utils.commonviews.TopAppBarsWithTextOnly
import com.babyflix.mobileapp.R
import kotlinx.coroutines.delay

@Preview
@Composable
fun ProcessingImageUi() {

     var isVisible by remember { mutableStateOf(true) }

        val alpha by animateFloatAsState(
            targetValue = if (isVisible) 1f else 0f,
            animationSpec = tween(durationMillis = 1000)
        )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                imageWithLoader()

                Spacer(modifier = Modifier.height(dp20))

                Text(
                    text = buildAnnotatedString {
                        withStyle(
                            style = SpanStyle(
                                fontSize = sp22,
                                fontFamily = fontsFamilyPoppins,
                                fontWeight = FontWeight.Bold,
                            )
                        ) {
                            append("Image submitted for\n")
                            append("enhancement.")
                        }
                    },
                    maxLines = 2,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(dp20))

                Box(
                    modifier = Modifier
                        .size(200.dp, 50.dp)
                        .background(Color.White)
                        , contentAlignment = Alignment.Center
                ) {
                    if (isVisible) {
                        Text(
                            text = "Please wait...",
                            fontFamily = fontsFamilyPoppins,
                            fontWeight = FontWeight.Normal,
                            fontSize = sp16,
                            modifier = Modifier.alpha(alpha)
                        )
                    }
                }

                LaunchedEffect(isVisible) {
                    delay(1000) // Adjust the delay as needed
                    isVisible = !isVisible
                }
            }
    }
}



@Composable
fun imageWithLoader(
    modifier: Modifier = Modifier
){
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.magic_wand_black),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = modifier
                .size(dp48).padding(dp10)
        )
        ProgressBar(
            modifier = modifier.size(dp48)
        )
    }
}