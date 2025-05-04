package ru.nilsolk.contactapp.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.nilsolk.contactapp.IContactAidlCallback
import ru.nilsolk.contactapp.IContactAidlInterface
import ru.nilsolk.contactapp.data.ContactManagerImpl


class ContactService : Service() {
    private lateinit var contactManager: ContactManagerImpl
    private val binder = object : IContactAidlInterface.Stub() {
        override fun getContacts(callback: IContactAidlCallback) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val contacts = contactManager.fetchContacts()
                    callback.onContactsLoaded(contacts)
                } catch (e: Exception) {
                    callback.onError(e.message ?: "Unknown error!");
                }
            }
        }

        override fun removeDuplicates(callback: IContactAidlCallback) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val contactsWithoutDuplicate = (contactManager.removeDuplicates()).first
                    val countOfRemoved = (contactManager.removeDuplicates()).second
                    if (countOfRemoved!= 0) {
                        callback.onRemovedDuplicates(contactsWithoutDuplicate)
                    } else {
                        callback.DuplicatesNotFound()
                    }
                } catch(e:Exception){
                    callback.onError(e.message ?: "Fail remove!")
                }
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        contactManager = ContactManagerImpl(contentResolver)
    }

    override fun onBind(intent: Intent): IBinder = binder
}