package com.babyfilx.ui.screens.imageEnhancement

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.babyfilx.api.apiinterface.APIS
import com.babyfilx.api.apistate.Response
import com.babyfilx.base.App
import com.babyfilx.data.models.Screens
import com.babyfilx.data.models.response.UpgradeUserModel
import com.babyfilx.data.repositories.SelectPlanRepository
import com.babyfilx.ui.screens.selectPlan.SelectPlanVIewModel
import com.babyfilx.ui.screens.selectPlan.SelectPlanViewModelFactory
import com.babyfilx.ui.theme.*
import com.babyfilx.utils.commonviews.ProgressBar
import com.babyfilx.utils.commonviews.TopAppBars
import com.babyfilx.utils.extentions.toast
import com.babyfilx.utils.findActivity
import com.babyfilx.utils.payment.inAppPurchase
import com.babyfilx.utils.shareData
import dagger.hilt.android.internal.managers.FragmentComponentManager.findActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*


@SuppressLint("UnusedMaterialScaffoldPaddingParameter", "SuspiciousIndentation")
@Composable
fun SelectPlanScreen(
    navController: NavController,
    viewModel: SelectPlanVIewModel
) {

//    val viewModel: SelectPlanVIewModel = viewModel()
    val context = LocalContext.current

    val plans by viewModel.plans.collectAsState()

    val responseState by viewModel.response.collectAsState(initial = Response.Loading())


    DisposableEffect(Unit) {
        onDispose {
            viewModel.endConnection() // Call the method in the ViewModel when disposed
        }
    }

    LaunchedEffect(Unit) {
        viewModel.getPriceList()
    }

    var selectedPlan by remember { mutableStateOf(plans.getOrNull(0)) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        backgroundColor = Color.White,
        topBar = {
            TopAppBars(
                title = "Select Plan",
                isIcons = Icons.Filled.ArrowBack,
                isDone = false
            ) {
                navController.navigateUp()
            }
        }
    ) {
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)) {
            // Show the progress bar when upgrading
            if (viewModel.isUpgrading.value) {
                ProgressBar(modifier = Modifier.fillMaxSize())
            }
            Column(modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()) {
                plans.forEachIndexed { index, plan ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedPlan = plan }
                            .border(
                                width = 2.dp,
                                color = if (selectedPlan == plan) pink_dark else grey_border,
                                shape = RoundedCornerShape(4.dp)
                            )
                            .padding(start = dp16, end = dp16)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = plan.name.uppercase(Locale.ROOT),
                                    style = TextStyle(fontSize = sp14, color = grey_plan_text),
                                    fontFamily = fontsFamily,
                                    fontWeight = FontWeight.Normal,
                                    modifier = Modifier.padding(top = dp17)
                                )
                                Text(
                                    text = plan.price,
                                    style = TextStyle(fontSize = sp20),
                                    fontFamily = fontsFamily,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(top = dp13)
                                )
                                Text(
                                    text = "Pay ${plan.price} every ${plan.name.lowercase(Locale.ROOT)}",
                                    style = TextStyle(fontSize = sp14, color = grey_plan_text),
                                    fontFamily = fontsFamily,
                                    fontWeight = FontWeight.Normal,
                                    modifier = Modifier.padding(top = dp15, bottom = dp15)
                                )
                            }
                            RadioButton(
                                selected = selectedPlan == plan,
                                onClick = null, // Disable radio button interaction
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = pink_dark
                                ),
                                modifier = Modifier.padding(start = dp8)
                            )
                        }
                    }
                    // Add space between plans except for the last plan
                    if (index < plans.size - 1) {
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
            selectedPlan?.price?.let { it1 ->
                Button(
                    onClick = {
                        selectedPlan?.let { plan ->
                            context.inAppPurchase(plan.index) { purchaseToken ->
//                                Log.i("Testing Payment", "Token1 : $purchaseToken")
                                    if (purchaseToken.isNotEmpty()) {
                                        // Purchase was successful, upgrade the user
//                                        findActivity(context).shareData(purchaseToken)
//                                        Log.i("Testing Payment", "Token2 : $purchaseToken")
                                        viewModel.upgradeUser(
                                            App.data.id,
                                            "premium",
                                            selectedPlan!!.name.toLowerCase(),
                                            purchaseToken,
                                            "ai_subscription"
                                        )
//                                        findActivity(context).shareData(purchaseToken)
                                    } else {
//                                        context.toast(purchaseToken)
//                                        // Handle failed purchase gracefully (optional)
//                                        // You can display a message to the user or take appropriate action
                                    }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .align(Alignment.BottomCenter),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = pink_dark,
                        contentColor = white
                    ),
                    enabled = it1.isNotEmpty()
                ) {
                    Text(
                        text = selectedPlan?.let { "Pay ${it.price}" } ?: "",
                        fontFamily = fontsFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = sp16
                    )
                }
            }
        }
        // Handle API response and navigation
        LaunchedEffect(responseState){
            when (responseState) {
                is Response.Success -> {
                    val response = responseState.data // Assuming 'data' holds the API response object
//                    Log.i("Testing Payment", "Response : $response")
//                findActivity(context).shareData(response.toString())
                    if (response?.code == "200") {
//                        Log.i("Testing Payment", "Response200 : $response")
                        // Display toast with success message
                        context.toast(response.message.toString())

                        // Navigate to the ImageSelection screen
                        navController.navigate(Screens.ImageSelection.root) {
                            popUpTo(Screens.Home.root) { inclusive = true }
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
    }
}

data class Plan(val name: String, val price: String, val index: Int)
