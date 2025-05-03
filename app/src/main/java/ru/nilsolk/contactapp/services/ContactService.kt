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
    private val contactManager = ContactManagerImpl(contentResolver)
    private val binder = object : IContactAidlInterface.Stub()
    {
        override fun getContacts(callback: IContactAidlCallback) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val contacts = contactManager.fetchContacts()
                    callback.onContactsLoaded(contacts)
                } catch(e:Exception){
                    callback.onError(e.message?:"Unknown error!");
                }
            }
        }
    }

    override fun onBind(intent: Intent): IBinder = binder
}