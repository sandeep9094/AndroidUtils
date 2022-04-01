package com.example.roomdatabase.repository

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface DatabaseDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertUser(user: User)

    @Delete
    fun deleteUser(user: User)

    @Query("SELECT * from user_list")
    fun getAllUser(): LiveData<List<User>>


}