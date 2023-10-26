package com.babyfilx.ui.screens.news

import androidx.compose.runtime.*
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.babyfilx.api.apistate.Response
import com.babyfilx.base.App
import com.babyfilx.data.localdatabse.room.LikeStatusDao
import com.babyfilx.data.models.FormEvent
import com.babyfilx.data.models.FormState
import com.babyfilx.data.models.ScreenState
import com.babyfilx.data.models.response.*
import com.babyfilx.data.repositories.BlogRepository
import com.babyfilx.ui.screens.dashboard.DashboardViewModel
import com.babyfilx.utils.HomeUiEvent
import com.babyfilx.utils.exception.NoInternetException
import com.babyfilx.utils.logs.loge
import com.babyfilx.utils.pagination.DefaultPagination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewsViewModel @Inject constructor(
    private val repository: BlogRepository,
    private val room: LikeStatusDao,
) : ViewModel() {

//    val dashboardViewModel: DashboardViewModel? = null

    var message by mutableStateOf("")
    var states by mutableStateOf(ScreenState())
    var state by mutableStateOf(FormState())

    var pos = 0
    var id = "0"
    var isCategory = false
    private val apiResponse = Channel<Response<Blog>>()
    val apiResponse1 = apiResponse.receiveAsFlow()


    var list = mutableStateListOf<BlogResponse>()
    var likes = mutableListOf<LikeStatu>()
    var likesStatusRoom = mutableListOf<LikeStatusRoomModel>()

    var catergoryList = mutableStateListOf<Category>()

    private val _uiEvent = MutableLiveData<HomeUiEvent>()
    val uiEvent: LiveData<HomeUiEvent> get() = _uiEvent

    init {
//        viewModelScope.launch {
//            dashboardViewModel?.recentScansFlow?.collect { recentScans ->
//                val convertedData = recentScans.map { scan ->
//                    BlogResponse(
//                        blogBody = scan.blogBody,
//                        blogImagePath = scan.blogImagePath,
//                        blogTitle = scan.blogTitle,
//                        blogUrl = scan.blogUrl,
//                        createdAt = scan.createdAt,
//                        dislike = scan.dislike,
//                        like = scan.like,
//                        nid = scan.nid,
//                        uid = scan.uid,
//                        userEmail = scan.userEmail,
//                        userPicture = scan.userPicture,
//                        categoryName = scan.categoryName,
//                        shareUrl = scan.shareUrl
//                    )
//                }
//                states = states.copy(items = convertedData)
//            }
//        }
    }

    fun formEvent(event: FormEvent) {
        when (event) {
            is FormEvent.SearchNews -> {
                //if (!states.isLoading)
                state = state.copy(searchNews = event.searchNews)
                isCategory = false

                reset()

            }
            else -> {

            }
        }
    }

    private val paginator = DefaultPagination(
        initialKey = states.page,
        onLoadUpdated = {
            states = states.copy(isLoading = it)
        },
        onRequest = {

            repository.blogApiForNews(it, 5,state.searchNews, isCategory, id)
        },
        getNextKey = {
            states.page + 1
        },
        onError = {
            loge("Error on news screen ${it!!.localizedMessage}")
            states = states.copy(endReached = true, error = it!!.localizedMessage)
            _uiEvent.value = HomeUiEvent.ShowSnackbar("No internet connection")
        },
        onSuccess = { items, nextKey ->

            val filter = filterLikeData(items)

            states = states.copy(
                items = states.items + filter,
                page = nextKey,
                endReached = if (isCategory && states.page == 1) true else items.isEmpty()
            )
        }
    )

    private fun filterLikeData(item: List<BlogResponse>, index: Int = -1): List<BlogResponse> {
        item.forEachIndexed { pos, blog ->
            var isLike = false

            if (index == -1) {
                likesStatusRoom.forEach {
                    val id = it.likeEntityId == blog.nid
                    val count = it.count.toInt() > blog.like
                    if (id && count) {
                        loge("JJNJNHJNH")
                        blog.isLike = true
                        blog.like = it.count.toInt()
                    } /*else {
                        viewModelScope.launch(Dispatchers.IO) {
                            room.delete(it)
                        }
                    }*/
                }
            }

            likes.forEach {
                val isId = blog.nid == it.likeEntityId
                val isUser = App.data.id == it.likeUserId
                if (isId && isUser) {
                    isLike = true
                }
            }
            if (isLike) {
                blog.isLike = true
                if (index != -1 && pos == index) {
                    val count = blog.like.toInt() + 1
                    blog.like = count
                }
            }
        }
        return item
    }

    init {
        likesStatusApi()

    }

    private fun getCategoriesApi() {
        try {
            viewModelScope.launch {
                repository.categoryApiForNews().onStart {
                    catergoryList.clear()
                    catergoryList.add(Category("0", "All", true))
                }.catch {}.collectLatest {
                    catergoryList.addAll(it)
                }
            }
        }catch (e: NoInternetException){
            _uiEvent.value = HomeUiEvent.ShowSnackbar("No internet connection")
        }
    }

    fun likesStatusApi() {
        try {
            viewModelScope.launch {
                states = states.copy(isLoading = true)
                repository.getLikesUsers().onStart {
                    likesStatusRoom = room.getAllLikeStatus().toMutableList()
                    loge("RoomDatabase  $likesStatusRoom")
                }.catch {
                    states = states.copy(isLoading = false, error = it.message)
                }.collectLatest {
                    states = states.copy(isLoading = false)

                    likes = it.filter { like -> like.likeUserId == App.data.id }.toMutableList()

                    val call1 = async { getBlogApi() }
                    val call2 = async { getCategoriesApi() }
                    call1.await()
                    call2.await()
                }
            }
        }catch (e: NoInternetException){
            _uiEvent.value = HomeUiEvent.ShowSnackbar("No internet connection")
        }
    }


    private fun addLikeApi(id: Int) {
        try {
            viewModelScope.launch {
                repository.addLikeAPi(id).catch {}.collectLatest {
                    loge("add like Api $it")
                    // reset()
                }
            }
        }catch (e:NoInternetException){
            _uiEvent.value = HomeUiEvent.ShowSnackbar("No internet connection")
        }
    }

    fun getBlogApi() {
        try {
            viewModelScope.launch {
                paginator.loadMoreItem()
            }
        }catch (e: NoInternetException){
            _uiEvent.value = HomeUiEvent.ShowSnackbar("No internet connection")
        }
    }

    fun likeAndUpdate(index: Int) {
        val id = states.items[index].nid
        likes.add(LikeStatu(id, App.data.id))
        addLikeApi(id.toInt())

        //locally change like status for the screen
        val data = filterLikeData(states.items, index)
        //Adding data in room database
        viewModelScope.launch(Dispatchers.IO) {
            val model = LikeStatusRoomModel(likeEntityId = id, data[index].like.toString())
            room.addStatus(model)
        }
        states = states.copy(items = emptyList())
        states = states.copy(items = data)
    }


    fun reset() {
        states = states.copy(items = emptyList(), page = 1)
        paginator.reset()
        getBlogApi()
    }


    fun filterBlogsCategories(index: Int) {
        catergoryList = catergoryList.mapIndexed { pos, it ->
            it.copy(isSelected = pos == index)
        }.toMutableStateList()
    }

}