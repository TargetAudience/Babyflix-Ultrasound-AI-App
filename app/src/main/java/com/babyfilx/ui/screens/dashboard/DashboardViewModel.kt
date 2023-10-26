package com.babyfilx.ui.screens.dashboard


import android.content.Context
import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.babyfilx.api.apistate.Response
import com.babyfilx.base.App.Companion.isFirst
import com.babyfilx.data.enums.BottomSheetEnum
import com.babyfilx.data.enums.HomeEnum
import com.babyfilx.data.models.DetailsModel
import com.babyfilx.data.models.ScreenState
import com.babyfilx.data.models.response.*
import com.babyfilx.data.repositories.BlogRepository
import com.babyfilx.data.repositories.HomeRepository
import com.babyfilx.data.repositories.SelectImageRepository
import com.babyfilx.data.repositories.SelectPlanRepository
import com.babyfilx.ui.screens.imageEnhancement.Plan
import com.babyfilx.ui.screens.news.NewsViewModel
import com.babyfilx.utils.HomeUiEvent
import com.babyfilx.utils.exception.NoInternetException
import com.babyfilx.utils.logs.loge
import com.babyflix.mobileapp.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val repository: SelectImageRepository,
    private val repository1: BlogRepository,
    private val context: Context,
    private val repository3: SelectPlanRepository,
    private val homeRepository: HomeRepository
) : ViewModel() {

    var refreshing by mutableStateOf(false)
    private val _plans = MutableStateFlow<List<Plan>>(emptyList())
    val plans: StateFlow<List<Plan>> = _plans
    var message3 by mutableStateOf("")
    private val apiResponse3 = Channel<Response<UpgradeUserModel>>()
    val response3 = apiResponse3.receiveAsFlow()
    var navController: NavController? = null
    var locationModel by mutableStateOf(LocationModel())
    // Add a boolean state variable to track the upgrade progress
    val _isUpgrading = mutableStateOf(false)
    val isUpgrading: State<Boolean> = _isUpgrading
    val newPlans = mutableListOf<Plan>()


    val recentScansFlow = MutableSharedFlow<List<BlogResponse>>()

    var states by mutableStateOf(ScreenState())


    val recentList: List<RecentScan> by lazy {
        // Populate this list with your dummy data for recent scans
        listOf(
            RecentScan("Rihanna Ultrasound", "1:02 PM | August 12, 2023"),
            RecentScan("Another Scan", "2:30 PM | August 13, 2023"),
            // Add more items as needed
        )
    }

    private val apiResponse = Channel<Response<Any>>()
    val response = apiResponse.receiveAsFlow()

    private val apiResponse2 = Channel<Response<Any>>()
    val response2 = apiResponse2.receiveAsFlow()

    var message by mutableStateOf("")

    // Store the list of images
    private val _images = mutableStateListOf<HomeEntriesModel>()
    val images: List<HomeEntriesModel> get() = _images

    var mainMessage by mutableStateOf("")

    // Track the loading state
    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    fun setLoadingState(isLoading: Boolean) {
        _isLoading.value = isLoading
    }

    private val _items = mutableStateListOf<BlogResponse>()
    val items: List<BlogResponse> get() = _items


    init {
        getLocationAPI()
        recentScans()
//        recentBlogs()
    }

    fun getLocationAPI() {
        try {
            viewModelScope.launch {
                homeRepository.getLocation().catch {}.collectLatest {
                    loge("LocationModel $it")
                    locationModel = it
                }
            }
        }catch (e: NoInternetException){
            loge("No internet connection")
        }
    }

     fun recentBlogs() {
        viewModelScope.launch {
            repository1.blogApiForNews(1, 5,"",false,"0").catch {
                mainMessage = it.message.toString()
                apiResponse.send(Response.Error(errorMessage = it.message.toString()))
            }.collect {
                apiResponse.send(Response.Success(it))
                isFirst = false
                _items.addAll(it)
                states = states.copy(items = it)
                recentScansFlow.emit(it)

            }
        }

    }

     fun recentScans() {
        viewModelScope.launch {
            loge("final call 1")
            apiResponse.send(Response.Loading())
            loge("final call 2")
            repository.galleryApiForRecentScans().catch {
                mainMessage = it.message.toString()
                refreshing = false
                apiResponse.send(Response.Error(errorMessage = it.message.toString()))
            }.collect { it ->
                apiResponse.send(Response.Success(it))
                isFirst = false
                refreshing = false
                _images.addAll(it as Collection<HomeEntriesModel>)
                loge("final call 3"+it)
            }
        }
    }

    fun upgradeUser(userId: String,plan: String , planType: String, purchaseToken: String , productId: String) {
        // Send Response.Loading() on the main thread

        viewModelScope.launch(Dispatchers.IO) {
            // Set the state to indicate that upgrade is in progress
            _isUpgrading.value = true

            apiResponse3.send(Response.Loading())
            try {
                val response = repository3.upgradeUserToPremium(userId , plan , planType , purchaseToken, productId).catch {
                    message3 = it.message.toString()
                    // Send the error response on the main thread
                    withContext(Dispatchers.Main) {
//                        Log.e("Testing Payment", "Error : $it")
                        apiResponse3.send(Response.Error(errorMessage = it.message.toString()))
                    }
                }.single() // Use single() instead of collectLatest to get the final value

                message3 = response.message.toString()

                // Send the success response on the main thread
                withContext(Dispatchers.Main) {
//                    Log.i("Testing Payment", "Success : $response")
                    apiResponse3.send(Response.Success(response))
                    _isUpgrading.value = false
                }
            } catch (e: Exception) {
                // Send the error response on the main thread
                withContext(Dispatchers.Main) {
                    message = e.message.toString()
//                    Log.e("Testing Payment", "Exception : $e")
                    apiResponse3.send(Response.Error(errorMessage = e.message.toString()))
                    // Reset the state after the upgrade is done
                    _isUpgrading.value = false
                }
            }
        }
    }


}




data class RecentScan(val title: String, val timestamp: String)