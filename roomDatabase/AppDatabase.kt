package com.example.roomdatabase.repository

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [User::class], version = 1, exportSchema = false)
abstract class AppDatabase: RoomDatabase() {

    abstract fun getDao(): DatabaseDao

    companion object {

        @Volatile
        private var instance: AppDatabase? = null
        private const val DATABASE_NAME= "room_example_database"

        @Synchronized
        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                val roomInstance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DATABASE_NAME
                ).fallbackToDestructiveMigration().build()
                instance = roomInstance
                roomInstance
            }
        }

    }
}