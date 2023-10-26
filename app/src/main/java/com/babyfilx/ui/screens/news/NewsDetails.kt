package com.babyfilx.ui.screens.news

import android.annotation.SuppressLint
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.URLSpan
import android.view.View
import android.webkit.*
import android.widget.TextView
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.navigation.NavController
import com.babyfilx.data.models.Screens
import com.babyfilx.ui.screens.dashboard.DashboardViewModel
import com.babyfilx.ui.screens.home.HomeViewModel
import com.babyfilx.ui.theme.*
import com.babyfilx.utils.Constant.getDateFromLong
import com.babyfilx.utils.commonviews.*
import com.babyfilx.utils.logs.loge
import com.babyflix.mobileapp.R

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun NewsDetailsScreen(
    navController: NavController,
    viewModel: NewsViewModel,
    dashboardViewModel: DashboardViewModel,
    homeViewModel: HomeViewModel
) {
    var like by remember {
        mutableStateOf(false)
    }
    var webView: WebView? = null
    loge("Data from news details ${viewModel.states.items[viewModel.pos]}")
    val data = viewModel.states.items[viewModel.pos]

    val scrollState = rememberScrollState()
    val context = LocalContext.current
    Scaffold(modifier = Modifier.fillMaxSize(), backgroundColor = Color.White, topBar = {
        TopAppBars(
            title = stringResource(id = R.string.news_details),
            isIcons = Icons.Filled.ArrowBack,
            isDone = false
        ) {
            navController.navigateUp()
        }
    }, bottomBar = {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(dp20, bottom = dp60), verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            /*  Image(
                  painter = painterResource(id = R.drawable.avatar),
                  contentDescription = null,
                  modifier = Modifier.size(dp20),
              )
              Text(
                  text = data.userEmail,
                  modifier = Modifier
                      .padding(start = dp5)
                      .weight(0.7f),
                  color = Color.Gray,
                  textAlign = TextAlign.Start,
                  fontSize = sp12,
                  fontFamily = fontsFamily,
                  fontWeight = FontWeight.Medium
              )*/
            Likes(like = data.isLike, count = data.like.toString()) {
                if (!data.isLike) {
                    viewModel.likeAndUpdate(viewModel.pos)
                }
            }

            Shares(
                context = context, data = data.shareUrl
            )
        }

    }) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(it)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Red)
            ) {
                ImageUrlLoading(
                    url = data.blogImagePath,
                    shape = RectangleShape,
                    modifier = Modifier
                        .fillMaxWidth(),
                    height = 250.dp

                )
            }

            Card(
                shape = RoundedCornerShape(topStart = dp20, topEnd = dp20),
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (-20).dp),
                backgroundColor = Color.White

            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(top = dp20)
                ) {
                    Text(
                        text = data.blogTitle,
                        color = Color.Black,
                        textAlign = TextAlign.Start,
                        fontSize = sp24,
                        fontFamily = fontsFamily,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = dp12, end = dp12)
                    )
                    Text(
                        text = "${data.createdAt.getDateFromLong()} | Working",
                        modifier = Modifier
                            .padding(top = dp8, start = dp12, end = dp12),
                        color = Color.Gray,
                        textAlign = TextAlign.Center,
                        fontSize = sp12,
                        fontFamily = fontsFamily,
                        fontWeight = FontWeight.SemiBold
                    )

                    AndroidView(modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .padding(top = dp8, start = dp12, end = dp12),
                        factory = { context ->
                            TextView(context).apply {



                                   textSize = 16f
                                   setTextColor(ContextCompat.getColor(context, R.color.black))
                                loge("newsDetails ${data.blogBody}")
                                val htmlContent = data.blogBody
                                text = HtmlCompat.fromHtml(htmlContent, HtmlCompat.FROM_HTML_MODE_LEGACY)
                                movementMethod = LinkMovementMethod.getInstance();

                                handleUrlClicks{url->
                                    navController.navigate(
                                        "${Screens.DeepLinking.root}/${
                                            url.replace(
                                                "/",
                                                "$"
                                            )
                                        }"
                                    )
                                }
                            }
                        }, update = {web->
                           // webView = web
                        })

//                    HtmlContent(html = data.blogBody)

                    if (data.blogUrl != null)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = dp8),
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            TextButton(onClick = {
                                navController.navigate(
                                    "${Screens.DeepLinking.root}/${
                                        data.blogUrl!!.replace(
                                            "/",
                                            "$"
                                        )
                                    }"
                                )
                            }) {
                                Text(
                                    text = "Read more",
                                    color = pink_color,
                                    textAlign = TextAlign.Start,
                                    fontSize = sp16,
                                    fontFamily = fontsFamily,
                                    fontWeight = FontWeight.Medium,
                                    style = TextStyle(textDecoration = TextDecoration.Underline)
                                )
                            }

                        }
                    Column {
                        // Add other Composable elements here
//                        WebView1(url = "https://servedby.aqua-adserver.com/afr.php?zoneid=10178")
                        // on below line adding admob banner ads.
                        AdView(adId = "ca-app-pub-4273229953550397~976499825")
                    }
                }
            }
        }
    }




}

fun TextView.handleUrlClicks(onClicked: ((String) -> Unit)? = null) {
    //create span builder and replaces current text with it
    text = SpannableStringBuilder.valueOf(text).apply {
        //search for all URL spans and replace all spans with our own clickable spans
        getSpans(0, length, URLSpan::class.java).forEach {
            //add new clickable span at the same position
            setSpan(
                object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        onClicked?.invoke(it.url)
                    }
                },
                getSpanStart(it),
                getSpanEnd(it),
                Spanned.SPAN_INCLUSIVE_EXCLUSIVE
            )
            //remove old URLSpan
            removeSpan(it)
        }
    }
    //make sure movement method is set
    movementMethod = LinkMovementMethod.getInstance()
}







