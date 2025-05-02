package ru.nilsolk.contactapp;
import ru.nilsolk.contactapp.Contact;
interface IContactAidlCallback {
    void onContactsLoaded(in List<Contact> contacts);
    void onError(String error);
}