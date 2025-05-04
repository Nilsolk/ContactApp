package ru.nilsolk.contactapp.data.mapper
import ru.nilsolk.contactapp.Contact
import ru.nilsolk.contactapp.data.ContactModel
class ContactMapper: AbstractMapper<Contact, ContactModel>() {
    override fun map(input: Contact): ContactModel {
        return ContactModel(name = input.name, number = input.number)
    }

    override fun mapList(input: List<Contact>): List<ContactModel> {
       return input.map { map(it) }
    }
}