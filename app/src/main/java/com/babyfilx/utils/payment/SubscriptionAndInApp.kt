package com.babyfilx.utils.payment

import android.content.Context
import com.android.billingclient.api.*
import com.babyfilx.ui.screens.selectPlan.SelectPlanVIewModel
import com.babyfilx.utils.findActivity
import com.babyfilx.utils.logs.loge
import com.babyfilx.utils.shareData
import com.google.common.collect.ImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL


fun Context.inAppPurchase(index: Int, function: (String) -> Unit) {
     val purchasesUpdatedListener =
        PurchasesUpdatedListener { billingResult, purchases ->
            // To be implemented in a later section.

            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
                for (purchase in purchases) {

                    loge("Purchase result ${purchase.purchaseToken}  Order Id ${purchase.orderId}")
//                    findActivity()?.shareData(purchase.purchaseToken)
//                    function(purchase.purchaseToken)
//                    handlePurchase(purchase)
                    function(purchase.purchaseToken)
                }
            }else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
                // Handle an error caused by a user cancelling the purchase flow.
                loge("ansfkjdnjkfds ${billingResult.debugMessage}")
            } else {
                // Handle any other error codes.
                loge("ansfkjdnjkfds ${billingResult.debugMessage}")

            }
        }

     val billingClient = BillingClient.newBuilder(this)
        .setListener(purchasesUpdatedListener)
        .enablePendingPurchases()
        .build()

    billingClient.startConnection(object : BillingClientStateListener {
        override fun onBillingSetupFinished(billingResult: BillingResult) {
            if (billingResult.responseCode ==  BillingClient.BillingResponseCode.OK) {
                // The BillingClient is ready. You can query purchases here.
                getProductForBilling(billingClient,index)
            }
        }
        override fun onBillingServiceDisconnected() {
            // Try to restart the connection on the next request to
            // Google Play by calling the startConnection() method.
            inAppPurchase(index,function)
        }
    })



}


fun Context.getProductForBilling(billingClient: BillingClient, index: Int) {
    val queryProductDetailsParams =
        QueryProductDetailsParams.newBuilder()
            .setProductList(
                ImmutableList.of(
                    QueryProductDetailsParams.Product.newBuilder()
                        .setProductId("ai_subscription")
                        .setProductType(BillingClient.ProductType.SUBS)
                        .build()))
            .build()

    billingClient.queryProductDetailsAsync(queryProductDetailsParams) {
            billingResult,
            productDetailsList ->
        // check billingResult
        // process returned productDetailsList

      processPurchases(billingClient,productDetailsList,index)
    }

}


 fun Context.processPurchases(
     billingClient: BillingClient,
     productDetailsList: MutableList<ProductDetails>,
     index: Int
 ) {

     loge("Test app in sub ${productDetailsList.size}")


     val context = this
     CoroutineScope(Dispatchers.IO).launch {
         val productDetailsParamsList = listOf(
             BillingFlowParams.ProductDetailsParams.newBuilder()
                 // retrieve a value for "productDetails" by calling queryProductDetailsAsync()
                 .setProductDetails(productDetailsList[0])
                 // to get an offer token, call ProductDetails.subscriptionOfferDetails()
                 // for a list of offers that are available to the user
                  .setOfferToken(productDetailsList[0].subscriptionOfferDetails!![index].offerToken)
                 .build()
         )
         val billingFlowParams = BillingFlowParams.newBuilder()
             .setProductDetailsParamsList(productDetailsParamsList)
             .build()

// Launch the billing flow
         val billingResult =
             billingClient.launchBillingFlow(context.findActivity()!!, billingFlowParams)
         // Process the result.
         loge("ansfkjdnjkfds 111 ${billingResult.debugMessage}")
         loge("ansfkjdnjkfds 111 ${billingResult.responseCode}")
     }
}