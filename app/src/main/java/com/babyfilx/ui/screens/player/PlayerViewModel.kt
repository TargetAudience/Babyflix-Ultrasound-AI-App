package com.babyfilx.ui.screens.player

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.view.Surface
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amazonaws.ivs.player.MediaPlayer
import com.amazonaws.ivs.player.MediaType
import com.amazonaws.ivs.player.Player
import com.amazonaws.ivs.player.TextMetadataCue
import com.babyfilx.data.enums.PlayingState
import com.babyfilx.data.models.DetailsModel
import com.babyfilx.data.models.OptionDataItem
import com.babyfilx.data.models.response.HomeEntriesModel
import com.babyfilx.data.repositories.PlayerRepository
import com.babyfilx.utils.ivs.Configuration
import com.babyfilx.utils.ivs.setListener
import com.babyfilx.utils.logs.loge
import com.babyfilx.utils.timeString
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.nio.charset.StandardCharsets
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    @SuppressLint("StaticFieldLeak") @ApplicationContext private val context: Context,
    private val playerRepository: PlayerRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private var player: MediaPlayer? = null
    private var playerListener: Player.Listener? = null
    var downloadUrl = MutableLiveData<String>(checkNotNull(savedStateHandle["url"]))
    var nodeId = MutableLiveData<String>(checkNotNull(savedStateHandle["nodeId"]))
    val model: DetailsModel = checkNotNull(savedStateHandle["data"])

    private val handler = Handler(Looper.getMainLooper())
    private val updateSeekBarTask = object : Runnable {
        override fun run() {
            progress.value = player?.position?.timeString()
            seekBarProgress.value = player?.position?.toInt()
            seekBarSecondaryProgress.value = player?.bufferedPosition?.toInt()
            if (liveStream.value == false) handler.postDelayed(this, 500)
        }
    }
    private val url = MutableLiveData<String>()
    val playerState = MutableLiveData<Player.State>()
    val buttonState = MutableLiveData<PlayingState>()
    val liveStream = MutableLiveData<Boolean>()
    val selectedRateValue = MutableLiveData<String>()
    val selectedQualityValue = MutableLiveData<String>()
    val duration = MutableLiveData<String>()
    val progress = MutableLiveData<String>()
    var valuesModel: HomeEntriesModel? = null
    val durationVisible = MutableLiveData<Boolean>()
    val progressVisible = MutableLiveData<Boolean>()
    val seekBarVisible = MutableLiveData<Boolean>()
    val showControls = MutableLiveData<Boolean>()
    val buffering = MutableLiveData<Boolean>()
    val isPlaying = MutableLiveData<Boolean>()

    val seekBarMax = MutableLiveData<Int>()
    val seekBarProgress = MutableLiveData<Int>()
    val seekBarSecondaryProgress = MutableLiveData<Int>()

    val volumeMax = MutableLiveData<Int>()
    val volume = MutableLiveData<Int>()

    val playerParamsChanged = MutableLiveData<Pair<Int, Int>>()
    val errorHappened = MutableLiveData<Pair<String, String>>()

    val qualities = MutableLiveData<List<OptionDataItem>>()
    // val sources = MutableLiveData<List<SourceDataItem>>()

    init {
//        initPlayer()
//        setPlayerListener()
//        initDefault()
//        //getSources()
//        callVideoApi()

    }

//    fun callVideoApi() {
//        loge("downloadUrl $downloadUrl")
//        viewModelScope.launch {
//            playerRepository.getPlayingUrl(downloadUrl.value).collectLatest {
//
//
//            }
//        }
//    }


    private fun initDefault() {
        buttonState.value = PlayingState.PLAYING
        url.value = Configuration.LINK
        selectedRateValue.value = Configuration.PLAYBACK_RATE_DEFAULT
        selectedQualityValue.value = Configuration.AUTO
    }

    private fun initPlayer() {
        // Media player initialization
        player = MediaPlayer(context)
    }

    private fun setPlayerListener() {
        // Media player listener creation and initialization
        playerListener = player?.setListener(
            onVideoSizeChanged = { width, height ->
                loge("Video size changed: $width $height")
                playerParamsChanged.value = Pair(width, height)
            },
            onCue = { cue ->
                if (cue is TextMetadataCue) {
                    loge("Received Text Metadata: ${cue.text}")
                }
            },
            onDurationChanged = { durationValue ->
                loge("Duration changed: $durationValue")
                if (player?.duration != null && player!!.duration > 0L) {
                    // Switch to VOD player controls
                    seekBarMax.value = durationValue.toInt()
                    liveStream.value = false
                    duration.value = durationValue.timeString()
                    durationVisible.value = true
                    progressVisible.value = true
                    updateSeekBarTask.run()
                } else {
                    // Switch to Live player controls
                    liveStream.value = true
                    durationVisible.value = false
                    progressVisible.value = false
                    seekBarVisible.value = false
                }
            },
            onStateChanged = { state ->
                loge("State changed: $state")
                playerState.value = state
            },
            onVolumeChanged = { volumeValue ->
                loge("Volume: ${volumeValue}")
                val v: Float = player!!.volume
                val volumePercent = (v / volumeMax.value!! * 100).toInt()
                volume.value = volumePercent
            },
            onMetadata = { data, buffer ->
                if (MediaType.TEXT_PLAIN == data) {
                    val textData = StandardCharsets.UTF_8.decode(buffer)
                    loge("Received Timed Metadata: $textData")
                }
            },
            onError = { exception ->
                loge("Error happened: $exception")
                errorHappened.value = Pair(exception.code.toString(), exception.errorMessage)
                isPlaying.value = false
            }
        )
    }

    fun toggleControls(show: Boolean) {
        loge("Toggling controls: $show")
        showControls.value = show
        seekBarVisible.value = show
    }

    fun play() {
        loge("Starting playback")
        // Starts or resumes playback of the stream.
        player?.play()
    }

    fun pause() {
        loge("Pausing playback")
        // Pauses playback of the stream.
        player?.pause()
    }

    fun playerRelease() {
        loge("Releasing player")
        // Removes a playback state listener
        playerListener?.let { player?.removeListener(it) }
        // Releases the player instance
        player?.release()
        player = null
    }

    fun playerStart(surface: Surface) {
        loge("Starting player")
        initPlayer()
        updateSurface(surface)
        setPlayerListener()
        playerLoadStream(Uri.parse(url.value))
        play()
    }

    fun playerLoadStream(uri: Uri) {
        loge("Loading stream URI: $uri")
        // Loads the specified stream
        player?.load(uri)
    }

    fun updateSurface(surface: Surface?) {
        loge("Updating player surface: $surface")
        // Sets the Surface to use for rendering video
        player?.setSurface(surface)
    }

    fun playerSeekTo(position: Long) {
        loge("Updating player position: $position")
        // Seeks to a specified position in the stream, in milliseconds
        player?.seekTo(position)
        progress.value = player?.position?.timeString()
    }

    fun playerVolumeSeekTo(position: Long) {
        loge("Updating player position: $position")
        // Seeks to a specified position in the stream, in milliseconds
        player?.volume ?: position
    }

    fun selectQuality(option: String) {
        loge("Set player quality: $option")
        selectedQualityValue.value = option
        player?.qualities?.find { it.name == option }?.let { quality ->
            // Sets the quality of the stream.
            player?.quality = quality
        }
    }

    fun selectAuto() {
        // Enables automatic quality selection (ABR Adaptive bitrate)
        player?.isAutoQualityMode = true
        selectedQualityValue.value = Configuration.AUTO
    }

    fun getPlayerQualities() {
        val qualityList: MutableList<OptionDataItem> =
            mutableListOf(
                OptionDataItem(
                    Configuration.AUTO,
                    selectedQualityValue.value == Configuration.AUTO || selectedQualityValue.value == null
                )
            )
        val list = player?.qualities?.map {
            OptionDataItem(it.name, selectedQualityValue.value == it.name)
        } ?: listOf()
        qualityList.addAll(list)
        qualities.value = qualityList
    }

    fun getPlayBackRates(): List<OptionDataItem> {
        return Configuration.PlaybackRate.toMutableList().map {
            OptionDataItem(
                it,
                selectedRateValue.value == it || selectedQualityValue.value == Configuration.PLAYBACK_RATE_DEFAULT
            )
        }
    }

    fun setPlaybackRate(option: String) {
        loge("Setting playback rate: $option")
        player?.playbackRate = option.toFloat()
        selectedRateValue.value = option
    }

    private fun getSources() {
        loge("Collecting sources")
        /* launchMain {
             cacheProvider.sourcesDao().getAll().collect {
                 val itemList: MutableList<SourceDataItem> = mutableListOf(
                     SourceDataItem(Configuration.LIVE_PORTRAIT_LINK, Configuration.PORTRAIT_OPTION),
                     SourceDataItem(Configuration.RECORDED_LANDSCAPE_LINK, Configuration.LANDSCAPE_OPTION)
                 )
                 itemList.addAll(it)
                 sources.value = itemList
             }
         }*/
    }

    fun deleteSource(url: String) {
        loge("Deleting source: $url")
        /* launchIO {
             cacheProvider.sourcesDao().delete(url)
         }*/
    }

/* fun addSource(source: SourceDataItem) {
     loge( "Adding source: $source")
   *//*  launchIO {
            cacheProvider.sourcesDao().insert(source)
        }*//*
    }*/

}
