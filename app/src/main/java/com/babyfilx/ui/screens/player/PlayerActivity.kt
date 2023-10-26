package com.babyfilx.ui.screens.player

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.IntentFilter
import android.content.pm.ActivityInfo
import android.media.AudioManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import android.widget.SeekBar
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Observer
import com.amazonaws.ivs.player.Player
import com.babyfilx.adapter.GenericRecyclerViewAdapter
import com.babyfilx.data.enums.PlayingState
import com.babyfilx.data.models.response.HomeEntriesModel
import com.babyfilx.utils.ivs.BecomingNoisyReceiver
import com.babyfilx.utils.ivs.Configuration.HIDE_CONTROLS_DELAY
import com.babyfilx.utils.ivs.launchMain
import com.babyfilx.utils.ivs.showDialog
import com.babyfilx.utils.logs.loge
import com.babyfilx.utils.shareData
import com.babyfilx.utils.toast
import com.babyflix.mobileapp.R
import com.babyflix.mobileapp.databinding.ActivityPlayerBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PlayerActivity : AppCompatActivity(), SurfaceHolder.Callback {
    private lateinit var binding: ActivityPlayerBinding
    private var myNoisyAudioStreamReceiver: BecomingNoisyReceiver? = null
    private val intentFilter = IntentFilter("android.media.VOLUME_CHANGED_ACTION")
    private var clipboard: ClipboardManager? = null
    val context = this
    val viewModel: PlayerViewModel by viewModels()
    private var flag: Boolean = true
    var audioManager: AudioManager? = null
    private var videoRecyclerViewAdapter: GenericRecyclerViewAdapter? = null
    private var allVideo = ArrayList<HomeEntriesModel>()
    private val timerHandler = Handler(Looper.getMainLooper())
    private val timerRunnable = kotlinx.coroutines.Runnable {
        launchMain {
            loge("Hiding controls")
            viewModel.toggleControls(false)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        audioManager = context.getSystemService(AUDIO_SERVICE) as AudioManager
        clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        supportActionBar?.hide()
        binding.lifecycleOwner = context
        binding.data = viewModel
        initializations()

        myNoisyAudioStreamReceiver = BecomingNoisyReceiver {
            getUpdatedVolume()
        }

        registerReceiver(myNoisyAudioStreamReceiver, intentFilter)


    }


    private fun initializations() {

        binding.videoRv.adapter = videoRecyclerViewAdapter
        getUpdatedVolume()
        observers()
        initSurface()
        initButtons()
        videoList()

        viewModel.playerStart(binding.surfaceView.holder.surface)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                when {
                    else -> finish()
                }
            }
        })
    }


    private fun getUpdatedVolume() {
        // on below line we are creating variables for
        // volume level, max volume, volume percent.
        val volumeLevel = audioManager!!.getStreamVolume(AudioManager.STREAM_MUSIC)
        val maxVolumeLevel = audioManager!!.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        val volumePercent = (volumeLevel.toFloat() / maxVolumeLevel * 100).toInt()
        viewModel.volumeMax.value = maxVolumeLevel
        viewModel.volume.value = volumePercent
    }

    private fun videoList() {
        loge("datamodels ${viewModel.model}")
//        allVideo = viewModel.model.list.filter {
//          //  it.download_url != viewModel.downloadUrl
//        } as ArrayList<HomeEntriesModel>
//        videoRecyclerViewAdapter =
//            GenericRecyclerViewAdapter {
//                viewModel.downloadUrl = it.download_url
//           //     viewModel.callVideoApi()
//            }

        binding.videoRv.adapter = videoRecyclerViewAdapter

        videoRecyclerViewAdapter!!.submitList(allVideo)
    }


    /**
     * this is for observer related data
     */
    private fun observers() {
        binding.shareVideoLayout.setOnClickListener {
            context.shareData(viewModel.downloadUrl.value.toString())
        }

        binding.copyLinkLayout.setOnClickListener {
            clipboard!!.setPrimaryClip(ClipData.newPlainText("", viewModel.downloadUrl.value))
            toast("Copied")
        }

        viewModel.playerState.observe(this, Observer { state ->
            when (state) {
                Player.State.BUFFERING -> {
                    // Indicates that the Player is buffering content
                    viewModel.buffering.value = true
                    viewModel.buttonState.value = PlayingState.PLAYING
                }
                Player.State.IDLE -> {
                    // Indicates that the Player is idle
                    viewModel.buffering.value = false
                    viewModel.buttonState.value = PlayingState.PAUSED
                }
                Player.State.READY -> {
                    // Indicates that the Player is ready to play the loaded source
                    viewModel.buffering.value = false
                    viewModel.buttonState.value = PlayingState.PAUSED
                }
                Player.State.ENDED -> {
                    // Indicates that the Player reached the end of the stream
                    viewModel.buffering.value = false
                    viewModel.buttonState.value = PlayingState.PAUSED
                }
                Player.State.PLAYING -> {
                    // Indicates that the Player is playing
                    viewModel.buffering.value = false
                    viewModel.buttonState.value = PlayingState.PLAYING
                }
                else -> { /* Ignored */
                }
            }
        })

        viewModel.buttonState.observe(this, Observer { state ->
            viewModel.isPlaying.value = state == PlayingState.PLAYING
        })

        viewModel.playerParamsChanged.observe(this, Observer {
            loge("Player layout params changed ${it.first} ${it.second}")
            //fitSurfaceToView(surface_view, it.first, it.second)
        })

        viewModel.errorHappened.observe(this, Observer {
            loge("Error dialog is shown")
            showDialog(it.first, it.second)
        })


        binding.controllers.seekBarVolume?.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, progress: Int, p2: Boolean) {
                audioManager!!.setStreamVolume(
                    AudioManager.STREAM_MUSIC,
                    progress, 0
                )
                getUpdatedVolume()
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {}

            override fun onStopTrackingTouch(p0: SeekBar?) {}

        })

    }

//    override fun onConfigurationChanged(newConfig: Configuration) {
//        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE)
//            screen()
//
//        super.onConfigurationChanged(newConfig)
//
//    }

    override fun onResume() {
        super.onResume()
        viewModel.play()
    }

    override fun onPause() {
        super.onPause()
        viewModel.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.playerRelease()
        unregisterReceiver(myNoisyAudioStreamReceiver)
        binding.surfaceView.holder.removeCallback(context)
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        /* Ignored */
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        loge("Surface destroyed")
        viewModel.updateSurface(null)
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        loge("Surface created")
        viewModel.updateSurface(holder.surface)
    }

    private fun initSurface() {
        binding.surfaceView.holder.addCallback(this)
        binding.playerRoot.setOnClickListener {
            loge("Player screen clicked")
            when (binding.controllers.playerControls.visibility) {
                View.VISIBLE -> {
                    viewModel.toggleControls(false)
                }
                View.GONE -> {
                    viewModel.toggleControls(true)
                    restartTimer()
                }
            }
        }

        binding.controllers.seekBar.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                restartTimer()
                if (fromUser) {
                    viewModel.playerSeekTo(progress.toLong())
                }
            }
        })


    }

    private fun initButtons() {
        binding.controllers.apply {
            playButtonView.setOnClickListener {
                restartTimer()
                when (viewModel.buttonState.value) {
                    PlayingState.PLAYING -> {
                        viewModel.buttonState.value = PlayingState.PAUSED
                        viewModel.pause()
                    }
                    else -> {
                        viewModel.buttonState.value = PlayingState.PLAYING
                        viewModel.play()
                    }
                }
            }

            playButton.setOnClickListener {
                restartTimer()
                when (viewModel.buttonState.value) {
                    PlayingState.PLAYING -> {
                        viewModel.buttonState.value = PlayingState.PAUSED
                        viewModel.pause()
                    }
                    else -> {
                        viewModel.buttonState.value = PlayingState.PLAYING
                        viewModel.play()
                    }
                }
            }

            fullScreenView.setOnClickListener {
                screen()
            }

            restartTimer()
        }
    }


    private fun screen() {
        if (flag) {
            flag = false
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            binding.videoPlayer.layoutParams.height =
                ConstraintLayout.LayoutParams.WRAP_CONTENT
        } else {
            flag = true
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            binding.videoPlayer.layoutParams.height =
                resources.getDimension(R.dimen.player_control_landscape_width).toInt()
        }
    }

    private fun restartTimer() {
        timerHandler.removeCallbacks(timerRunnable)
        timerHandler.postDelayed(timerRunnable, HIDE_CONTROLS_DELAY)
    }

    private fun fitSurfaceToView(surfaceView: SurfaceView, width: Int, height: Int) {
        val parent = surfaceView.parent as View
        val oldWidth = parent.width
        val oldHeight = parent.height
        val newWidth: Int
        val newHeight: Int
        val ratio = height.toFloat() / width.toFloat()
        if (oldHeight.toFloat() > oldWidth.toFloat() * ratio) {
            newWidth = oldWidth
            newHeight = (oldWidth.toFloat() * ratio).toInt()
        } else {
            newWidth = (oldHeight.toFloat() / ratio).toInt()
            newHeight = oldHeight
        }
        val layoutParams = surfaceView.layoutParams
        layoutParams.width = newWidth
        layoutParams.height = newHeight
        surfaceView.layoutParams = layoutParams
    }


    override fun onBackPressed() {
        if (flag) {
            finish()
        } else {
            screen()
        }

    }

}