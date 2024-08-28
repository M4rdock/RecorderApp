package com.rkoma.recorderapp

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Timestamp

@Entity(tableName = "recorderApp")
data class RecorderApp(
    var filename:String,
    var filePath:String,
    var timestamp:Long,
    var duration:String,
    var ampsPath:String

) {
    @PrimaryKey(autoGenerate = true)
    var id = 0


}