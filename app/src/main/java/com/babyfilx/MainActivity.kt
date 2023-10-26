package com.babyfilx

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.babyfilx.api.apiinterface.APIS
import com.babyfilx.api.workmanager.UploadService
import com.babyfilx.base.App
import com.babyfilx.data.AppState
import com.babyfilx.data.enums.BottomSheetEnum
import com.babyfilx.data.localdatabse.LocalDatabase
import com.babyfilx.data.models.UserStatusModel
import com.babyfilx.data.rememberAppState
import com.babyfilx.ui.screens.home.HomeViewModel
import com.babyfilx.ui.screens.imageEnhancement.SelectImagesViewModel
import com.babyfilx.ui.theme.*
import com.babyfilx.utils.CommanClass
import com.babyfilx.utils.Constant.getRealPathFromURI
import com.babyfilx.utils.HomeUiEvent
import com.babyfilx.utils.commonviews.*
import com.babyfilx.utils.exception.NoInternetException
import com.babyfilx.utils.internet.Internet
import com.babyfilx.utils.logs.loge
import com.babyfilx.utils.permissions.*
import com.babyfilx.utils.toast
import com.babyflix.mobileapp.R
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.ActivityResult.RESULT_IN_APP_UPDATE_FAILED
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject




@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var commanClass: CommanClass

    private var navController1: NavController?= null

    private var isProgressDialogVisible by mutableStateOf(false)

    @Inject
    lateinit var apis: APIS

    private var isDialogVisible by mutableStateOf(false)
    private var dialogTitle by mutableStateOf("")
    private var dialogMessage by mutableStateOf("")

    private val mainActivityScope = CoroutineScope(Dispatchers.Main)

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.let {
                if (it.action == "SHOW_NOTIFICATION_DIALOG") {
                    dialogTitle = it.getStringExtra("title") ?: ""
                    dialogMessage = it.getStringExtra("message") ?: ""
                    isDialogVisible = true
                }
            }
        }
    }


    private val APP_UPDATE_REQUEST_CODE = 123

    val viewModel: MainViewModel by viewModels()

    val homeViewModel: HomeViewModel by viewModels()

    val selectImagesViewModel: SelectImagesViewModel by viewModels()

    var isSuccess = true

    private lateinit var appUpdateManager: AppUpdateManager


    @Inject
    lateinit var localDatabase: LocalDatabase

    @OptIn(ExperimentalMaterialApi::class)
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @SuppressLint("CoroutineCreationDuringComposition")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(Internet.isInternetAvailable(applicationContext)){
            // Initialize the AppUpdateManager
            appUpdateManager = AppUpdateManagerFactory.create(this)
            // Check for updates
            checkForAppUpdate()
            try {
                mainActivityScope.launch {
                    val id = App.data.id
                    if(id.isNotEmpty()){
                        val response =  apis.getUserStatus(UserStatusModel( uid = id))
                        if (response.isSuccessful && response.body() != null) {
                            loge("Responsee user type ${response.body()!!.status}")
                            if(response.body()!!.status == "premium"){
                                if (App.data.userType == "premium"){
                                    localDatabase.setUserType("premium")
                                }else{
                                    localDatabase.setUserType("premium")
                                }
                            }else{
                                if (App.data.userType == "premium"){
                                    localDatabase.setUserType("basic")
                                }else{
                                    localDatabase.setUserType("basic")
                                }
                            }
                        } else {
                            loge("Response error")
                        }
                    }
                }
            }catch (e: NoInternetException){
                homeViewModel._uiEvent.value = HomeUiEvent.ShowSnackbar("No internet connection")
            }
        }else{
            homeViewModel._uiEvent.value = HomeUiEvent.ShowSnackbar("No internet connection")
        }

        setContent {
            val appState = rememberAppState()
            val navController = rememberNavController()
            val context = LocalContext.current
//            context.configureAmplify()
            val scaffoldState = rememberScaffoldState()
            val scope = rememberCoroutineScope()
            navController1 = navController

//            HandleNotificationNavigation(navController = navController, notificationData = intent.extras, appState)

            // Observe the HomeViewModel's uiEvent
            homeViewModel.uiEvent.observe(this) { event ->
                when (event) {
                    is HomeUiEvent.ShowSnackbar -> {
                        mainActivityScope.launch {
                            showSnackBar(event.message, scaffoldState)
                        }
                    }
                    // Handle other UI events if needed
                }
            }

            val broadcastManager = LocalBroadcastManager.getInstance(context)

            //Open enhance complete screen throw notification
//            DisposableEffect(Unit) {
//                val receiver = object : BroadcastReceiver() {
//                    override fun onReceive(context: Context?, intent: Intent?) {
//                        intent?.let {
//                            if (it.action == "FCM_NOTIFICATION_RECEIVED") {
//                                // Trigger the pull-to-refresh operation here
//                                selectImagesViewModel.notificationTitle = it.getStringExtra("title") ?: ""
//                                selectImagesViewModel.notificationMessage = it.getStringExtra("body") ?: ""
//
//                                if (selectImagesViewModel.notificationTitle == "Babyflix") {
//                                    // Navigate only if it hasn't already navigated
//                                    appState.navHostController.navigate(BottomBarScreen.EnhancementComplete.routes)
//                                }
//                            }
//                        }
//                    }
//                }
//                val filter = IntentFilter("FCM_NOTIFICATION_RECEIVED")
//                broadcastManager.registerReceiver(receiver, filter)
//
//                onDispose {
//                    broadcastManager.unregisterReceiver(receiver)
//                }
//            }

            val broadcastReceiverMessage = object : BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {
                    intent?.let {
                        if (it.action == "UPLOAD_COMPLETED") {
                            // Show Snackbar when the upload is completed
                            mainActivityScope.launch {
                                showSnackBar(getString(R.string.upload_successful), scaffoldState)
                                homeViewModel.refreshing = true
                                homeViewModel.galleyApi()
                            }
                        }else if(it.action == "UPLOAD_ERROR"){
                            mainActivityScope.launch {
                                showSnackBar(getString(R.string.something_went_wrong), scaffoldState)
                            }
                        }else {

                        }
                    }
                }
            }

            LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiverMessage, IntentFilter("UPLOAD_COMPLETED"))

            var isUpload by remember { mutableStateOf(false) }

            val bottomSheetScaffoldState =
                rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)

            val coroutineScope = rememberCoroutineScope()


            // Register the broadcast receiver
            LocalBroadcastManager.getInstance(this)
                .registerReceiver(broadcastReceiver, IntentFilter("SHOW_NOTIFICATION_DIALOG"))


            if(isDialogVisible){
                when (dialogTitle) {
                    "AI_Expire" -> {
                        appState.navHostController.navigate(BottomBarScreen.Flix10KSubscription.routes)
                        mainActivityScope.launch {
                            localDatabase.setUserType("basic")
                        }
                    }
                    "AI_Subscribe" -> {
                        appState.navHostController.navigate(BottomBarScreen.ExperienceFlix10K.routes)
                        mainActivityScope.launch {
                            localDatabase.setUserType("premium")
                        }
                    }
                }
            }


            if (isProgressDialogVisible) {
                PleaseWaitDialog(mainActivityScope)
            }

            BackPressHandler {
                if (scaffoldState.drawerState.isOpen){
                    scope.launch {
                        scaffoldState.drawerState.close()
                    }
                }
                else{
                    finish()
                }
            }

            BabyFilxTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    scaffoldState = scaffoldState,
                    topBar = {
                        if (viewModel.topBar)
                            TopAppBars(title = viewModel.title, isDone = false) {
                                if (it == 1) {
                                    scope.launch {
                                        scaffoldState.drawerState.open()
                                    }
                                } else {
                                    context.toast("EveryThing is Done")
                                }
                            }
                    },
                    backgroundColor = Color.White,
                    bottomBar = {
                        if (appState.shouldShowBottomBar)
                            BottomBar(navController = appState.navHostController, viewModel = viewModel,selectImagesViewModel = selectImagesViewModel, context = context, userType = App.data.userType, scope = coroutineScope, bottomSheetScaffoldState = bottomSheetScaffoldState, scaffoldState = scaffoldState){
                                isUpload = it
                            }
                    },
                ) { innerPadding ->
                    BottomNavGraph(modifier = Modifier.padding(innerPadding), navController = appState.navHostController)
                }
            }

            context.uploadFile(isUpload,viewModel) {
                isUpload = false
            }

            context.BottomSheet(scope = coroutineScope, viewModel = viewModel, context = context, scaffoldState = scaffoldState , bottomSheetScaffoldState = bottomSheetScaffoldState){
                isUpload = it
            }

        }


    }

    override fun onDestroy() {
        // Unregister the BroadcastReceiver in onDestroy to avoid memory leaks
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver)
        super.onDestroy()
    }


    @OptIn(ExperimentalMaterialApi::class)
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @Composable
    fun BottomBar(
        navController: NavHostController,
        userType: String,
        viewModel: MainViewModel,
        selectImagesViewModel:SelectImagesViewModel,
        context: Context,
        scope: CoroutineScope,
        bottomSheetScaffoldState: ModalBottomSheetState,
        scaffoldState: ScaffoldState,
        upload: (Boolean) -> Unit
    ) {
        val screens = listOf(
            BottomBarScreen.Dashboard,
            BottomBarScreen.Home,
            BottomBarScreen.FlixCam,
            BottomBarScreen.Flix10KSubscription,
            BottomBarScreen.More
        )
        val navStackBackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navStackBackEntry?.destination

        Card(
            backgroundColor = bottom_background, modifier = Modifier
                .fillMaxWidth()
                .height(dp60), elevation = dp20
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = dp10),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                screens.forEach { tab ->

                    loge("Selected item ${tab.routes}")
                    AddItem(
                        tab = tab,
                        currentDestination = currentDestination,
                        navHostController = navController,
                        viewModel = viewModel,
                        selectImagesViewModel = selectImagesViewModel,
                        scope = scope,
                        bottomSheetScaffoldState = bottomSheetScaffoldState,
                        scaffoldState = scaffoldState
                    ){
                        upload(true)
                    }
                }
            }
        }

    }
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    fun RowScope.AddItem(
        currentDestination: NavDestination?,
        tab: BottomBarScreen,
        navHostController: NavHostController,
        viewModel: MainViewModel,
        selectImagesViewModel: SelectImagesViewModel,
        scope: CoroutineScope,
        bottomSheetScaffoldState: ModalBottomSheetState,
        scaffoldState: ScaffoldState,
        upload: (Boolean) -> Unit,
    ) {

        val permissionRequest =
            rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { resultsMap -> }
        val isPicker = isPhotoPickerAvailable()

        LaunchedEffect(Unit) {
            val permission = pushNotificationPermission()
            if (permission.isNotEmpty()) {
                permissionRequest.launch(permission.toTypedArray())
            }
        }

        val cameraLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.TakePicture(),
            onResult = { success ->
                loge("gjfh ${viewModel.uri}")
                if (success) {
                    val message = if (viewModel.isVideo) {
                        "Video taken Successfully"
                    } else {
                        "Image taken Successfully"
                    }
                    // TODO show toast msg image selected
                    mainActivityScope.launch {
                        showSnackBar(message, scaffoldState)
                    }
                    upload(true)
                }
            }
        )

        val isPremiumUser = App.data.userType
        var selected = currentDestination?.hierarchy?.any { it.route == tab.routes } == true

        if(currentDestination?.route == "image selection" && tab.routes == "flix10k"){
            selected = true
        }else if(currentDestination?.route == "flix10k" && tab.routes == "flix10k"){
            selected = true
        }else if(currentDestination?.route == "experience flix10K" && tab.routes == "flix10k"){
            selected = true
        }else if(currentDestination?.route == "enhancement_complete" && tab.routes == "flix10k"){
            selected = true
        }
        else if(currentDestination?.route == "news details" && tab.routes == "more"){
            selected = true
        }else if(currentDestination?.route == "news" && tab.routes == "more"){
            selected = true
        }

        // Set contentColor to pink_color for selected tabs, otherwise use grey_plan_text
        val contentColor = if (selected) pink_color else grey_plan_text

        loge("Current dest ${currentDestination?.route} tab ${tab.routes}")

        IconButton(
            onClick = {
                loge("Tab Click ${tab.routes}")
                selectImagesViewModel.setLoadingState(false)
                if (tab.routes.equals("flixcam")) {
                    // Open the camera directly
                    val permission = updateOrRequestPermissions()
                    if (permission.isNotEmpty()) {
                        loge("messagesss $permission")
                        permissionRequest.launch(permission.toTypedArray())
                    }else{
                        myClickHandler { uri ->
                            viewModel.uri = uri.toString()
                            cameraLauncher.launch(uri)
                        }
                    }
                    //If want to open bottomsheet
//                    scope.launch {
//                        bottomSheetScaffoldState.show()
//                    }
//                    viewModel.bottomSheet = true
                } else if (tab.routes.equals("flix10k")) {
                    if (isPremiumUser.equals("premium")) {
                        navHostController.navigate(BottomBarScreen.ExperienceFlix10K.routes)
                    } else {
                        navHostController.navigate(BottomBarScreen.Flix10KSubscription.routes)
                    }
                }
                else {
                    navHostController.navigate(tab.routes)
                }
            },
            modifier = Modifier.weight(1f)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    painter = painterResource(id = tab.icon),
                    contentDescription = "",
                    tint = contentColor,
                    modifier = Modifier.size(20.dp),
                )
                Text(text = tab.title, color = contentColor, fontSize = sp11)
            }
        }
    }






    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    fun BottomSheetForImagePicker(
        bottomSheetScaffoldState: ModalBottomSheetState,
        viewModel: MainViewModel,
        onClick: (BottomSheetEnum) -> Unit
    ) {
        ModalBottomSheetLayout(
            modifier = Modifier.padding(horizontal = dp8), sheetShape = RoundedCornerShape(dp4),
            sheetState = bottomSheetScaffoldState,
            sheetContent = {
                Column(
                    Modifier
                        .fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    viewModel.imageAndVideoList.onEachIndexed { index, it ->
                        TextContent(
                            text = it.key,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = dp10),
                            fontWeight = if (index == 0) FontWeight.Normal else FontWeight.Bold,
                            fontSize = if (index == 0) sp11 else sp16,
                            color = if (index == 0) hint_color else Color.Black,
                            textAlign = TextAlign.Center
                        ) {
                            onClick(it.value)
                        }
                        Divider(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(border_color)
                                .height(dp1)
                        )
                    }
                }
            },
            sheetBackgroundColor = Color.White
        ) {}
    }




@SuppressLint("SuspiciousIndentation")
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    fun Context.BottomSheet(
        scope: CoroutineScope,
        viewModel: MainViewModel,
        bottomSheetScaffoldState: ModalBottomSheetState,
        context: Context,
        scaffoldState: ScaffoldState,
        upload: (Boolean) -> Unit
    ) {
        val permissionRequest =
            rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { resultsMap -> }
        val isPicker = isPhotoPickerAvailable()

        LaunchedEffect(Unit) {
            val permission = pushNotificationPermission()
            if (permission.isNotEmpty()) {
                permissionRequest.launch(permission.toTypedArray())
            }
        }

        val cameraLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.TakePicture(),
            onResult = { success ->
                loge("gjfh ${viewModel.uri}")
                if (success) {
                    val message = if (viewModel.isVideo) {
                        getString(R.string.video_taken_successfully)
                    } else {
                        getString(R.string.image_taken_successfully)
                    }
                    // TODO show toast msg image selected
                    mainActivityScope.launch {
                        showSnackBar(message, scaffoldState)
                    }
                    upload(true)
                }
            }
        )

        loge("IsPicker  $isPicker")
        val gallery = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.PickVisualMedia(),
            onResult = { uri ->
                viewModel.uri = uri.toString()
                //viewModel.callUploadApi()
                if (uri != null) {
                    val message = if (viewModel.isVideo) {
                        getString(R.string.video_taken_successfully)
                    } else {
                        getString(R.string.image_taken_successfully)
                    }
                    //TODO
                    mainActivityScope.launch {
                            showSnackBar(message, scaffoldState)
                    }
                    validForm(viewModel.isVideo, viewModel) {
                        upload(true)
                    }
                }
            }
        )

        val galleryNotPicker = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent(),
            onResult = { uri ->
                viewModel.uri = uri.toString()
                //viewModel.callUploadApi()
                if (uri != null) {
                    mainActivityScope.launch {
                        showSnackBar(getString(R.string.image_taken_successfully), scaffoldState)
                    }
                    validForm(viewModel.isVideo, viewModel) {
                        upload(true)
                    }
                }
            }
        )
        val pickVideoLauncher =
            rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    result.data?.let {
                        viewModel.uri = it.data.toString()
                        //viewModel.callUploadApi()
                        mainActivityScope.launch {
                            showSnackBar(getString(R.string.video_taken_successfully), scaffoldState)
                        }
                        upload(true)
                    } ?: mainActivityScope.launch {
                        showSnackBar(getString(R.string.something_went_wrong), scaffoldState)
                    }
                }
            }

        if (viewModel.bottomSheet)
            BottomSheetForImagePicker(bottomSheetScaffoldState, viewModel) { it ->
                loge("messagesss ")
                val permission = updateOrRequestPermissions()
                if (permission.isNotEmpty() && (it != BottomSheetEnum.Any && it != BottomSheetEnum.Nothing)) {
                    loge("messagesss $permission")
                    permissionRequest.launch(permission.toTypedArray())
                } else {
                    when (it) {
                        BottomSheetEnum.Image_Camera -> {
                            myClickHandler { uri ->
                                viewModel.uri = uri.toString()
                                cameraLauncher.launch(uri)
                            }
                        }
                        BottomSheetEnum.Image_Gallery -> {
                            viewModel.isVideo = false
                            if (isPicker)
                                gallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                            else
                                galleryNotPicker.launch("image/*")
                        }
                        BottomSheetEnum.Video_Camera -> {
                            cameraIntent { intent ->
                                pickVideoLauncher.launch(intent)
                            }
                        }
                        BottomSheetEnum.Video_Gallery -> {
                            viewModel.isVideo = true
                            if (isPicker)
                                gallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly))
                            else
                                galleryNotPicker.launch("video/*")
                        }
                        else -> {}
                    }

                    scope.launch {
                        if (it != BottomSheetEnum.Any) {
                            bottomSheetScaffoldState.hide()
                            viewModel.bottomSheet = false
                        }

                    }
                }
            }
    }

    private suspend fun showSnackBar(s: String, scaffoldState: ScaffoldState) {
        scaffoldState.snackbarHostState.showSnackbar(s)
    }


    private fun checkForAppUpdate() {
        try {
            val appUpdateInfoTask = appUpdateManager.appUpdateInfo
            appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
                if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
                ) {
                    // Request the update
                    appUpdateManager.startUpdateFlowForResult(
                        appUpdateInfo,
                        AppUpdateType.IMMEDIATE,
                        this,
                        APP_UPDATE_REQUEST_CODE
                    )
                }
            }
        } catch (e : NoInternetException) {
            loge("error")
        }
    }

    // Handle the update flow result
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == APP_UPDATE_REQUEST_CODE) {
            when (resultCode) {
                RESULT_OK -> {
                    // Update completed successfully
                    toast("App update successfully")
                }
                RESULT_CANCELED -> {
                    // Update canceled by the user
                    toast("App update cancel")
                }
                RESULT_IN_APP_UPDATE_FAILED -> {
                    // Update failed, retry or handle the error
                    toast("App update failed")
                }
            }
        }
    }

    fun Context.validForm(isImage: Boolean, mainViewModel: MainViewModel, onClick: () -> Unit) {
        val path = getRealPathFromURI(mainViewModel.uri.toUri())
        val temp = path!!.substring(path.toString().lastIndexOf(".") + 1)
        val list = listOf("mp4", "avi")
        loge("nsjdbfajb $isImage")
        if (!list.contains(temp) && isImage) {
            mainViewModel.message = getString(R.string.video_upload_message)
            return
        } else {
            onClick()
        }
    }

    fun Context.uploadFile(isUpload: Boolean, viewModel: MainViewModel, onClick: () -> Unit) {
        if (isUpload) {
            // uploadFile(uri = viewModel.uri.toUri(),viewModel.locationModel){}
            val data = Intent(this, UploadService::class.java)
            data.putExtra("file", viewModel.uri)
            data.putExtra("company", App.data.companyId)
            data.putExtra("location", App.data.locationId)
            startService(data)
        }
        onClick()
    }

    @Composable
    fun HandleNotificationNavigation(
        navController: NavController,
        notificationData: Bundle?,
        appState: AppState
    ) {
        val screenToOpen = notificationData?.getString("screenToOpen")

        if (screenToOpen != null) {
            when (screenToOpen) {
                "screen1" -> {
                    // Navigate to screen1
                    navController.navigate(BottomBarScreen.ImageSelection.routes)
                }
                // Add more cases for other screens as needed
                else -> {
                    // Handle unknown screen or default behavior
                }
            }
        }
    }

}


