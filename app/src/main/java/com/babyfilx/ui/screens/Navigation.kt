package com.babyfilx.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.babyfilx.MainViewModel
import com.babyfilx.data.models.Screens
import com.babyfilx.data.repositories.SelectPlanRepository
import com.babyfilx.ui.screens.buyprints.BuyPrintsVIewModel
import com.babyfilx.ui.screens.buyprints.FotomotoScreen
import com.babyfilx.ui.screens.changePassword.ChangePasswordScreen
import com.babyfilx.ui.screens.dashboard.DashboardScreen
import com.babyfilx.ui.screens.dashboard.DashboardViewModel
import com.babyfilx.ui.screens.deeplinking.URlForBabyFilx
import com.babyfilx.ui.screens.forgot.ForgotPassword
import com.babyfilx.ui.screens.help.Help
import com.babyfilx.ui.screens.home.HomeScreen
import com.babyfilx.ui.screens.home.HomeViewModel
import com.babyfilx.ui.screens.imageEnhancement.*
import com.babyfilx.ui.screens.imageSelection.ImageSelectionScreen
import com.babyfilx.ui.screens.imagedetail.ImageDetails
import com.babyfilx.ui.screens.login.LoginScreen
import com.babyfilx.ui.screens.login.OnBoardScreen
import com.babyfilx.ui.screens.news.NewsDetailsScreen
import com.babyfilx.ui.screens.news.NewsScreen
import com.babyfilx.ui.screens.news.NewsViewModel
import com.babyfilx.ui.screens.player.VideoPLayerScreen
import com.babyfilx.ui.screens.profile.ProfileScreen
import com.babyfilx.ui.screens.reset.ResetPasswordScreen
import com.babyfilx.ui.screens.selectPlan.SelectPlanVIewModel
import com.babyfilx.ui.screens.setting.SettingScreen
import com.babyfilx.ui.screens.splash.SplashScreen
import com.babyfilx.utils.commonviews.LoginVertical


@Composable
fun Navigation(
    modifier: Modifier = Modifier,
    navController: NavHostController
) {
    var viewModel: HomeViewModel? = null
    var buyPrintsVIewModel: BuyPrintsVIewModel? = null
    var newsViewModel: NewsViewModel? = null
    var selectImagesViewModel: SelectImagesViewModel? = null
    var selectplanViewModel: SelectPlanVIewModel? = null
    var dashboardViewModel: DashboardViewModel? = null

//
//    NavHost(modifier = modifier, navController = navController, startDestination = Screens.Splash.root) {
//        composable(Screens.Splash.root) {
//            SplashScreen(navController = navController)
//        }
//
//        composable(Screens.Login.root) {
//            LoginVertical {
//                LoginScreen(navController = navController)
//            }
//        }
//
//        composable(Screens.OnBoard.root) {
//            LoginVertical {
//                OnBoardScreen(navController = navController)
//            }
//        }
//
//        composable(Screens.Home.root) {
//            viewModel = hiltViewModel()
//            viewModel?.let {
//                HomeScreen(navController = navController, it)
//            }
//        }
//        composable(Screens.ForgotPassword.root) {
//            ForgotPassword(navController = navController)
//        }
//        composable(Screens.Setting.root) {
//            SettingScreen(navController = navController)
//        }
//        composable(Screens.SelectPlan.root) {
//            selectplanViewModel = hiltViewModel()
//            SelectPlanScreen(navController = navController, selectplanViewModel?: hiltViewModel())
//        }
//        composable(Screens.EnhancedImage.root) {
//            viewModel = hiltViewModel()
//            EnhancedImageScreen(navController = navController, viewModel?: hiltViewModel())
//        }
//        composable(Screens.News.root) {
//            newsViewModel = hiltViewModel()
//            NewsScreen(navController = navController, newsViewModel ?: hiltViewModel())
//        }
//        composable(Screens.NewsDetails.root) {
//            NewsDetailsScreen(navController = navController, newsViewModel ?: hiltViewModel(), dashboardViewModel ?: hiltViewModel())
//        }
//        composable(Screens.Profile.root) {
//            ProfileScreen(navController = navController)
//        }
//        composable(Screens.ChangePassword.root) {
//            ChangePasswordScreen(navController = navController)
//        }
//        composable(Screens.Details.root) {
//            viewModel?.let {
//                ImageDetails(navController = navController, it)
//            }
//        }
//        composable(
//            "${Screens.Help.root}/{value}",
//            arguments = listOf(navArgument("value") { type = NavType.BoolType })
//        ) {
//            Help(navController = navController)
//        }
//
//        composable(Screens.Reset.root) {
//            ResetPasswordScreen(navController = navController)
//        }
//
//        composable(Screens.VideoDetails.root) {
//            viewModel?.let {
//                VideoPLayerScreen(navController = navController, it)
//            }
//        }
//
//        composable(
//            "${Screens.DeepLinking.root}/{value}",
//            arguments = listOf(navArgument("value") {
//                type = NavType.StringType
//                defaultValue = ""
//            })
//        ) {
//
//            URlForBabyFilx(navController = navController)
//        }
//    }
}