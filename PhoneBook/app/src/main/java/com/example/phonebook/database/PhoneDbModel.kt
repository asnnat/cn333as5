package com.example.phonebook.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class PhoneDbModel(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "phone_number") val phone_number: String,
    @ColumnInfo(name = "tag_id") val tagId: Long,
    @ColumnInfo(name = "in_trash") val isInTrash: Boolean
) {
    companion object {
        val DEFAULT_PHONES = listOf(
            PhoneDbModel(1, "Ant", "0987654321", 1, false),
            PhoneDbModel(2, "Bird", "0987654322", 2, false),
            PhoneDbModel(3, "Cat", "0987654323", 3, false),
            PhoneDbModel(4, "Dog", "0987654324", 1, true),
        )
    }
}