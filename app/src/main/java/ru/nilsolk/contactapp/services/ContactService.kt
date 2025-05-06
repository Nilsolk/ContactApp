package ru.nilsolk.contactapp.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import ru.nilsolk.contactapp.IContactAidlCallback
import ru.nilsolk.contactapp.IContactAidlInterface
import ru.nilsolk.contactapp.data.ContactManagerImpl


class ContactService : Service() {

    private lateinit var contactManager: ContactManagerImpl
    private val serviceJob = SupervisorJob()
    private val coroutineScope = CoroutineScope(Dispatchers.IO + serviceJob)

    private val binder = object : IContactAidlInterface.Stub() {
        override fun getContacts(callback: IContactAidlCallback) {
            coroutineScope.launch {
                try {
                    val contacts = contactManager.fetchContacts()
                    callback.onContactsLoaded(contacts)
                } catch (e: Exception) {
                    callback.onError(e.message ?: "Unknown error!");
                }
            }
        }

        override fun removeDuplicates(callback: IContactAidlCallback) {
            coroutineScope.launch {
                try {
                    val pair = contactManager.removeDuplicates()
                    val contactsWithoutDuplicate = pair.first
                    val countOfRemoved = pair.second
                    if (countOfRemoved != 0) {
                        callback.onRemovedDuplicates(contactsWithoutDuplicate)
                    } else {
                        callback.DuplicatesNotFound()
                    }
                } catch (e: Exception) {
                    callback.onError(e.message ?: "Fail remove!")
                }
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        contactManager = ContactManagerImpl(contentResolver)
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceJob.cancel()
    }
    override fun onBind(intent: Intent): IBinder = binder
}