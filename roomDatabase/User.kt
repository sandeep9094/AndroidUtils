package com.example.roomdatabase.repository

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_list")
data class User(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var name: String,
    var lastName: String
) {
    constructor(name: String, lastName: String): this(0, name, lastName)
}