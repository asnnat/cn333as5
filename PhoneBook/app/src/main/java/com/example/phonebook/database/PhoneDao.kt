package com.example.phonebook.database

import android.provider.ContactsContract.CommonDataKinds.Phone
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PhoneDao {
    // get all phone number with synchronous
    @Query("SELECT * FROM PhoneDbModel")
    fun getAllSync(): List<PhoneDbModel>

    // get phone number by id with synchronous
    @Query("SELECT * FROM PhoneDbModel WHERE id IN (:phoneIds)")
    fun getNotesByIdsSync(phoneIds: List<Long>): List<PhoneDbModel>

    // get phone number by an id
    @Query("SELECT * FROM PhoneDbModel WHERE id LIKE :id")
    fun findByIdSync(id: Long): PhoneDbModel

    // insert by id in duplicated
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(phoneDbModel: PhoneDbModel)

    // insert
    @Insert
    fun insertAll(vararg phoneDbModel: PhoneDbModel)

    // delete by an id
    @Query("DELETE FROM PhoneDbModel WHERE id LIKE :id")
    fun delete(id: Long)

    // delete by group of id
    @Query("DELETE FROM PhoneDbModel WHERE id IN (:phoneIds)")
    fun delete(phoneIds: List<Long>)
}