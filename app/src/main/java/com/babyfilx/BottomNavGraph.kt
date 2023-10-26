package com.babyfilx

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.babyfilx.data.models.Screens
import com.babyfilx.ui.screens.buyprints.FotomotoScreen
import com.babyfilx.ui.screens.changePassword.ChangePasswordScreen
import com.babyfilx.ui.screens.createaccount.*
import com.babyfilx.ui.screens.dashboard.DashboardScreen
import com.babyfilx.ui.screens.dashboard.DashboardViewModel
import com.babyfilx.ui.screens.deeplinking.URlForBabyFilx
import com.babyfilx.ui.screens.forgot.ForgotPassword
import com.babyfilx.ui.screens.freePrints.Flix10k
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
import com.babyfilx.utils.logs.loge

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun BottomNavGraph(navController: NavHostController, modifier: Modifier) {

    var viewModel: HomeViewModel = hiltViewModel()
    var mainViewModel: MainViewModel? = null
    var newsViewModel: NewsViewModel = hiltViewModel()
    var dashboardViewModel: DashboardViewModel? = null
    var selectImagesViewModel: SelectImagesViewModel = hiltViewModel()
    var selectplanViewModel: SelectPlanVIewModel? = null
    val scope = rememberCoroutineScope()
    val uri = "babyfilx://notification"

    NavHost(
        navController = navController,
        startDestination = Screens.Splash.root
    ) {
        composable(Screens.Splash.root) {
            SplashScreen(navController = navController)
        }
        navigation(
            route = Screens.BottomBar.root,
            startDestination = BottomBarScreen.Dashboard.routes
        ) {
            composable(BottomBarScreen.Dashboard.routes) {
                DashboardScreen(
                    navController = navController,
                    viewModel = dashboardViewModel ?: hiltViewModel(),
                    newsViewModel = newsViewModel ?: hiltViewModel(),
                    homeViewModel = viewModel
                )
            }
            composable(route = BottomBarScreen.Home.routes) {
                HomeScreen(navController = navController, viewModel)
            }
            composable(route = BottomBarScreen.FlixCam.routes) {
//                NewsScreen(navController = navController, newsViewModel ?: hiltViewModel())
            }
            composable(route = BottomBarScreen.More.routes) {
                SettingScreen(
                    navController = navController,
                    viewModel = mainViewModel ?: hiltViewModel(),
                    scope = scope,
                    homeViewModel = viewModel
                ) {

                }
            }
            composable(route = BottomBarScreen.Flix10KSubscription.routes) {
//                NewsScreen(navController = navController, newsViewModel ?: hiltViewModel())
                Flix10k(navController = navController, selectplanViewModel ?: hiltViewModel())
//                ImageEnhancementScreen(navController = navController)
            }
            composable(route = BottomBarScreen.ExperienceFlix10K.routes) {
                ExperienceFlix10KScreen(navController = navController, selectImagesViewModel)
            }
            composable(route = BottomBarScreen.ImageSelection.routes) {
//                NewsScreen(navController = navController, newsViewModel ?: hiltViewModel())
                ImageSelectionScreen(navController = navController, selectImagesViewModel)
//                ImageEnhancementScreen(navController = navController)
            }
            composable(BottomBarScreen.News.routes) {
                NewsScreen(navController = navController, newsViewModel ?: hiltViewModel())
            }
            composable(BottomBarScreen.NewsDetails.routes) {
                    NewsDetailsScreen(
                        navController = navController,
                        newsViewModel,
                        dashboardViewModel ?: hiltViewModel(),
                        viewModel ?: hiltViewModel()
                    )
            }
//            composable(
//                route = "babyflix://${BottomBarScreen.EnhancementComplete.routes}",
//                arguments = listOf(navArgument("imageUrl") {
//                    type = NavType.StringType
//                }),
//                deepLinks = listOf(navDeepLink { uriPattern = "babyflix://enhancement_complete/imageUrl=}" })
//            ) { backStackEntry ->
//                val imageUrl = backStackEntry.arguments?.getString("imageUrl")
//
//                // Use the imageUrl to load and display the EnhancementCompleteUi screen
//                EnhancementCompleteUi(navController = navController, imageUrl!!, selectImagesViewModel)
//            }
            composable(BottomBarScreen.EnhancementComplete.routes) {
                EnhancementCompleteUi(navController = navController, "" ,selectImagesViewModel)
            }
        }
        composable(Screens.Login.root) {
            LoginVertical {
                LoginScreen(navController = navController)
            }
        }
        composable(Screens.OnBoard.root) {
            LoginVertical {
                OnBoardScreen(navController = navController)
            }
        }
        composable(Screens.ProcessingImage.root) {
            ProcessingImageUi()
        }
        composable(Screens.ForgotPassword.root) {
            ForgotPassword(navController = navController)
        }
        composable(Screens.Subscription.root) {
            viewModel = hiltViewModel()
            ImageEnhancementScreen(navController = navController)
        }
        composable(Screens.SelectPlan.root) {
            selectplanViewModel = hiltViewModel()
            SelectPlanScreen(navController = navController, selectplanViewModel ?: hiltViewModel())
        }
        composable(Screens.EnhancedImage.root) {
            viewModel = hiltViewModel()
            EnhancedImageScreen(navController = navController, viewModel ?: hiltViewModel())
        }
//        composable(Screens.ImageSelection.root) {
//            selectImagesViewModel = hiltViewModel()
//            ImageSelectionScreen(navController = navController, selectImagesViewModel?: hiltViewModel())
//        }
        composable(Screens.FotoMotoWeb.root) {
            FotomotoScreen(navController = navController)
        }
//        composable(Screens.News.root) {
//            NewsScreen(navController = navController, newsViewModel )
//        }
//        composable(Screens.NewsDetails.root) {
//            NewsDetailsScreen(navController = navController, newsViewModel , dashboardViewModel ?: hiltViewModel(), viewModel ?: hiltViewModel())
//        }
        composable(Screens.Profile.root) {
            ProfileScreen(navController = navController)
        }
        composable(Screens.ChangePassword.root) {
            ChangePasswordScreen(navController = navController)
        }
        composable(Screens.Details.root) {
            ImageDetails(navController = navController, viewModel)
        }
        composable(
            "${Screens.Help.root}/{value}",
            arguments = listOf(navArgument("value") { type = NavType.BoolType })
        ) {
            Help(navController = navController)
        }

        composable(Screens.Reset.root) {
            ResetPasswordScreen(navController = navController)
        }

        composable(Screens.VideoDetails.root) {
            viewModel?.let {
                VideoPLayerScreen(navController = navController, it)
            }
        }

        composable(
            "${Screens.DeepLinking.root}/{value}",
            arguments = listOf(navArgument("value") {
                type = NavType.StringType
                defaultValue = ""
            })
        ) {

            URlForBabyFilx(navController = navController)
        }

        composable(Screens.SignUp.root) {
            LoginVertical {
                SignUpScreen(navController = navController)
            }
        }

        composable(Screens.SignUpEmail.root) {
            LoginVertical {
                SignUpEmailScreen(navController = navController)
            }
        }

        composable(Screens.SignUpMobile.root) {
            LoginVertical {
                SignUpMobileScreen(navController = navController)
            }
        }

        composable(Screens.SignUpPassword.root) {
            LoginVertical {
                SignUpPasswordScreen(navController = navController)
            }
        }

        composable(Screens.SignUpClinic.root) {
            LoginVertical {
                SignUpClinicScreen(navController = navController)
            }
        }


    }
}


