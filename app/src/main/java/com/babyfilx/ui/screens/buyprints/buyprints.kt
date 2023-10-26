package com.babyfilx.ui.screens.buyprints

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import androidx.navigation.NavController
import com.babyfilx.data.models.Screens
import com.babyfilx.ui.screens.home.HomeViewModel
import com.babyfilx.ui.theme.pink_color
import com.babyfilx.utils.commonviews.ImageUrlLoading
import com.babyfilx.utils.commonviews.StringTextContent
import com.babyflix.mobileapp.R


@Composable
fun BuyPrintsScreen(navController: NavController, viewModel: HomeViewModel) {

    val scrollState = rememberScrollState()
    val state = viewModel.images

    Column(
        modifier = Modifier
    ) {

        ImageUrlLoading(
            url = "https://content4.babyflix.net/p/103/thumbnail/entry_id/0_8dxgia4e/width/200/height/300/type/1/quality/100",
            shape = RoundedCornerShape(0.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
            /*      .pointerInput(Unit) {
                      detectTransformGestures { _, _, zoom, _ ->
                          scale *= zoom
                      }

                  }
                  .graphicsLayer {
                      scaleX = maxOf(.5f, minOf(3f,scale))
                      scaleY = maxOf(.5f, minOf(3f,scale))
                  }*/,
            contentScale = ContentScale.FillWidth
        )
        StringTextContent(
            modifier = Modifier.clickable {
//                navController.navigate("fotomoto")
//                val url =
//                    "<script type=\"text/javascript\" src=\"//widget.fotomoto.com/stores/script/c00dfc1b8f839e9f6de03cfa6d4e3f396fabaa30.js\"></script><img src=\"https://content4.babyflix.net/p/103/thumbnail/entry_id/0_j5qdm837/width/200/height/300/type/1/quality/100\" class=\"fotomoto-item\"/><noscript>If Javascript is disabled in your browser, to place orders please visit the page where I <a href='https://my.fotomoto.com/store/c00dfc1b8f839e9f6de03cfa6d4e3f396fabaa30'>sell my photos</a>, powered by <a href='https://my.fotomoto.com'>Fotomoto</a>.</noscript>"
//                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
//                startActivity(intent)
                navController.navigate(Screens.FotoMotoWeb.root)
            }.fillMaxWidth(),
            message = stringResource(id = R.string.buy_photo),
            color = pink_color,
            textAlign = TextAlign.Center
        )

//        BuyPrintsCardView(viewModel)

    }

    /*  when(response.value){
          is Response.Error -> {}
          is Response.Loading ->
          is Response.Success -> {}
          else -> {}
      }*/


}


