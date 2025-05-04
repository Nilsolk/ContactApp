package ru.nilsolk.contactapp.data

import android.content.ContentResolver
import android.content.ContentUris
import android.provider.ContactsContract
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.nilsolk.contactapp.Contact

class ContactManagerImpl(
    private val contentResolver: ContentResolver
) : ContactManager {
    override suspend fun fetchContacts(): MutableList<Contact> {
        return withContext(Dispatchers.IO) {
            val contacts = mutableListOf<Contact>()
            val projection = arrayOf(
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER
            )
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
                while (cursor.moveToNext()) {
                    contacts.add(
                        Contact().apply {
                            id = cursor.getString(idIndex) ?: ""
                            name = cursor.getString(nameIndex) ?: ""
                            number = cursor.getString(numberIndex) ?: ""
                            Log.d("Element", name)
                        }
                    )
                }
            }
            contacts
        }
    }

    override suspend fun removeDuplicates(): Pair<MutableList<Contact>, Int> {
        return withContext(Dispatchers.IO) {
            val uniqueContactsMap = HashMap<String, Contact>()
            var countOfRemoved = 0
            val projection = arrayOf(
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER
            )
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

                while (cursor.moveToNext()) {
                    val contact = Contact().apply {
                        id = cursor.getString(idIndex) ?: ""
                        name = cursor.getString(nameIndex) ?: ""
                        number = cursor.getString(numberIndex) ?: ""
                    }

                    val contactKey = "${contact.name}-${contact.number}"
                    if (!uniqueContactsMap.containsKey(contactKey)) {
                        uniqueContactsMap[contactKey] = contact
                    } else {
                        val contactIdToDelete = cursor.getString(idIndex)
                        deleteContactById(contactIdToDelete)
                        countOfRemoved++
                    }
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