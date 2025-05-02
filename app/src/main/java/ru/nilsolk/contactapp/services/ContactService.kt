package ru.nilsolk.contactapp.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import ru.nilsolk.contactapp.IContactAidlCallback
import ru.nilsolk.contactapp.IContactAidlInterface


class ContactService : Service() {
    val binder = object : IContactAidlInterface.Stub()
    {
        override fun getContacts(callback: IContactAidlCallback?) {
            TODO("Not yet implemented")
        }
    }

    override fun onBind(intent: Intent): IBinder = binder
}