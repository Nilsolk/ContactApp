package ru.nilsolk.contactapp.data

import android.content.ContentResolver
import android.provider.ContactsContract
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.nilsolk.contactapp.Contact

class ContactManagerImpl(
    private val contentResolver: ContentResolver
):ContactManager {
    override suspend fun fetchContacts(): MutableList<Contact> {
        return withContext(Dispatchers.IO) {
            val contacts = mutableListOf<Contact>()
            val projection = arrayOf(
                ContactsContract.Contacts.DISPLAY_NAME_PRIMARY,
                ContactsContract.Contacts.DISPLAY_NAME_SOURCE,
                ContactsContract.CommonDataKinds.Phone.NUMBER
            )
            contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                projection,
                null, null, null
            )?.use { cursor ->
                val nameIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY)
                val numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                val surnameIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME_SOURCE)
                while (cursor.moveToNext()) {
                    contacts.add(
                        Contact().apply {
                            name = cursor.getString(nameIndex) ?: ""
                            number = cursor.getString(numberIndex) ?: ""
                            surname = cursor.getString(surnameIndex) ?: ""
                        }
                    )
                }
            }
            contacts
        }
    }
}