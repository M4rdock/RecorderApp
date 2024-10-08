package com.rkoma.recorderapp

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.io.File
import androidx.room.Room
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date

const val REQUEST_CODE = 200
class MainActivity : AppCompatActivity() {


    private var permessi = arrayOf(Manifest.permission.RECORD_AUDIO)
    private var permessigarantiti = false

    private lateinit var startRecordingButton: ImageButton
    private lateinit var stopRecordingButton: ImageButton
    private lateinit var listButton: ImageButton
    private lateinit var deleteButton: ImageButton
    private lateinit var doneButton: ImageButton
    private lateinit var Time: TextView


    private lateinit var recorder: MediaRecorder
    private var dirPath=""
    private var fileName=""
    private var inRec= false
    private var inPause= false

    private lateinit var stopwatch : StopWatch
    private lateinit var dataB: AppDatabase //variabile dichiaerata per Db




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //connessione con il Db
        dataB = Room.databaseBuilder(
            this,
            AppDatabase::class.java,
            "registration"
        ).build()


        startRecordingButton = findViewById(R.id.recBtn)
        stopRecordingButton = findViewById(R.id.pauseBtn)
        listButton= findViewById(R.id.listBtn)
        deleteButton=findViewById(R.id.deleteBtn)
        doneButton=findViewById(R.id.doneBtn)
        Time = findViewById(R.id.timerTextView)



        permessigarantiti = ActivityCompat.checkSelfPermission(this, permessi[0]) == PackageManager.PERMISSION_GRANTED

        if(!permessigarantiti)
            ActivityCompat.requestPermissions(this, permessi , REQUEST_CODE)

        stopwatch = StopWatch(Time)

        startRecordingButton.setOnClickListener{
            when{
                inPause -> resumeRec()
                inRec-> pauseRec()
                else -> startRec()
            }
        }

        listButton.setOnClickListener{

            startActivity(Intent(this , ShowList::class.java))

        }

        deleteButton.setOnClickListener{
            stopRec()
            val file = File("$dirPath$fileName.mp3")
            file.delete()
            Toast.makeText(this, "file cancellato", Toast.LENGTH_SHORT).show()

        }

        doneButton.setOnClickListener{
            stopRec()
            showSavesheet()
            //Toast.makeText(this, "salvato", Toast.LENGTH_SHORT).show()
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode == REQUEST_CODE)
            permessigarantiti = grantResults[0] == PackageManager.PERMISSION_GRANTED
    }

    private fun pauseRec(){
        recorder.pause()
        inPause=true
        startRecordingButton.setImageResource(R.drawable.ic_rrecord)
        stopwatch.pauseSw()
    }

    private fun resumeRec(){
        recorder.resume()
        inPause=false
        startRecordingButton.setImageResource(R.drawable.ic_pausa)
        stopwatch.startSw()
    }

    private fun stopRec(){
        stopwatch.resetSw()

        recorder.stop()
        recorder.release()

        inPause=false
        inRec=false

        //la pagina mostra il bottone di avvio e la lista , delete e done compaiono solo quando viene fatta partire la registrazione
        listButton.visibility= View.VISIBLE
        doneButton.visibility= View.GONE
        deleteButton.visibility= View.GONE
        startRecordingButton.setImageResource(R.drawable.ic_rrecord)

        Time.text="00:00:00"
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

        val SimpleDateFormat = SimpleDateFormat("yyyy.MM.DD_HH.mm")
        val data = SimpleDateFormat.format(Date())
        fileName="audio_record_$data"

        recorder.setAudioSource(MediaRecorder.AudioSource.MIC)
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
        recorder.setOutputFile("$dirPath$fileName.mp3")

        try{
            recorder.prepare()
        }catch(e: IOException){}

        recorder.start()

        startRecordingButton.setImageResource(R.drawable.ic_pausa)
        inRec=true
        inPause=false


        stopwatch.startSw()

        deleteButton.visibility = View.VISIBLE
        listButton.visibility= View.GONE
        doneButton.visibility= View.VISIBLE


    }

    private fun showSavesheet() {
        // Crea il Bottom Sheet Dialog
        val bottomSheetDialog = BottomSheetDialog(this)
        val bottomSheetView = LayoutInflater.from(this).inflate(R.layout.save_sheet, null)

        // Imposta il nome del file nell'EditText
        val fileNameET : EditText = bottomSheetView.findViewById(R.id.editTextFileName)
        fileNameET.setText(fileName)


        // Imposta il listener per il pulsante "Conferma"
        val confirmButton: Button = bottomSheetView.findViewById(R.id.buttonConfirm)
        confirmButton.setOnClickListener {

            //funzione per rinominare
            val newName = fileNameET.text.toString()
            if (newName != fileNameET.toString()){
                val newFile =File("$dirPath$newName.mp3")
                File("$dirPath$fileName.mp3").renameTo(newFile)}

            val filePath ="$dirPath$newName.mp3"
            val timestamp = Date().time
            val duration = stopwatch.getElapsedTime()

            val registrazione = RecorderApp(newName, filePath, timestamp, duration)

            //per non usare troppe risorse
            GlobalScope.launch{
                dataB.RecorderAppDAO().insert(registrazione)
            }

            bottomSheetDialog.dismiss()
        }

        // Imposta il listener per il pulsante "Elimina"
        val deleteButton: Button = bottomSheetView.findViewById(R.id.buttonDelete)
        deleteButton.setOnClickListener {
            File("$dirPath$fileName.mp3").delete()
            bottomSheetDialog.dismiss() // Chiudi il Bottom Sheet
        }

        // Mostra il Bottom Sheet
        bottomSheetDialog.setContentView(bottomSheetView)
        bottomSheetDialog.show()
    }




}