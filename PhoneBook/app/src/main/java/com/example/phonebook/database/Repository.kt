package com.example.phonebook.database

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.example.phonebook.domain.model.PhoneModel
import com.example.phonebook.domain.model.TagModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class Repository(
    private val phoneDao: PhoneDao,
    private val tagDao: TagDao,
    private val dbMapper: DbMapper
) {
    // by lazy -> do something only the first time
    // available phone number
    private val phonesNotInTrashLiveData: MutableLiveData<List<PhoneModel>> by lazy {
        MutableLiveData<List<PhoneModel>>()
    }
    fun getAllPhonesNotInTrash(): LiveData<List<PhoneModel>> = phonesNotInTrashLiveData

    // not available phone number
    private val phonesInTrashLiveData: MutableLiveData<List<PhoneModel>> by lazy {
        MutableLiveData<List<PhoneModel>>()
    }
    fun getAllPhonesInTrash(): LiveData<List<PhoneModel>> = phonesInTrashLiveData

    private fun initDatabase(postInitAction: () -> Unit) {
        GlobalScope.launch {
            // prepare default tags for database
            val tags = TagDbModel.DEFAULT_TAGS.toTypedArray()
            val dbColors = tagDao.getAllSync()
            if (dbColors.isNullOrEmpty()) {
                tagDao.insertAll(*tags)
            }

            // prepare default phones for database
            val phones = PhoneDbModel.DEFAULT_PHONES.toTypedArray()
            val dbNotes = phoneDao.getAllSync()
            if (dbNotes.isNullOrEmpty()) {
                phoneDao.insertAll(*phones)
            }

            postInitAction.invoke()
        }
    }

    // get list of phone number
    private fun getAllPhonesDependingOnTrashStateSync(inTrash: Boolean): List<PhoneModel> {
        val tagDbModels: Map<Long, TagDbModel> = tagDao.getAllSync().map { it.id to it }.toMap()
        val dbNotes: List<PhoneDbModel> =
            phoneDao.getAllSync().filter { it.isInTrash == inTrash }
        return dbMapper.mapPhones(dbNotes, tagDbModels)
    }

    private fun updatePhonesLiveData() {
        phonesNotInTrashLiveData.postValue(getAllPhonesDependingOnTrashStateSync(false))
        phonesInTrashLiveData.postValue(getAllPhonesDependingOnTrashStateSync(true))
    }

    // initiate database
    init {
        initDatabase(this::updatePhonesLiveData)
    }

    fun insertPhone(phone: PhoneModel) {
        phoneDao.insert(dbMapper.mapDbPhone(phone))
        updatePhonesLiveData()
    }

    fun deletePhones(phoneIds: List<Long>) {
        phoneDao.delete(phoneIds)
        updatePhonesLiveData()
    }

    fun movePhoneToTrash(phoneId: Long) {
        val dbPhone = phoneDao.findByIdSync(phoneId)
        val newDbPhone = dbPhone.copy(isInTrash = true)
        phoneDao.insert(newDbPhone)
        updatePhonesLiveData()
    }

    fun restorePhonesFromTrash(phoneIds: List<Long>) {
        val dbPhonesInTrash = phoneDao.getNotesByIdsSync(phoneIds)
        dbPhonesInTrash.forEach {
            val newDbNote = it.copy(isInTrash = false)
            phoneDao.insert(newDbNote)
        }
        updatePhonesLiveData()
    }

    fun getAllTags(): LiveData<List<TagModel>> =
        Transformations.map(tagDao.getAll()) { dbMapper.mapTags(it) }
}