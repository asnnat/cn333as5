package com.example.phonebook.domain.model

const val NEW_PHONE_ID = -1L

data class PhoneModel(
    val id: Long = NEW_PHONE_ID, // This value is used for new notes
    val name: String = "",
    val phone_number: String = "",
    val tag: TagModel = TagModel.DEFAULT
)