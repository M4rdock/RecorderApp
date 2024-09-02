package com.rkoma.recorderapp

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File


class ShowList : AppCompatActivity(), ClickListener {


    private lateinit var recorderApp: ArrayList<RecorderApp>
    private lateinit var thelper: Helper
    private lateinit var datab: AppDatabase
    private lateinit var recyclerView: RecyclerView

    private lateinit var toolbar: MaterialToolbar

    private lateinit var searchFile : TextInputEditText


    private lateinit var bottomSheet: LinearLayout
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>

    private lateinit var deletebtn : ImageButton
    private lateinit var editbtn : ImageButton
    private lateinit var sharebtn : ImageButton

    private lateinit var cancella :  TextView
    private lateinit var rinomina :  TextView
    private lateinit var condividi : TextView



    private lateinit var editbar : View
    private lateinit var closebtn : ImageButton
    private lateinit var selectAllbtn : ImageButton

    private var allChecked = false


    @OptIn(DelicateCoroutinesApi::class)
    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_show_list)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.second)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        editbar= findViewById(R.id.edit)
        closebtn=findViewById(R.id.closebtn)
        selectAllbtn = findViewById(R.id.selectAll)

        deletebtn= findViewById(R.id.deletebtn2)
        sharebtn=findViewById(R.id.sharebtn)
        editbtn = findViewById(R.id.editbtn)

        cancella= findViewById(R.id.Cancella)
        rinomina=findViewById(R.id.Rinomina)
        condividi = findViewById(R.id.Condividi)


        bottomSheet = findViewById(R.id.RenDelSha)
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        toolbar= findViewById(R.id.toolbar)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        toolbar.setNavigationOnClickListener{
            onBackPressedDispatcher.onBackPressed()
        }

        recorderApp= ArrayList()

        // Inizializzazione del database usando Room
        datab = Room.databaseBuilder(this, AppDatabase::class.java, "registration").build()


        thelper = Helper(recorderApp, this)

        // Inizializzazione del RecyclerView e configurazione dell'adapter e del layout manager
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.adapter= thelper
        recyclerView.layoutManager = LinearLayoutManager(this)


        // Avvio del caricamento dei dati in modo asincrono usando Coroutine
        loadData()

        searchFile = findViewById(R.id.searchbar)
        searchFile.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString()
                searchDatabase(query)

            }

            override fun afterTextChanged(s: Editable?) {

            }
        })

        closebtn.setOnClickListener(){
            leaveEditMode()

        }

        selectAllbtn.setOnClickListener(){
            allChecked = !allChecked
            recorderApp.map {it.isChecked = allChecked }
            thelper.notifyDataSetChanged()

            if(allChecked){
                disableShare()
                disableEdit()
                enableDelete()
            }else{
                disableShare()
                disableDelete()
                disableEdit()
            }
        }


        deletebtn.setOnClickListener{
            val alert = AlertDialog.Builder(this)
            alert.setTitle("Cancella registrazioni")
            val selRecord = recorderApp.count{it.isChecked}
            alert.setMessage("Sei sicuro di voler cancellare i $selRecord selezionato/i ?")

            alert.setPositiveButton("Cancella"){_,_ ->
                val delet = recorderApp.filter{it.isChecked}. toTypedArray()

                GlobalScope.launch {
                    datab.RecorderAppDAO().delete(delet)

                    runOnUiThread{
                        recorderApp.removeAll(delet.toSet())
                        thelper.notifyDataSetChanged()
                        leaveEditMode()
                    }
                }

            }
            alert.setNegativeButton("Annulla"){_,_ ->

            }
            val dialog = alert.create()
            dialog.show()
        }

        rinomina.setOnClickListener{
            val alert = AlertDialog.Builder(this)
            val dialogView = this.layoutInflater.inflate(R.layout.edit_sheet, null)
            alert.setView(dialogView)
            val dialog = alert.create()

            val record = recorderApp.filter { it.isChecked }[0]
            val textInput = dialogView.findViewById<TextInputEditText>(R.id.editTextFileName)

            textInput.setText(record.filename)

            dialogView.findViewById<Button>(R.id.buttonConfirm).setOnClickListener{
                val input = textInput.text.toString()
                if (input.isEmpty()){
                    Toast.makeText(this, "devi inserire un nome", Toast.LENGTH_SHORT).show()
                }else {
                    record.filename = input
                    GlobalScope.launch {
                        datab.RecorderAppDAO().update(record)

                        runOnUiThread{
                            thelper.notifyItemChanged(recorderApp.indexOf(record))
                            leaveEditMode()
                        }
                    }
                }
            }

            dialogView.findViewById<Button>(R.id.buttonDelete).setOnClickListener{
                dialog.dismiss()
            }


            dialog.show()
        }




    }

    private fun leaveEditMode(){
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        editbar.visibility = View.GONE
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        recorderApp.map {it.isChecked = false }
        thelper.setEditMode(false)
    }


    private fun disableShare(){
        sharebtn.visibility=View.GONE
        sharebtn.isClickable= false
        condividi.setTextColor(ContextCompat.getColor(this, R.color.white))

    }

    private fun disableEdit(){
        editbtn.visibility=View.GONE
        editbtn.isClickable= false
        rinomina.setTextColor(ContextCompat.getColor(this, R.color.white))

    }

    private fun disableDelete(){
        deletebtn.visibility=View.GONE
        deletebtn.isClickable= false
        cancella.setTextColor(ContextCompat.getColor(this, R.color.white))

    }



    private fun enableShare(){
        sharebtn.visibility=View.VISIBLE
        sharebtn.isClickable= true
        condividi.setTextColor(ContextCompat.getColor(this, R.color.black))
        sharebtn.setOnClickListener {
            shareSelectedVoiceMemos()
        }
    }

    private fun enableEdit(){
        editbtn.visibility=View.VISIBLE
        editbtn.isClickable= true
        rinomina.setTextColor(ContextCompat.getColor(this, R.color.black))

    }

    private fun enableDelete(){
        deletebtn.visibility=View.VISIBLE
        deletebtn.isClickable= true
        cancella.setTextColor(ContextCompat.getColor(this, R.color.black))

    }


    @SuppressLint("NotifyDataSetChanged")
    private fun searchDatabase(query: String) {
        lifecycleScope.launch {
            // Usa il dispatcher IO per l'accesso al database
            withContext(Dispatchers.IO) {
                recorderApp.clear()
                val queryR = datab.RecorderAppDAO().search("%$query%")
                recorderApp.addAll(queryR)

                runOnUiThread {
                    thelper.notifyDataSetChanged()
                }

            }
        }
    }

    // Funzione per caricare i dati dal database
    @SuppressLint("NotifyDataSetChanged")
    private fun loadData() {
        lifecycleScope.launch {
            // Usa il dispatcher IO per l'accesso al database
            withContext(Dispatchers.IO) {
                recorderApp.clear()
                val query = datab.RecorderAppDAO().getAll() // Carica tutti i record dal database
                recorderApp.addAll(query)

                thelper.notifyDataSetChanged()
            }


        }
    }

    override val filePath: String
        get() = TODO("Not yet implemented")

    override fun clicklistener(position: Int) {
        val recording = recorderApp[position]

        if (thelper.isEditMode()){
            recorderApp[position].isChecked = !recorderApp[position].isChecked
            thelper.notifyItemChanged(position)

            val selected = recorderApp.count{it.isChecked}
            when(selected){
                0 -> {
                    disableEdit()
                    disableShare()
                    disableDelete()

                }

                1 -> {
                    enableEdit()
                    enableShare()
                    enableDelete()
                }

                else -> {
                    disableEdit()
                    disableShare()
                    enableDelete()
                }

            }


        }else{
            val intent = Intent(this, AudioPlayer::class.java)

            intent.putExtra("filepath", recording.filePath)
            intent.putExtra("filename", recording.filename)
            startActivity(intent)

        }


    }

    override fun longclicklistener(position: Int) {
        thelper.setEditMode(true)
        recorderApp[position].isChecked = !recorderApp[position].isChecked
        thelper.notifyItemChanged(position)


        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED



        if(thelper.isEditMode() && editbar.visibility == View.GONE){
            supportActionBar?.setDisplayHomeAsUpEnabled(false)
            supportActionBar?.setDisplayShowHomeEnabled(false)

            editbar.visibility = View.VISIBLE

            enableDelete()
            enableEdit()
            enableShare()

        }
    }

    private fun shareSelectedVoiceMemos() {
        // Filtra i memo vocali selezionati
        val selectedMemos = recorderApp.filter { it.isChecked }

        if (selectedMemos.isEmpty()) {
            Toast.makeText(this, "Seleziona almeno un memo da condividere", Toast.LENGTH_SHORT).show()
            return
        }

        // Lista per contenere gli URI dei file
        val filesToShare = ArrayList<Uri>()

        selectedMemos.forEach { memo ->
            val file = File(memo.filePath)
            if (file.exists()) {
                // Ottieni l'Uri sicuro per il file utilizzando FileProvider
                val fileUri = FileProvider.getUriForFile(
                    this,
                    "com.rkoma.recorderapp.fileprovider",  // Assicurati che questo nome di provider corrisponda a quello definito nel tuo Manifest
                    file
                )
                filesToShare.add(fileUri)
            }
        }

        if (filesToShare.isNotEmpty()) {
            // Crea l'Intent di condivisione
            val shareIntent = Intent(Intent.ACTION_SEND_MULTIPLE)
            shareIntent.type = "audio/*"
            shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, filesToShare)
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

            // Avvia l'Intent di condivisione
            startActivity(Intent.createChooser(shareIntent, "Condividi memo vocale tramite"))
        } else {
            Toast.makeText(this, "Nessun file disponibile per la condivisione", Toast.LENGTH_SHORT).show()
        }
    }


}


