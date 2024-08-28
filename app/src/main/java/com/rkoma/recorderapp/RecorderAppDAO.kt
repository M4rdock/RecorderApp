package com.rkoma.recorderapp

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface RecorderAppDAO {
    @Query( value = "SELECT * FROM recorderApp")
    fun getAll(): List<RecorderApp>
    @Insert
    fun insert(vararg recorderApp: RecorderApp)
    @Delete
    fun delete(recorderApp: RecorderApp)
    @Update
    fun update(recorderApp: RecorderApp)


}