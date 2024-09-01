package com.rkoma.recorderapp

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.appcompat.widget.Toolbar

class AudioPlayer : AppCompatActivity() {

    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var playbtn: ImageButton
    private lateinit var seekBar: SeekBar
    private lateinit var toolbar: Toolbar
    private lateinit var showfilename : TextView
    private var isPlaying = false

    private lateinit var runnable: Runnable
    private lateinit var handler: Handler
    private var delay = 100L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_audio_player)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.third)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        playbtn= findViewById(R.id.playerbtn)
        seekBar= findViewById(R.id.seekbar)
        toolbar= findViewById(R.id.toolbar)
        showfilename= findViewById(R.id.showfilename)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }


        val filepath = intent.getStringExtra("filepath")
        val filename = intent.getStringExtra("filename")

        showfilename.text = filename

        mediaPlayer = MediaPlayer()

        if (filepath != null) {
            mediaPlayer = MediaPlayer().apply {
                setDataSource(filepath)
                prepare()
            }
        }



        handler = Handler(Looper.getMainLooper())
        runnable = Runnable{
            seekBar.progress = mediaPlayer.currentPosition
            handler.postDelayed(runnable, delay)
        }

        playbtn.setOnClickListener(){
            startPlayback()
        }

        //inizializzo la seekbar
        seekBar.max= mediaPlayer.duration


        mediaPlayer.setOnCompletionListener {
            playbtn.setImageResource(R.drawable.ic_play)
        }

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    mediaPlayer.seekTo(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // Puoi mettere in pausa il MediaPlayer mentre l'utente interagisce con la SeekBar
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                // Riprendi la riproduzione se era in pausa
                mediaPlayer.start()
            }
        })
        // Registra il callback per il pulsante indietro
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Logica personalizzata per il pulsante indietro
                mediaPlayer.stop()
                mediaPlayer.release()
                handler.removeCallbacks(runnable)
                // Chiama onBackPressedDispatcher se vuoi comunque finire l'attività
                finish()
            }
        })

    }


        // Metodo per avviare e stoppare
        private fun startPlayback() {
            if (!isPlaying) {
                mediaPlayer.start()
                playbtn.setImageResource(R.drawable.ic_pausa)
                handler.postDelayed(runnable, delay)
                isPlaying = true
            }else{
                mediaPlayer.pause()
                playbtn.setImageResource(R.drawable.ic_play)
                handler.removeCallbacks(runnable)
                isPlaying= false
            }
        }
}
