package ru.nilsolk.contactapp.data

import android.content.ContentResolver
import android.content.ContentUris
import android.graphics.Bitmap
import android.provider.ContactsContract
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.nilsolk.contactapp.Contact

class ContactManagerImpl(
    private val contentResolver: ContentResolver
) : ContactManager {

    private val projection = arrayOf(
        ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
        ContactsContract.CommonDataKinds.Phone.NUMBER,
        ContactsContract.CommonDataKinds.Phone.PHOTO_URI
    )

    private suspend fun processContacts(process: suspend (Contact) -> Unit) {
        withContext(Dispatchers.IO) {
            contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                projection,
                null, null, null
            )?.use { cursor ->
                val idIndex =
                    cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)
                val nameIndex =
                    cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                val numberIndex =
                    cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                val photoUriIndex =
                    cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI)

                while (cursor.moveToNext()) {
                    val contact = Contact().apply {
                        id = cursor.getString(idIndex) ?: ""
                        name = cursor.getString(nameIndex) ?: ""
                        number = cursor.getString(numberIndex) ?: ""
                        photoUri = cursor.getString(photoUriIndex) ?: ""
                    }
                    process(contact)
                }
            }
        }
    }


    override suspend fun fetchContacts(): MutableList<Contact> {
        return withContext(Dispatchers.IO) {
            val contacts = mutableListOf<Contact>()
            processContacts { contact ->
                contacts.add(contact)
            }
            contacts
        }
    }

    override suspend fun removeDuplicates(): Pair<MutableList<Contact>, Int> {
        return withContext(Dispatchers.IO) {
            val uniqueContactsMap = HashMap<String, Contact>()
            var countOfRemoved = 0
            processContacts { contact ->
                val contactKey = "${contact.name}-${contact.number}"
                if (!uniqueContactsMap.containsKey(contactKey)) {
                    uniqueContactsMap[contactKey] = contact
                } else {
                    val contactIdToDelete = contact.id
                    deleteContactById(contactIdToDelete)
                    countOfRemoved++
                }
            }
            Pair(uniqueContactsMap.values.toMutableList(), countOfRemoved)
        }

    }

    override suspend fun deleteContactById(contactId: String) {
        val contactUri =
            ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactId.toLong())
        contentResolver.delete(contactUri, null, null)
    }

}