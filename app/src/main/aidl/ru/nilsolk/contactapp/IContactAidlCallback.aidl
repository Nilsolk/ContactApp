package ru.nilsolk.contactapp;
import ru.nilsolk.contactapp.Contact;
interface IContactAidlCallback {
    void onContactsLoaded(in List<Contact> contacts);
    void onRemovedDuplicates(in List<Contact> contacts);
    void DuplicatesNotFound();
    void onError(String error);
}