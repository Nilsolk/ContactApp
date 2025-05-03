package ru.nilsolk.contactapp.data.mapper
import ru.nilsolk.contactapp.Contact
import ru.nilsolk.contactapp.ui.ContactModel
class ContactMapper: AbstractMapper<Contact, ContactModel>() {
    override fun map(input: Contact): ContactModel {
        return ContactModel(name = input.name, surname = input.surname, number = input.number)
    }

    override fun mapList(input: List<Contact>): List<ContactModel> {
       return input.map { map(it) }
    }
}