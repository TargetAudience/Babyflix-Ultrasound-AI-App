package com.babyfilx.utils

import android.annotation.TargetApi
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.OpenableColumns
import androidx.compose.material.ScaffoldState
import com.babyfilx.data.models.response.CommanModel
import com.babyfilx.data.models.response.UpgradeUserModel
import org.json.JSONArray
import java.text.SimpleDateFormat
import java.util.*


object Constant {


    const val LIKE_STATUS_DATABASE = "like_status_database"
    const val FireBaseURL = "https://aeb7zdjjb7.execute-api.us-east-1.amazonaws.com/"
    const val BASE_URL2 = "https://8d5a-2401-4900-5ef0-1f7b-ff27-417-7fa4-a892.ngrok-free.app/"
    fun errorMessage(data: String): CommanModel {
        val jsonArray = JSONArray(data)
        var message = ""
        var code = 0

        val result = jsonArray.getJSONObject(0)
        if (result.has("success")) {
            message = result.optString("success")
            code = 200
        } else {
            message = result.optString("error")
        }

        return CommanModel(
            message = message,
            code = code
        )
    }

    fun Context.getRealPathFromURI(selectedImage: Uri?): String? {
        val returnCursor = contentResolver.query(
            selectedImage!!,
            null, null, null, null
        )
        val nameIndex = returnCursor!!.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        returnCursor.moveToFirst()
        return returnCursor.getString(nameIndex)

    }


    fun getCurrentDate(): String {
        val c: Date = Calendar.getInstance().time
        val df = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return df.format(c)
    }

    fun String.getDateFromLong(): String? {
        val simpleDateFormat = SimpleDateFormat("MMM dd, yyyy", Locale("en"))
        return simpleDateFormat.format(this.toLong() * 1000L)
    }


    suspend fun showSnackBar(s: String, scaffoldState: ScaffoldState) {
        scaffoldState.snackbarHostState.showSnackbar(s)
    }
}