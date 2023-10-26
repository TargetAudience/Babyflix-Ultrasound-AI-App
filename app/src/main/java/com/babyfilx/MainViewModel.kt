package com.babyfilx

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import com.babyfilx.api.apistate.Response
import com.babyfilx.data.enums.BottomSheetEnum
import com.babyfilx.data.localdatabse.LocalDatabase
import com.babyfilx.data.models.DetailsModel
import com.babyfilx.data.models.response.*
import com.babyflix.mobileapp.R
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
     val localDatabase: LocalDatabase
) : ViewModel() {

    val imageAndVideoList = mutableMapOf(
        R.string.choose_images_and_videos to BottomSheetEnum.Any,
        R.string.photo_form_camera to BottomSheetEnum.Image_Camera,
        R.string.video_form_camera to BottomSheetEnum.Video_Camera,
        R.string.take_from_photos to BottomSheetEnum.Image_Gallery,
        R.string.take_from_videos to BottomSheetEnum.Video_Gallery,
        R.string.cancel to BottomSheetEnum.Nothing,
    )


    var isUploadCompleted by mutableStateOf(false)
    var showDialog by mutableStateOf(false)
    var topBar by mutableStateOf(false)
    var title by mutableStateOf("Gallery")

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


    var images by mutableStateOf(mutableMapOf<String, List<HomeEntriesModel>>())
    var videos by mutableStateOf(mutableMapOf<String, List<HomeEntriesModel>>())
    var all by mutableStateOf(mutableMapOf<String, List<HomeEntriesModel>>())


    var type = 0

    val tabData = listOf(
        "All",
        "Photos",
        "Videos",
    )


}