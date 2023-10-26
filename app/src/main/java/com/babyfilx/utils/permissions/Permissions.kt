package com.babyfilx.utils.permissions

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.ext.SdkExtensions.getExtensionVersion
import android.provider.MediaStore
import android.provider.Settings
import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcher
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.annotation.RequiresApi
import androidx.compose.runtime.*
import androidx.core.content.ContextCompat
import com.babyfilx.utils.logs.loge
import java.io.File
import java.io.IOException


fun Context.cameraIntent(click: (Intent) -> Unit) {
    Intent(MediaStore.ACTION_VIDEO_CAPTURE).also { takeVideoIntent ->
        takeVideoIntent.resolveActivity(packageManager)?.also {
            click(takeVideoIntent)
        }
    }
}


fun Context.readPermission() = ContextCompat.checkSelfPermission(
    this,
    android.Manifest.permission.READ_EXTERNAL_STORAGE
) == PackageManager.PERMISSION_GRANTED


fun Context.writePermission() = ContextCompat.checkSelfPermission(
    this,
    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
) == PackageManager.PERMISSION_GRANTED

fun Context.cameraPermission() = ContextCompat.checkSelfPermission(
    this,
    android.Manifest.permission.CAMERA
) == PackageManager.PERMISSION_GRANTED


@RequiresApi(Build.VERSION_CODES.TIRAMISU)
fun Context.notificationPermission() = ContextCompat.checkSelfPermission(
    this,
    Manifest.permission.POST_NOTIFICATIONS
) == PackageManager.PERMISSION_GRANTED

/**
 * getting permissions
 */
fun Context.updateOrRequestPermissions(): MutableList<String> {

    val minSDk29 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
    val minSDk33s = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
    val minSDk33 =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) notificationPermission() else true

    val readExternalStoragePermission = readPermission() || minSDk33s
    val cameraPermission = cameraPermission()
    val writeExternalStoragePermission = writePermission() || minSDk29

    val permissionRequest = mutableListOf<String>()

    loge("messagesss $readExternalStoragePermission")
    loge("messagesss $cameraPermission")
    loge("messagesss $writeExternalStoragePermission")
    if (!readExternalStoragePermission) {
        permissionRequest.add(android.Manifest.permission.READ_EXTERNAL_STORAGE)
    }
    if (!writeExternalStoragePermission) {
        permissionRequest.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }
    if (!cameraPermission) {
        permissionRequest.add(android.Manifest.permission.CAMERA)
    }
    if (!minSDk33) {
        permissionRequest.add(Manifest.permission.POST_NOTIFICATIONS)
    }
    /*  if (permissionRequest.isNotEmpty()) {
          permissionLauncher.launch(permissionRequest.toTypedArray())
      }*/
    return permissionRequest
}


/**
 * getting permissions
 */
fun Context.writePermissions(): MutableList<String> {

    val minSDk29 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

    val writeExternalStoragePermission = writePermission() || minSDk29

    val permissionRequest = mutableListOf<String>()


    if (!writeExternalStoragePermission) {
        permissionRequest.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }


    return permissionRequest
}


@RequiresApi(Build.VERSION_CODES.TIRAMISU)
fun Context.pushNotificationPermission(): MutableList<String> {
    val minSDk33 =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) notificationPermission() else true
    val permissionRequest = mutableListOf<String>()
    if (!minSDk33) {
        permissionRequest.add(Manifest.permission.POST_NOTIFICATIONS)
    }
    return permissionRequest

}

fun Context.myClickHandler(onClick: (Uri) -> Unit) {
    createImageFile(this)
    val imageUri =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Images.Media.getContentUri(
                MediaStore.VOLUME_EXTERNAL_PRIMARY
            )
        } else {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }
    val imageDetails = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, imageName)
    }
    contentResolver.insert(imageUri, imageDetails)?.let {
        onClick(it)
    } ?: run {
    }

}


lateinit var currentPhotoPath: String
lateinit var imageName: String

/**
 *
 * This for read camera image name
 *
 */
@Throws(IOException::class)
fun createImageFile(context: Context): File? {
    // Create an image file name
    //  val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
    val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    return File.createTempFile(
        "IMG-", /* prefix */
        ".jpg", /* suffix */
        storageDir /* directory */
    ).apply {
        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = absolutePath
        imageName = name
    }
}


@Composable
fun BackPressHandler(
    backPressedDispatcher: OnBackPressedDispatcher? =
        LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher,
    onBackPressed: () -> Unit
) {
    val currentOnBackPressed by rememberUpdatedState(newValue = onBackPressed)

    val backCallback = remember {
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                currentOnBackPressed()
            }
        }
    }

    DisposableEffect(key1 = backPressedDispatcher) {
        backPressedDispatcher?.addCallback(backCallback)

        onDispose {
            backCallback.remove()
        }
    }
}


@RequiresApi(Build.VERSION_CODES.TIRAMISU)
fun isPhotoPickerAvailable(): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        true
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        getExtensionVersion(Build.VERSION_CODES.R) >= 2
    } else {
        false
    }
}

fun Context.allowPermissonCustomly(){
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
    val uri: Uri = Uri.fromParts("package",packageName, null)
    intent.data = uri
    startActivity(intent)
}
