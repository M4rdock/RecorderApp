package com.rkoma.recorderapp

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "recorderApp")

data class RecorderApp(
    var filename:String,
    var filePath:String,
    var timestamp:Long,
    var duration:Long

) {
    @PrimaryKey(autoGenerate = true)
    var id =0
    var isChecked= false



}