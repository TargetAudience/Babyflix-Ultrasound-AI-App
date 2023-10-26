package com.babyfilx.ui.screens.selectPlan

import android.content.Context
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.android.billingclient.api.*
import com.babyfilx.api.apistate.Response
import com.babyfilx.data.models.Screens
import com.babyfilx.data.models.response.CommanModel
import com.babyfilx.data.models.response.SelectImageResponse
import com.babyfilx.data.models.response.UpgradeUserModel
import com.babyfilx.data.repositories.SelectPlanRepository
import com.babyfilx.ui.screens.imageEnhancement.Plan
import com.babyfilx.utils.extentions.toast
import com.babyfilx.utils.logs.loge
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SelectPlanVIewModel @Inject constructor(
    private val context: Context,
    private val repository: SelectPlanRepository
):  ViewModel(){

    private val _plans = MutableStateFlow<List<Plan>>(emptyList())
    val plans: StateFlow<List<Plan>> = _plans
    var message by mutableStateOf("")
    private val apiResponse = Channel<Response<UpgradeUserModel>>()
    val response = apiResponse.receiveAsFlow()
    var navController: NavController? = null
    // Add a boolean state variable to track the upgrade progress
    val _isUpgrading = mutableStateOf(false)
    val isUpgrading: State<Boolean> = _isUpgrading
    val newPlans = mutableListOf<Plan>()


    private val purchasesUpdatedListener =
        PurchasesUpdatedListener { billingResult, purchases ->

        }

    private val billingClient = BillingClient.newBuilder(context)
        .setListener(purchasesUpdatedListener)
        .enablePendingPurchases()
        .build()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            getPriceList()
        }
    }

    fun getPriceList() {
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    // The BillingClient is ready. You can query purchases here.
                    getProduct(billingClient)
                }
            }

            override fun onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
                viewModelScope.launch(Dispatchers.IO) {
                    getPriceList()
                }
            }
        })
    }

    fun getProduct(billingClient: BillingClient) {
        val queryProductDetailsParams = QueryProductDetailsParams.newBuilder()
            .setProductList(
                listOf(
                    QueryProductDetailsParams.Product.newBuilder()
                        .setProductId("ai_subscription")
                        .setProductType(BillingClient.SkuType.SUBS)
                        .build()
                )
            )
            .build()

        billingClient.queryProductDetailsAsync(queryProductDetailsParams) { billingResult, productDetailsList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                loge("product : "+productDetailsList)
                productDetailsList[0].subscriptionOfferDetails?.forEachIndexed { index, offerDetail ->
                    val offerTag = offerDetail.offerTags[0]
                    val formattedPrice = offerDetail.pricingPhases.pricingPhaseList[0].formattedPrice
                    val plan = Plan(offerTag, formattedPrice, index)
                    newPlans.add(plan)
                }
                _plans.value = newPlans
            }
        }
    }

    fun endConnection() {
        billingClient.endConnection()
    }


    fun upgradeUser(userId: String,plan: String , planType: String, purchaseToken: String , productId: String) {
        // Send Response.Loading() on the main thread

        viewModelScope.launch(Dispatchers.IO) {
            // Set the state to indicate that upgrade is in progress
            _isUpgrading.value = true

            apiResponse.send(Response.Loading())
            try {
                val response = repository.upgradeUserToPremium(userId , plan , planType , purchaseToken, productId).catch {
                    message = it.message.toString()
                    // Send the error response on the main thread
                    withContext(Dispatchers.Main) {
//                        Log.e("Testing Payment", "Error : $it")
                        apiResponse.send(Response.Error(errorMessage = it.message.toString()))
                    }
                }.single() // Use single() instead of collectLatest to get the final value

                message = response.message.toString()

                // Send the success response on the main thread
                withContext(Dispatchers.Main) {
//                    Log.i("Testing Payment", "Success : $response")
                    apiResponse.send(Response.Success(response))
                    _isUpgrading.value = false
                }
            } catch (e: Exception) {
                // Send the error response on the main thread
                withContext(Dispatchers.Main) {
                    message = e.message.toString()
//                    Log.e("Testing Payment", "Exception : $e")
                    apiResponse.send(Response.Error(errorMessage = e.message.toString()))
                    // Reset the state after the upgrade is done
                    _isUpgrading.value = false
                }
            }
        }
    }





}

