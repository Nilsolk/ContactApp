package ru.nilsolk.contactapp;
import ru.nilsolk.contactapp.IContactAidlCallback;

interface IContactAidlInterface {
    void getContacts(in IContactAidlCallback callback);
}
