package com.wellingtonhenrique.contatos.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Contact::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun contactDao() : ContactDao

    companion object {
        private var INSTANCE: AppDatabase? = null
        fun getDatabase(context : Context) : AppDatabase{
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "contact_db"
                ).build()
            }
            return INSTANCE!!
        }
    }
}