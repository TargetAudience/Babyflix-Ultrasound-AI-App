package com.babyfilx.ui.screens.setting

import android.annotation.SuppressLint
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.babyfilx.BottomBarScreen
import com.babyfilx.MainViewModel
import com.babyfilx.base.App
import com.babyfilx.data.enums.DrawerContentEnum
import com.babyfilx.data.enums.SettingContentEnum
import com.babyfilx.data.models.DrawerContentModel
import com.babyfilx.data.models.Screens
import com.babyfilx.data.models.SettingContentModel
import com.babyfilx.ui.screens.freePrints.inAppSubscribe
import com.babyfilx.ui.screens.home.HomeViewModel
import com.babyfilx.ui.theme.*
import com.babyfilx.utils.commonviews.*
import com.babyfilx.utils.removeScreen
import com.babyflix.mobileapp.BuildConfig
import com.babyflix.mobileapp.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun SettingScreen(
    navController: NavController,
    viewModel: MainViewModel,
    scope: CoroutineScope,
    homeViewModel: HomeViewModel,
    onClick: (DrawerContentModel) -> Unit
) {

    val context = LocalContext.current

    val scrollState = rememberScrollState()

    val list = mutableListOf(
        SettingContentModel("News", SettingContentEnum.News),
        SettingContentModel("Profile", SettingContentEnum.Profile),
        SettingContentModel("Change Password", SettingContentEnum.ChangePassword),
        SettingContentModel("Terms & Conditions", SettingContentEnum.TermsCondition),
        SettingContentModel("Help", SettingContentEnum.Help),
    )


    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = dp60),
        backgroundColor = Color.White,
        topBar = {
            TopAppBarsWithTextOnly(
                title = stringResource(id = R.string.my_account),
                onClick = {

                })
        },
        bottomBar = {


        }) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(dp25)
        ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = dp23, bottom = dp23),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Current Date and Text "My Babyflix" on the left
                    Column(
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.spacedBy(-dp10)
                    ) {
                        Text(
                            text = App.data.name,
                            fontSize = sp13,
                            fontWeight = FontWeight.Normal,
                            fontFamily = fontsFamilyPoppins,
                            color = pink_button
                        )
                        Text(
                            text = App.data.lName,
                            fontSize = sp20,
                            fontWeight = FontWeight.Bold,
                            fontFamily = fontsFamilyPoppins,
                            color = pink_button
                        )
                    }

                    // Circular Image View (Logo) on the right
                    Image(
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = null,
                        modifier = Modifier
                            .size(dp40)
                            .clip(CircleShape) // Apply circular clipping
                    )
                }

            Box(modifier = Modifier.weight(1f)) {

                LazyColumn {
                    itemsIndexed(list) { _, item ->
                        SettingRowItems(model = item) {
                            when (it.contentModel) {
                                SettingContentEnum.News -> navController.navigate(BottomBarScreen.News.routes)
                                SettingContentEnum.Profile -> navController.navigate(Screens.Profile.root)
                                SettingContentEnum.ChangePassword -> navController.navigate(Screens.ChangePassword.root)
                                SettingContentEnum.TermsCondition -> navController.navigate("${Screens.Help.root}/${true}")
                                SettingContentEnum.Help -> navController.navigate("${Screens.Help.root}/${false}")
                                else -> {}
                            }
                        }
                    }
                    item {
//                    ButtonView(name = R.string.logout, modifier = Modifier.padding(dp20)) {
//                        onClick(DrawerContentModel("", DrawerContentEnum.Logout))
//                        viewModel.showDialog = true
//                    }
                    }
                }
            }

            if (viewModel.showDialog)
                Alert(name = stringResource(id = R.string.logout_message),
                    title = stringResource(id = R.string.logout),
                    onDismiss = { viewModel.showDialog = !viewModel.showDialog },
                    {
                        scope.launch {
                            viewModel.localDatabase.setUserType("basic")
                            viewModel.localDatabase.clear()
                            homeViewModel.all.clear()
                            homeViewModel.images.clear()
                            homeViewModel.videos.clear()
                        }
                        navController.removeScreen(Screens.Login.root)
                        navController.navigate(Screens.Login.root)
                        viewModel.showDialog = !viewModel.showDialog
                    }
                ) {

                }

            Column(
                modifier = Modifier.fillMaxWidth()
                .padding(top = dp16)
                .align(Alignment.CenterHorizontally),
            )
            {
                Image(
                    painter = painterResource(id = R.drawable.logo_babyflix),
                    contentDescription = "Splash",
                    modifier = Modifier.size(dp163, dp50)
                )
                Text(
                    text = "© 2023. All rights reserved ",
                    fontSize = sp14,
                    fontFamily = fontsFamilyPoppins,
                    fontWeight = FontWeight.Normal,
                    color = Color.Black,
                )
                Text(
                    text = "Version ${BuildConfig.VERSION_NAME}",
                    fontSize = sp14,
                    fontFamily = fontsFamilyPoppins,
                    fontWeight = FontWeight.Normal,
                    color = version_text,
                    modifier = Modifier.padding(bottom = dp33)
                )
                Button(
                    onClick = {
                        // Handle the click event for the "Logout" button
                        onClick(DrawerContentModel("", DrawerContentEnum.Logout))
                        viewModel.showDialog = true

                    },
                    colors = ButtonDefaults.buttonColors(backgroundColor = white),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(dp60)
                        .border(
                            width = dp2, color = Color.Black, shape = RoundedCornerShape(
                                dp30
                            )
                        ),
                    shape = RoundedCornerShape(dp30)
                ) {
                    Text(
                        text = "Sign Out",
                        fontSize = sp20,
                        fontFamily = fontsFamilyPoppins,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        //      modifier = Modifier.padding(top = 16.dp, bottom = 16.dp, start = 57.dp, end = 56.dp)
                    )
                }
            }
        }
    }
}

