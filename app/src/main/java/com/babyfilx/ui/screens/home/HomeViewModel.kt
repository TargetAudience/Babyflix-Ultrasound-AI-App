package com.babyfilx.ui.screens.home


import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.babyfilx.api.apistate.Response
import com.babyfilx.base.App.Companion.isFirst
import com.babyfilx.data.enums.BottomSheetEnum
import com.babyfilx.data.enums.HomeEnum
import com.babyfilx.data.models.DetailsModel
import com.babyfilx.data.models.response.CommanModel
import com.babyfilx.data.models.response.HomeEntriesModel
import com.babyfilx.data.models.response.HomeModel
import com.babyfilx.data.models.response.LocationModel
import com.babyfilx.data.repositories.HomeRepository
import com.babyfilx.utils.HomeUiEvent
import com.babyfilx.utils.exception.NoInternetException
import com.babyfilx.utils.internet.Internet
import com.babyfilx.utils.logs.loge
import com.babyflix.mobileapp.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: HomeRepository
) : ViewModel() {
    var bottomSheet by mutableStateOf(false)
    var isVideo = false

    var refreshing by mutableStateOf(false)
    var detailsModel by mutableStateOf(DetailsModel())
    var locationModel by mutableStateOf(LocationModel())
    var message by mutableStateOf("")
    var delete by mutableStateOf(false)
    var isDownload by mutableStateOf(false)

    var mainMessage by mutableStateOf("")

    var uri: String = ""

    private val apiResponse = Channel<Response<HomeModel>>()
    val response = apiResponse.receiveAsFlow()
    private val deleteApiResponse = Channel<Response<CommanModel>>()
    val deleteResponse = deleteApiResponse.receiveAsFlow()

    // LiveData for holding the error message
    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    val _uiEvent = MutableLiveData<HomeUiEvent>()
    val uiEvent: LiveData<HomeUiEvent> get() = _uiEvent


    var images by mutableStateOf(mutableMapOf<String, List<HomeEntriesModel>>())
    var videos by mutableStateOf(mutableMapOf<String, List<HomeEntriesModel>>())
    var all by mutableStateOf(mutableMapOf<String, List<HomeEntriesModel>>())

    // Track the loading state
    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    fun setLoadingState(isLoading: Boolean) {
        _isLoading.value = isLoading
    }


    var type = 0

    val tabData = listOf(
        "All",
        "Photos",
        "Videos",
    )

    init {
        galleyApi()
        getLocationAPI()
    }

    fun getLocationAPI() {
        try {
            viewModelScope.launch {
                repository.getLocation().catch {}.collectLatest {
                    loge("LocationModel $it")
                    locationModel = it
                }
            }
        }catch (e: NoInternetException){
            _uiEvent.value = HomeUiEvent.ShowSnackbar("No internet connection")
        }
    }


    fun galleyApi() {
        try {
            setLoadingState(true)
            viewModelScope.launch {
                loge("final call 1")
                apiResponse.send(Response.Loading())
                loge("final call 2")
                repository.galleryApiForHome().catch {
                    mainMessage = it.message.toString()
                    refreshing = false
                    apiResponse.send(Response.Error(errorMessage = it.message.toString()))
                    setLoadingState(false)
                }.collect { it ->
                    all.clear()
                    videos.clear()
                    images.clear()
                    all = it.all
                    refreshing = false
                    videos = it.videos
                    images = it.images
                    apiResponse.send(Response.Success(it))
                    mainMessage = it.message
                    isFirst = false
                    loge("final call 3"+it.images)
                    setLoadingState(false)
                }
            }
        }catch (e: NoInternetException){
            _uiEvent.value = HomeUiEvent.ShowSnackbar("No internet connection")
            setLoadingState(false)
        }
    }

    /**
     * this is for get the id for delete node
     */
    fun deleteApi() {
        loge("is the selected index $type")
        var isOne = 0
        var id = ""
        when (type) {
            0 -> {
                all.forEach {
                    it.value.forEach { values ->
                        if (values.isSelect) {
                            id = values.node_id
                            isOne++
                            deleteAPiCall(id, isOne)
                        }
                    }
                }
            }
            1 -> {
                images.forEach {
                    it.value.forEach { values ->
                        if (values.isSelect) {
                            id = values.node_id
                            isOne++
                            deleteAPiCall(id, isOne)
                        }
                    }
                }
            }
            2 -> {
                videos.forEach {
                    it.value.forEach { values ->
                        if (values.isSelect) {
                            id = values.node_id
                            isOne++
                            deleteAPiCall(id, isOne)
                        }
                    }
                }
            }
        }
        if (isOne == 0) {
            message = "Please select the item to delete"
        }
    }


    /**
     * this is for delete api call
     */
    fun deleteAPiCall(id: String, isOne: Int) {
//        when (isOne) {
        viewModelScope.launch {
            deleteApiResponse.send(Response.Loading())
            repository.deleteApi(id).catch {
                message = it.message.toString()
                deleteApiResponse.send(Response.Error(message))
            }.collectLatest {
                deleteApiResponse.send(Response.Success(it))
                message = it.message.toString()
                galleyApi()
            }
        }
//            0 -> message = "Please select the item to delete"
    }


    val imageAndVideoList = mutableMapOf(
        R.string.choose_images_and_videos to BottomSheetEnum.Any,
        R.string.photo_form_camera to BottomSheetEnum.Image_Camera,
        R.string.video_form_camera to BottomSheetEnum.Video_Camera,
        R.string.take_from_photos to BottomSheetEnum.Image_Gallery,
        R.string.take_from_videos to BottomSheetEnum.Video_Gallery,
        R.string.cancel to BottomSheetEnum.Nothing,
    )


    /**
     * this is for Select all
     */
    fun selectAll(initial: String, homeEnum: HomeEnum, d: Boolean) {
        when (homeEnum) {
            HomeEnum.All -> {
                val data = all.getValue(initial).map {
                    it.copy(isSelect = d)
                }
                all.set(initial, value = data)
            }
            HomeEnum.Image -> {
                val data = images.getValue(initial).map {
                    it.copy(isSelect = d)
                }
                images.set(initial, value = data)
            }
            HomeEnum.Videos -> {
                val data = videos.getValue(initial).map {
                    it.copy(isSelect = d)
                }
                videos.set(initial, value = data)
            }
        }

    }

    /**
     * this is for Select one item at a time
     */
    fun selectParticularItem(i: Int, initial: String, homeEnum: HomeEnum, select: Boolean) {
        when (homeEnum) {
            HomeEnum.All -> {
                val data = all.getValue(initial).mapIndexed { index, it ->
                    if (index == i)
                        it.copy(isSelect = !select)
                    else it
                }
                all.set(initial, value = data)
            }
            HomeEnum.Image -> {
                val data = images.getValue(initial).mapIndexed { index, it ->
                    if (index == i)
                        it.copy(isSelect = !select)
                    else it
                }
                images.set(initial, value = data)
            }
            HomeEnum.Videos -> {
                val data = videos.getValue(initial).mapIndexed { index, it ->
                    if (index == i)
                        it.copy(isSelect = !select)
                    else it
                }
                videos.set(initial, value = data)
            }
        }
    }


    /**
     * this is for download any URL you want
     */
    fun downloadImageAndVideo(onDownload: (HomeEntriesModel?) -> Unit) {
        var isSelect = false
        isDownload = false
        all.forEach {
            it.value.forEach { values ->
                if (values.isSelect) {
                    onDownload(values)
                    isSelect = true
                }
            }
        }
        images.forEach {
            it.value.forEach { values ->
                if (values.isSelect) {
                    onDownload(values)
                    isSelect = true
                }
            }
        }
        videos.forEach {
            it.value.forEach { values ->
                if (values.isSelect) {
                    onDownload(values)
                    isSelect = true
                }
            }
        }
        if (isSelect)
            unselectAll()
        else
            onDownload(null)
    }


    fun videoList() {
        val list = mutableListOf<HomeEntriesModel>()
        videos.forEach { it ->
            it.value.forEach { data ->
                list.add(data)
            }
        }
        detailsModel.list = list
    }


    private fun unselectAll() {
        all.forEach { v ->
            v.value.forEach {
                if (it.isSelect)
                    it.isSelect = false
            }
        }

        images.forEach { v ->
            v.value.forEach {
                if (it.isSelect)
                    it.isSelect = false
            }
        }
        videos.forEach { v ->
            v.value.forEach {
                if (it.isSelect)
                    it.isSelect = false
            }
        }
        images = images
        videos = videos
        all = all
        isDownload = true
    }

}