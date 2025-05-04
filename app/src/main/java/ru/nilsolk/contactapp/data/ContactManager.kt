package ru.nilsolk.contactapp.data

import ru.nilsolk.contactapp.Contact

interface ContactManager {
    suspend fun fetchContacts():MutableList<Contact>
    suspend fun removeDuplicates():MutableList<Contact>
    suspend fun deleteContactById(contactId: String)
}