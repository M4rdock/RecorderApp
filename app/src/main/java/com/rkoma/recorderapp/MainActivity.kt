package com.rkoma.recorderapp

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Build
import android.widget.Button
import android.widget.ImageButton
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date

const val REQUEST_CODE = 200
class MainActivity : AppCompatActivity() {


    private var permessi = arrayOf(Manifest.permission.RECORD_AUDIO)
    private var permessigarantiti = false

    private lateinit var startRecordingButton: ImageButton
    private lateinit var stopRecordingButton: ImageButton
    private lateinit var saveRecordingButton: Button
    private lateinit var viewRecordingsButton: Button

    private lateinit var recorder: MediaRecorder
    private var dirPath=""
    private var fileName=""
    private var inRec= false
    private var inPause= false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        startRecordingButton = findViewById(R.id.recBtn)
        stopRecordingButton = findViewById(R.id.pauseBtn)

        permessigarantiti = ActivityCompat.checkSelfPermission(this, permessi[0]) == PackageManager.PERMISSION_GRANTED

        if(!permessigarantiti)
            ActivityCompat.requestPermissions(this, permessi , REQUEST_CODE)

        startRecordingButton.setOnClickListener{
            when{
                inPause -> resumeRec()
                inRec-> pauseRec()
                else -> startRec()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode == REQUEST_CODE)
            permessigarantiti = grantResults[0] == PackageManager.PERMISSION_GRANTED
    }

    private fun pauseRec(){
        recorder.pause()
        inPause=true
        startRecordingButton.setImageResource(R.drawable.ic_record)
    }

    private fun resumeRec(){
        recorder.resume()
        inPause=false
        startRecordingButton.setImageResource(R.drawable.ic_pause)
    }

    private fun startRec(){
        if(!permessigarantiti){
            ActivityCompat.requestPermissions(this, permessi , REQUEST_CODE)
            return
        }
        //start recording
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            recorder = MediaRecorder(this)
        }

        dirPath= "${externalCacheDir?.absolutePath}"

        var SimpleDateFormat = SimpleDateFormat("yyyy.MM.DD_hh.mm")
        var data = SimpleDateFormat.format(Date())
        fileName="audio_record_$data"

        recorder.setAudioSource(MediaRecorder.AudioSource.MIC)
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
        recorder.setOutputFile("$dirPath$fileName.mp3")

        try{
            recorder.prepare()
        }catch(e: IOException){}

        recorder.start()

        startRecordingButton.setImageResource(R.drawable.ic_pause)
        inRec=true
        inPause=false

    }

}