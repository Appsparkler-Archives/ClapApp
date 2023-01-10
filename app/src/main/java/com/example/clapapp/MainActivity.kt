package com.example.clapapp

import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore.Audio.Media
import android.view.View
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {
    private lateinit var seekBar: SeekBar
    private var mediaPlayer:MediaPlayer? = null
    private lateinit var runnable:Runnable
    private lateinit var handler: Handler
    private lateinit var textViewPlayed: TextView
    private lateinit var textViewDue: TextView

    private fun setPlayedAndDueTimes() {
        val textViewPlayed = findViewById<TextView>(R.id.textViewPlayed)
        val textViewDue = findViewById<TextView>(R.id.textViewDue)
        val played = "00:00"
        val mediaPlayer:MediaPlayer = MediaPlayer.create(this, R.raw.clapping)
        val due = mediaPlayer.duration
        textViewPlayed.text = played
        val remainingSeconds = due / 1000
        textViewDue.text = String.format("%02d:%02d", remainingSeconds / 60, remainingSeconds % 60)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setPlayedAndDueTimes()
        val playButton = findViewById<FloatingActionButton>(R.id.floatingActionButtonPlay)
        val pauseButton = findViewById<FloatingActionButton>(R.id.floatingActionButtonPause)
        val stopButton = findViewById<FloatingActionButton>(R.id.floatingActionButtonStop)
        seekBar = findViewById<SeekBar>(R.id.seekBarClapping)

        playButton.setOnClickListener {
            if(mediaPlayer == null){
                mediaPlayer = MediaPlayer.create(this, R.raw.clapping)
                initializeSeekbar()
            }
            mediaPlayer?.start()
        }

        pauseButton.setOnClickListener {
            mediaPlayer?.pause()
        }

        stopButton.setOnClickListener {
            mediaPlayer?.stop()
            mediaPlayer?.reset()
            mediaPlayer?.release()
            mediaPlayer = null
            handler.removeCallbacks(runnable)
            seekBar.progress = 0
            textViewPlayed.text = "00:00"
        }
    }

    private fun initializeSeekbar() {
        seekBar.max = mediaPlayer?.duration!!
        handler = Handler(Looper.getMainLooper())
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    mediaPlayer?.seekTo(progress)
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })

        runnable = Runnable {
            val textViewPlayed = findViewById<TextView>(R.id.textViewPlayed)
            val textViewDue = findViewById<TextView>(R.id.textViewDue)
            handler.postDelayed(runnable, 1000)
            seekBar.progress = mediaPlayer!!.currentPosition
            setCurrentPositionOnTextView(textViewPlayed, mediaPlayer)
            setDuePositionOnTextView(textViewDue, mediaPlayer)
            getRemainingTime(mediaPlayer)
        }
        handler.postDelayed(runnable, 1000)
    }

    private fun setCurrentPositionOnTextView(textView: TextView, mediaPlayer: MediaPlayer?) {
        val seconds = mediaPlayer?.currentPosition!! / 1000
        textView.text = String.format("%02d:%02d", seconds / 60, seconds % 60)
    }

    private fun getRemainingTime(mediaPlayer: MediaPlayer?) {
        val seconds = mediaPlayer?.duration!! / 1000
        val remainingSeconds = seconds - mediaPlayer?.currentPosition!! / 1000
//        textView.text = String.format("%02d:%02d", remainingSeconds / 60, remainingSeconds % 60)
    }

    private fun setDuePositionOnTextView(textView: TextView, mediaPlayer: MediaPlayer?) {
        val seconds = mediaPlayer?.duration!! / 1000
        textView.text = String.format("%02d:%02d", seconds / 60, seconds % 60)
    }
}
