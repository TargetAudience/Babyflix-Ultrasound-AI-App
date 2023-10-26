package com.babyfilx.utils.aws

import android.content.Context
import android.net.Uri
import android.os.Environment
import com.amazonaws.auth.CognitoCachingCredentialsProvider
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.amazonaws.regions.Region.getRegion
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.CannedAccessControlList
import com.amplifyframework.AmplifyException
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin
import com.amplifyframework.core.Amplify
import com.amplifyframework.storage.options.StorageUploadFileOptions
import com.amplifyframework.storage.s3.AWSS3StoragePlugin
import com.babyfilx.base.App
import com.babyfilx.data.enums.AWSStateEnum
import com.babyfilx.data.models.Upload
import com.babyfilx.data.models.response.LocationModel
import com.babyfilx.utils.Constant.getRealPathFromURI
import com.babyfilx.utils.filepath.FileUtilsModified
import com.babyfilx.utils.logs.loge
import com.babyflix.mobileapp.BuildConfig
import java.io.File


fun Context.configureAmplify() {
    try {
        Amplify.addPlugin(AWSCognitoAuthPlugin())
        Amplify.addPlugin(AWSS3StoragePlugin())
        Amplify.configure(this)

        loge("Initialized Amplify")
    } catch (error: AmplifyException) {
        loge("Could not initialize Amplify $error")
    }
}


fun Context.uploadFile(uri: Uri, locationModel: LocationModel, onState: (AWSStateEnum) -> Unit) {
    onState(AWSStateEnum.Loading)
    if (locationModel.location_id.isNullOrBlank()) {
        onState(AWSStateEnum.Error)
        return
    }

    val path =
        FileUtilsModified.getPath(this, uri)
    val fileName = getRealPathFromURI(uri) ?: (1000..9000).random().toString()
    loge("uploadFils $App.data.id\n $fileName $path")

    val key =
        "babyflix/${locationModel.company_id}/${locationModel.location_id}/${App.data.id}_$fileName"
    loge("uploadFils key $key")
    //${BuildConfig.aws}${locationModel.company_id}/${locationModel.location_id}/
    loge("FileUpload key $key")
    val file = File(path)

    Amplify.Storage.uploadFile(
        key,
        file,
        StorageUploadFileOptions.defaultInstance(),
        { progress ->
            loge("upload file ${progress.fractionCompleted}")
        },
        {
            loge("upload file ${it.key}")
            loge("upload file $it")
            onState(AWSStateEnum.Success)
        },
        { error ->
            loge("Failed upload ${error.cause}")
            onState(AWSStateEnum.Error)
        },
    )

}


fun Context.downloadPhoto(fileName: String, onState: (AWSStateEnum) -> Unit) {
    onState(AWSStateEnum.Loading)

    val localFile = File("${Environment.DIRECTORY_DOWNLOADS}/$fileName")
    Amplify.Storage.downloadFile(
        fileName,
        localFile,
        {
            onState(AWSStateEnum.Success)
        },
        { error ->
            loge("Failed upload ${error.printStackTrace()}")
            onState(AWSStateEnum.Error)
        }
    )
}


fun Context.s3Upload(uri: Uri, locationModel: LocationModel, onState: (Upload) -> Unit) {
    if (locationModel.location_id.isNullOrBlank()) {
        onState(Upload.Error)
        return
    }
    loge("uploadFils files")
    onState(Upload.Loading)
    val fileName = getRealPathFromURI(uri) ?: (1000..9000).random().toString()
    val path =
        FileUtilsModified.getPath(this, uri)
    // val credentials = BasicAWSCredentials(access_key_id, BuildConfig.secret_access_key)


    val credentialsProvider = CognitoCachingCredentialsProvider(
        this,
        BuildConfig.pool_id,  // Identity pool ID
        Regions.US_EAST_1 // Region
    )


    val s3 = AmazonS3Client(credentialsProvider, getRegion(Regions.US_EAST_1))
    //s3.setRegion(getRegion(Regions.US_EAST_1))//Release
    //s3.setRegion(getRegion(Regions.US_EAST_2))// Debug
    //s3.endpoint = App.data.id

    loge("uploadFils $App.data.id\n $fileName $path")
    val key =
        "babyflix/${locationModel.company_id}/${locationModel.location_id}/${App.data.id}_$fileName"
    loge("uploadFils key $key")
    val transferUtility = TransferUtility.builder()
        .defaultBucket(
            BuildConfig.bucket_name
        )
        .context(this).s3Client(s3).build()
    val file = File(path)
    val uploadObserver = transferUtility.upload(
        BuildConfig.bucket_name,
        key,
        file,
        CannedAccessControlList.PublicRead
    )


    val transferListener = object : TransferListener {
        override fun onStateChanged(id: Int, state: TransferState) {
            if (state == TransferState.COMPLETED) {
                loge("s3Upload $state")
                onState(Upload.Completed(""))

                //  val url = "${secrets.s3BaseUrl}/$fileName.$extension"
                // LiveSubject.FILE_UPLOAD_FILE.onNext(UploadFileStatus.Complete(url))
            }
        }

        override fun onProgressChanged(id: Int, current: Long, total: Long) {
            val status = (((current.toDouble() / total) * 100.0).toInt())
            loge("s3Upload $status")
            onState(Upload.Progress(status))
            //  LiveSubject.FILE_UPLOAD_FILE.onNext(UploadFileStatus.FileStatus(status))
        }

        override fun onError(id: Int, ex: Exception) {
            onState(Upload.Error)
            loge("s3Upload $ex")
            // LiveSubject.FILE_UPLOAD_FILE.onNext(UploadFileStatus.Error(ex))
        }
    }

    uploadObserver.setTransferListener(transferListener)

}