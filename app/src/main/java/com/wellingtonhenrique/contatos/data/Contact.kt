package com.wellingtonhenrique.contatos.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "contacts")
data class Contact(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name : String,
    val phone : String,
    val imageUrl : String
)