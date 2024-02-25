package com.example.videoplayertest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import androidx.viewpager2.widget.ViewPager2
import com.example.videoplayertest.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private lateinit var adapter: VideoAdapter
    private val videos = ArrayList<VideoModel>()
    private val exoPlayerItems = ArrayList<ExoPlayerItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        videos.add(
            VideoModel(
                title = "Mountains",
                url = "android.resource://"+ packageName+ "/" + R.raw.video7,
                description = "The first Blender Open Movie from 2006"
            )
        )
        videos.add(
            VideoModel(
                title = "Mountains",
                url = "android.resource://"+ packageName+ "/" + R.raw.video6,
                        description = "The first Blender Open Movie from 2006"
            )
        )

        videos.add(
            VideoModel(
                title = "For Bigger Escape",
                url = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4",
                description = "The first Blender Open Movie from 2006"
            )
        )

        videos.add(
            VideoModel(
                title = "Blender Open Movie from 2006",
                url = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4",
                description = "The first Blender Open Movie from 2006"
            )
        )

        videos.add(
            VideoModel(
                title = "Big Buck Bunny",
                url = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
                description = "The first Blender Open Movie from 2006"
            )
        )


        adapter = VideoAdapter(this, videos, object : VideoAdapter.OnVideoPreparedListener {
            override fun onVideoPrepared(exoplayerItem: ExoPlayerItem) {
                exoPlayerItems.add(exoplayerItem)
            }

        })
        binding.viewPager.adapter = adapter

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                val previousIndex = exoPlayerItems.indexOfFirst { it.exoPlayer.isPlaying }
                if (previousIndex != -1) {
                    val player = exoPlayerItems[previousIndex].exoPlayer
                    player.pause()
                    player.playWhenReady = false
                }

                val newIndex = exoPlayerItems.indexOfFirst { it.position == position }
                if (newIndex != -1) {
                    val player = exoPlayerItems[newIndex].exoPlayer
                    player.playWhenReady = true
                    player.play()
                }

            }
        })
    }

    override fun onPause() {
        super.onPause()

        val index = exoPlayerItems.indexOfFirst { it.position == binding.viewPager.currentItem }
        if (index != -1) {
            val player = exoPlayerItems[index].exoPlayer
            player.pause()
            player.playWhenReady = false
        }
    }

    override fun onResume() {
        super.onResume()

        val index = exoPlayerItems.indexOfFirst { it.position == binding.viewPager.currentItem }
        if (index != -1) {
            val player = exoPlayerItems[index].exoPlayer
            player.playWhenReady = true
            player.playWhenReady
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (exoPlayerItems.isNotEmpty()) {
            for (item in exoPlayerItems) {
                val player = item.exoPlayer
                player.stop()
                player.clearMediaItems()
            }
        }
    }
}