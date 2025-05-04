package ru.nilsolk.contactapp.data

import ru.nilsolk.contactapp.Contact

interface ContactManager {
    suspend fun fetchContacts():MutableList<Contact>
    suspend fun removeDuplicates(): Pair<MutableList<Contact>, Int>
    suspend fun deleteContactById(contactId: String)
}