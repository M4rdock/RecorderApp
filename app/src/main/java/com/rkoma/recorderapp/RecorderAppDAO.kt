package com.rkoma.recorderapp

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface RecorderAppDAO {

    /**
     * Recupera tutti i record dalla tabella `recordings` e li ordina in base alla data in ordine decrescente.
     * La query SQL viene specificata direttamente nell'annotazione `@Query`.
     *
     */
    @Query (value = "SELECT * FROM recorderApp Where filename like :query")
    fun search(query: String): List<RecorderApp>




    @Query( value = "SELECT * FROM recorderApp ORDER BY timestamp DESC")
    fun getAll(): List<RecorderApp>
    /**
     * Inserisce un nuovo record nella tabella `recordings`.
     * Room genera automaticamente la query SQL necessaria per l'inserimento.
     *
     */

    @Insert
    fun insert(vararg recorderApp: RecorderApp)

    /**
     * Elimina un record specifico dalla tabella `recordings`.
     * Room esegue l'operazione di eliminazione in base alla chiave primaria dell'oggetto `Recording` fornito.
     *
     */

    @Delete
    fun delete(recorderApp: RecorderApp)


    @Delete
    fun delete(recorderApp: Array<RecorderApp>)
    /**
     * Aggiorna un record esistente nella tabella `recordings`.
     * Room determina automaticamente quale record aggiornare basandosi sulla chiave primaria dell'oggetto `Recording` fornito.
     *
     */

    @Update
    fun update(recorderApp: RecorderApp)


}