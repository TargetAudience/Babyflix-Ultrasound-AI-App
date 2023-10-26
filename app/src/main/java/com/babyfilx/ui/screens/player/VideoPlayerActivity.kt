package com.babyfilx.ui.screens.player


import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.IntentFilter
import android.content.pm.ActivityInfo
import android.media.AudioManager
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.webkit.WebViewClient
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.babyfilx.adapter.GenericRecyclerViewAdapter
import com.babyfilx.api.apistate.Response
import com.babyfilx.base.App.Companion.isFirst
import com.babyfilx.data.models.response.HomeEntriesModel
import com.babyfilx.ui.screens.home.HomeViewModel
import com.babyfilx.utils.extentions.download
import com.babyfilx.utils.ivs.BecomingNoisyReceiver
import com.babyfilx.utils.logs.loge
import com.babyfilx.utils.shareData
import com.babyfilx.utils.showDialogs
import com.babyfilx.utils.toast
import com.babyflix.mobileapp.R
import com.babyflix.mobileapp.databinding.ActivityVideoPlayerBinding
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.gms.ads.AdRequest
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Collections

@AndroidEntryPoint
class
VideoPlayerActivity : AppCompatActivity() {
    private val playbackStateListener: Player.Listener = playbackStateListener()
    val context = this
    var volume = 0
    private lateinit var binding: ActivityVideoPlayerBinding
    private var myNoisyAudioStreamReceiver: BecomingNoisyReceiver? = null
    private val intentFilter = IntentFilter("android.media.VOLUME_CHANGED_ACTION")
    private var clipboard: ClipboardManager? = null
    var simpleExoPlayer: ExoPlayer? = null
    val viewModel: PlayerViewModel by viewModels()
    val homeViewModel: HomeViewModel by viewModels()
    private var videoRecyclerViewAdapter: GenericRecyclerViewAdapter? = null
    private var allVideo = ArrayList<HomeEntriesModel>()
    var audioManager: AudioManager? = null
    var iVFullScreen: ImageView? = null
    var mute: ImageView? = null
    var volumeBar: SeekBar? = null
    private var flag: Boolean = true
    private var isMute: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        iVFullScreen = binding.player.findViewById(R.id.exo_fullscreen)
        volumeBar = binding.player.findViewById(R.id.exo_audio_track)
        mute = binding.player.findViewById(com.babyflix.mobileapp.R.id.volume_view)
        audioManager = context.getSystemService(AUDIO_SERVICE) as AudioManager
        clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        // playVideo()
        initializePlayer()
        videoList()
        onClickList()
        getUpdatedVolume()
        onBackPresseds()
        initializeAdd()
//        myNoisyAudioStreamReceiver = BecomingNoisyReceiver {
//            getUpdatedVolume()
//        }
//
//        registerReceiver(myNoisyAudioStreamReceiver, intentFilter)


    }

    private fun initializeAdd() {
//        <iframe id='a3f19631' name='a3f19631' src='https://servedby.aqua-adserver.com/afr.php?zoneid=10179&amp;cb=INSERT_RANDOM_NUMBER_HERE' frameborder='0' scrolling='no' width='375' height='144' allow='autoplay'><a href='https://servedby.aqua-adserver.com/ck.php?n=a3fc1c34&amp;cb=INSERT_RANDOM_NUMBER_HERE' target='_blank'><img src='https://servedby.aqua-adserver.com/avw.php?zoneid=10179&amp;cb=INSERT_RANDOM_NUMBER_HERE&amp;n=a3fc1c34' border='0' alt='' /></a></iframe>

//        binding.playerWeb.settings.javaScriptEnabled = true
//        binding.playerWeb.loadUrl("https://servedby.aqua-adserver.com/afr.php?zoneid=10179")

        val adRequest = AdRequest.Builder().build()
        binding.adView.loadAd(adRequest)
    }

    private fun onBackPresseds() {
        onBackPressedDispatcher.addCallback(
            this /* lifecycle owner */,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (!flag)
                        screen()
                    else
                        finish()
                }
            })
    }

    private fun onClickList() {
        binding.shareVideoLayout.setOnClickListener {
            context.shareData(
                "Check out my imagery shared from BabyFlix " + "https://www.babyflix.net/" + "m/" + Integer.toHexString(
                    viewModel.nodeId.value.toString().toInt()
                )
            )
        }

        binding.backButton.setOnClickListener {
            finish()
        }
        binding.copyLinkLayout.setOnClickListener {
            clipboard!!.setPrimaryClip(ClipData.newPlainText("", viewModel.downloadUrl.value))
            toast("Copied")
        }

        iVFullScreen?.setOnClickListener {
            screen()
        }
        mute?.setOnClickListener {
            volumeChange()
        }

        volumeBar?.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                audioManager!!.setStreamVolume(AudioManager.STREAM_MUSIC, i, 0)
                if (i == 0) {
                    isMute = false
                    mute?.setImageResource(R.drawable.mute)
                } else {
                    isMute = true
                    mute?.setImageResource(R.drawable.volume)
                }

            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })

        binding.downloadLinkLayout.setOnClickListener {
            context.download(viewModel.valuesModel!!)
        }

        binding.deleteLinkLayout.setOnClickListener {
            context.showDialogs(getString(R.string.delete_message)) {
                loge("call delete")
                homeViewModel.deleteAPiCall(viewModel.nodeId.value!!, 0)
            }
        }
        lifecycleScope.launch {
            homeViewModel.deleteResponse.collectLatest {
                when (it) {
                    is Response.Error -> {
                        context.toast(it.message)
                        binding.progressBar1.isVisible = false
                    }
                    is Response.Loading -> {
                        binding.progressBar1.isVisible = true
                    }
                    is Response.Success -> {
                        binding.progressBar1.isVisible = false
                        isFirst = true
                        finish()
                    }
                }
            }
        }

    }

    private fun volumeChange() {
        isMute = if (isMute) {
            mute?.setImageResource(R.drawable.mute)
            volumeBar?.progress = 0
            false
        } else {
            mute?.setImageResource(R.drawable.volume)
            volumeBar?.progress = if (volume == 0) 20 else volume
            true
        }
    }

    private fun screen() {
        if (flag) {
            hideSystemUi()
            flag = false
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            binding.player.layoutParams.height =
                ConstraintLayout.LayoutParams.MATCH_PARENT
            binding.layout.visibility = View.GONE

        } else {
            hideSystemUi()
            flag = true
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            binding.player.layoutParams.height =
                resources.getDimension(R.dimen.player_control_landscape_width).toInt()
            binding.layout.visibility = View.VISIBLE

        }
    }


    private fun initializePlayer() {
        val trackSelector = DefaultTrackSelector(this).apply {
            setParameters(buildUponParameters().setMaxVideoSizeSd())
        }
        simpleExoPlayer = ExoPlayer.Builder(this)
            .setTrackSelector(trackSelector)
            .setSeekBackIncrementMs(10000)
            .setSeekForwardIncrementMs(10000)
            .build()
            .also { exoPlayer ->
                val mediaItem = MediaItem.fromUri(viewModel.downloadUrl.value!!)
                exoPlayer.setMediaItem(mediaItem)
                binding.player.player = exoPlayer

            }
        simpleExoPlayer!!.playWhenReady = true
        simpleExoPlayer!!.prepare()
        simpleExoPlayer!!.addListener(playbackStateListener)

    }

    private fun videoList() {
        loge("datamodels ${viewModel.model}")
        allVideo = viewModel.model.list.filter {
            if (it.download_url == viewModel.downloadUrl.value)
                viewModel.valuesModel = it
            it.download_url != viewModel.downloadUrl.value
        } as ArrayList<HomeEntriesModel>
        videoRecyclerViewAdapter =
            GenericRecyclerViewAdapter { it, pos ->
                viewModel.downloadUrl.value = it.download_url
                viewModel.nodeId.value = it.node_id
                viewModel.valuesModel = it
                simpleExoPlayer?.release()
                initializePlayer()
                videoList()
            }
        binding.videoRv.adapter = videoRecyclerViewAdapter
        videoRecyclerViewAdapter!!.submitList(allVideo)
    }


    private fun playbackStateListener() = object : Player.Listener {
        override fun onPlaybackStateChanged(playbackState: Int) {
            when (playbackState) {
                //  ExoPlayer.STATE_IDLE -> "ExoPlayer.STATE_IDLE      -"
                ExoPlayer.STATE_BUFFERING -> binding.progressBar.visibility = View.VISIBLE
                ExoPlayer.STATE_READY -> binding.progressBar.visibility = View.GONE
                //ExoPlayer.STATE_ENDED -> "ExoPlayer.STATE_ENDED     -"
                // else -> "UNKNOWN_STATE             -"
            }
        }
    }

    private fun getUpdatedVolume() {
        // on below line we are creating variables for
        // volume level, max volume, volume percent.
        val volumeLevel = audioManager!!.getStreamVolume(AudioManager.STREAM_MUSIC)
        val maxVolumeLevel = audioManager!!.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        volumeBar?.max = maxVolumeLevel
        volumeBar?.progress = volumeLevel
        volume = volumeLevel
    }

    override fun onStop() {
        super.onStop()
        simpleExoPlayer?.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        // unregisterReceiver(myNoisyAudioStreamReceiver)
        releasePlayer()
    }

    override fun onResume() {
        super.onResume()
        // hideSystemUi()
        /*if ((Util.SDK_INT <= 23 || simpleExoPlayer == null)) {
            initializePlayer()
        }*/
    }

    @SuppressLint("InlinedApi")
    private fun hideSystemUi() {
        WindowInsetsControllerCompat(window, binding.player).let { controller ->
            if (flag) {
                WindowCompat.setDecorFitsSystemWindows(window, false)
                controller.hide(WindowInsetsCompat.Type.systemBars())
            } else {
                WindowCompat.setDecorFitsSystemWindows(window, true)
                controller.show(WindowInsetsCompat.Type.systemBars())
            }

            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    private fun releasePlayer() {
        simpleExoPlayer?.let { exoPlayer ->
            exoPlayer.removeListener(playbackStateListener)
            exoPlayer.release()
        }
        simpleExoPlayer = null
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (event.keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            volumeBar?.progress = audioManager!!.getStreamVolume(AudioManager.STREAM_MUSIC)
            volume = volumeBar?.progress ?: 20
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
        if (event.keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            volumeBar?.progress = audioManager!!.getStreamVolume(AudioManager.STREAM_MUSIC)
            volume = volumeBar?.progress ?: 20
        }
        return super.onKeyUp(keyCode, event)
    }


}