package com.babyfilx.ui.screens.news

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.babyfilx.BottomBarScreen
import com.babyfilx.data.models.FormEvent
import com.babyfilx.data.models.Screens
import com.babyfilx.ui.theme.*
import com.babyfilx.utils.commonviews.*
import com.babyflix.mobileapp.R

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun NewsScreen(navController: NavController, viewModel: NewsViewModel) {

    val scrollState = rememberScrollState()
    val state = viewModel.state
    val response = viewModel.apiResponse1.collectAsState(initial = null)

    Scaffold(modifier = Modifier.fillMaxSize(), backgroundColor = Color.White, topBar = {
        TopAppBars(
            modifier = Modifier,
            title = "News",
            isIcons = Icons.Filled.ArrowBack,
            isDone = false
        ) {
            navController.navigateUp()
        }}){
        Column(
            modifier = Modifier
        ) {

            SearchTextView(
                value = state.searchNews,
                hint = R.string.search,
                errorMessage = state.searchNewsError,
                modifier = Modifier.padding(top = dp23),
            ) {
                viewModel.formEvent(FormEvent.SearchNews(it))
            }

            CustomListView(viewModel)

            NewsCardView(viewModel,
                onClick = {
                    viewModel.pos = it
                    navController.navigate(BottomBarScreen.NewsDetails.routes)
                }
            )

//            Spacer(modifier = Modifier.height(dp60))

        }
    }

    /*  when(response.value){
          is Response.Error -> {}
          is Response.Loading ->
          is Response.Success -> {}
          else -> {}
      }*/


}


