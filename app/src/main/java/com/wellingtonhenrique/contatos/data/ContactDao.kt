package com.wellingtonhenrique.contatos.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface ContactDao {
    @Query("SELECT * FROM contacts ORDER BY name ASC")
    fun getAllContacts(): LiveData<List<Contact>>

    @Insert
    suspend fun insertContact(contact: Contact)

    @Query("SELECT * FROM contacts WHERE id = :id")
    fun getContactById(id: Int): LiveData<Contact>

    @Update
    suspend fun updateContact(contact: Contact)

    @Delete
    suspend fun deleteContact(contact: Contact)
}