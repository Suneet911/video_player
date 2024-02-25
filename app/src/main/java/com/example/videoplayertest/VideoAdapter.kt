package com.example.videoplayertest

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.videoplayertest.databinding.ListVideoBinding
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player.Listener
import com.google.android.exoplayer2.Player.REPEAT_MODE_ONE
import com.google.android.exoplayer2.Player.STATE_BUFFERING
import com.google.android.exoplayer2.Player.STATE_READY
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSource.Factory
import com.google.android.exoplayer2.ExoPlayer as ExoPlayer1

class VideoAdapter(var context: Context, var videos: ArrayList<VideoModel> , var videoPreparedListener: OnVideoPreparedListener) :
    RecyclerView.Adapter<VideoAdapter.ViewHolder>() {

    class ViewHolder(val binding: ListVideoBinding, private val context: Context, var videoPreparedListener: OnVideoPreparedListener) :
        RecyclerView.ViewHolder(binding.root) {

            private lateinit var exoPlayer : ExoPlayer1
            private lateinit var mediaSource : MediaSource

            fun setVideoPath(url : String){
                exoPlayer = ExoPlayer1.Builder(context).build()
                exoPlayer.addListener(object : Listener{
                    override fun onPlayerError(error: PlaybackException) {
                        super.onPlayerError(error)
                        Toast.makeText(context, "Can't Play This Video", Toast.LENGTH_SHORT).show()
                    }

                    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                        if (playbackState == STATE_BUFFERING){
                            binding.progressBar.visibility= View.VISIBLE
                        }else if (playbackState== STATE_READY){
                            binding.progressBar.visibility= View.INVISIBLE
                        }
                    }
                })

                binding.videoPlayer.player= exoPlayer

                val videoPlayer= binding.videoPlayer
                videoPlayer.setOnClickListener {
                    if (videoPlayer.player?.isPlaying == true){
                        binding.playOrPause.visibility= View.VISIBLE
                        videoPlayer.player!!.pause()
                        binding.playOrPause.setImageResource(R.drawable.btn_pause)
                        binding.playOrPause.postDelayed({
                            binding.playOrPause.visibility= View.GONE
                        }, 500)
                    }
                    else{
                        videoPlayer.player!!.play()
                        binding.playOrPause.visibility= View.VISIBLE
                        binding.playOrPause.setImageResource(R.drawable.btn_play)
                        binding.playOrPause.postDelayed({
                            binding.playOrPause.visibility= View.GONE
                        }, 500)
                    }
                }

                exoPlayer.seekTo(0)
                exoPlayer.repeatMode= REPEAT_MODE_ONE

                val dataSourceFactory = Factory(context)

                mediaSource= ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(
                    MediaItem.fromUri(Uri.parse(url)))

                exoPlayer.setMediaSource(mediaSource)
                exoPlayer.prepare()

                if (absoluteAdapterPosition == 0){
                    exoPlayer.playWhenReady= true
                    exoPlayer.play()
                }

                videoPreparedListener.onVideoPrepared(ExoPlayerItem(exoPlayer, absoluteAdapterPosition))
            }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view= ListVideoBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(view, context, videoPreparedListener)
    }

    override fun getItemCount(): Int {
        return  videos.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = videos[position]

        holder.binding.title.text= model.title
        holder.binding.description.text=model.description

        var like= true

        holder.binding.btnLike.setOnClickListener {
            if (like){
                holder.binding.btnLike.setImageResource(R.drawable.like_red)
            }
            else{
                holder.binding.btnLike.setImageResource(R.drawable.like)
            }
            like= !like
        }
        holder.setVideoPath(model.url)
    }

    interface OnVideoPreparedListener{
        fun onVideoPrepared(exoplayerItem : ExoPlayerItem)
    }
}