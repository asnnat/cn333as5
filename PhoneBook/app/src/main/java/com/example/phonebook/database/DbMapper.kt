package com.example.phonebook.database

import com.example.phonebook.domain.model.NEW_PHONE_ID
import com.example.phonebook.domain.model.PhoneModel
import com.example.phonebook.domain.model.TagModel

class DbMapper {
    // convert between DbModel to Model

    fun mapPhones(
        phoneDbModels: List<PhoneDbModel>,
        tagDbModels: Map<Long, TagDbModel>
    ): List<PhoneModel> = phoneDbModels.map {
        val tagDbModel = tagDbModels[it.tagId]
            ?: throw RuntimeException("Tag for tagId: ${it.tagId} was not found.")

        mapPhone(it, tagDbModel)
    }

    fun mapPhone(phoneDbModel: PhoneDbModel, tagDbModel: TagDbModel): PhoneModel {
        val tag = mapTag(tagDbModel)
        return with(phoneDbModel) { PhoneModel(id, name, phone_number, tag) }
    }

    fun mapTags(tagDbModels: List<TagDbModel>): List<TagModel> =
        tagDbModels.map { mapTag(it) }

    fun mapTag(tagDbModel: TagDbModel): TagModel =
        with(tagDbModel) { TagModel(id, name) }

    fun mapDbPhone(phone: PhoneModel): PhoneDbModel =
        with(phone) {
            // new phone id
            if (id == NEW_PHONE_ID)
                PhoneDbModel(
                    name = name,
                    phone_number = phone_number,
                    tagId = tag.id,
                    isInTrash = false
                )
            // phone idi is exist
            else
                PhoneDbModel(id, name, phone_number, tag.id, false)
        }
}