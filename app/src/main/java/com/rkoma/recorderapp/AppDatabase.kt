package com.rkoma.recorderapp

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [RecorderApp::class], version = 1)

abstract class AppDatabase : RoomDatabase() {

    abstract fun RecorderAppDAO() : RecorderAppDAO


}

