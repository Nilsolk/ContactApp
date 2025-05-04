package ru.nilsolk.contactapp.data

import android.content.ContentResolver
import android.content.ContentUris
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
                ContactsContract.CommonDataKinds.Phone.NUMBER
            )
            contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                projection,
                null, null, null
            )?.use { cursor ->
                val nameIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY)
                val numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                while (cursor.moveToNext()) {
                    contacts.add(
                        Contact().apply {
                            name = cursor.getString(nameIndex) ?: ""
                            number = cursor.getString(numberIndex) ?: ""
                        }
                    )
                }
            }
            contacts
        }
    }

    override suspend fun removeDuplicates(): MutableList<Contact> {
        return withContext(Dispatchers.IO) {
            val uniqueContactsMap = HashMap<String, Contact>()
            val projection = arrayOf(
                ContactsContract.Contacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME_PRIMARY,
                ContactsContract.CommonDataKinds.Phone.NUMBER
            )
            contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                projection,
                null, null, null
            )?.use { cursor ->
                val idIndex = cursor.getColumnIndex(ContactsContract.Contacts._ID)
                val nameIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY)
                val numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)

                while (cursor.moveToNext()) {
                    val contact = Contact().apply {
                        id = cursor.getString(idIndex) ?: ""
                        name = cursor.getString(nameIndex) ?: ""
                        number = cursor.getString(numberIndex)?.replace("\\s|-|\\(|\\)".toRegex(), "") ?: ""
                    }

                    val contactKey = "${contact.name}-${contact.number}"
                    if (!uniqueContactsMap.containsKey(contactKey)) {
                        uniqueContactsMap[contactKey] = contact
                    } else {
                        val contactIdToDelete = cursor.getString(idIndex)
                        deleteContactById(contactIdToDelete)
                    }
                }
            }

            uniqueContactsMap.values.toMutableList()
        }
    }

    override suspend fun deleteContactById(contactId: String) {
        val contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactId.toLong())
        contentResolver.delete(contactUri, null, null)
    }

}