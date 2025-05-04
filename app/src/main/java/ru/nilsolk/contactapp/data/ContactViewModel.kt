import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.nilsolk.contactapp.Contact
import ru.nilsolk.contactapp.IContactAidlCallback
import ru.nilsolk.contactapp.IContactAidlInterface
import ru.nilsolk.contactapp.data.mapper.ContactMapper
import ru.nilsolk.contactapp.services.ContactService
import ru.nilsolk.contactapp.ui.ContactModel

class ContactViewModel : ViewModel() {
    private var contactService: IContactAidlInterface? = null

    private val _contacts = MutableLiveData<List<ContactModel>>()
    val contacts: LiveData<List<ContactModel>> = _contacts

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private val callback = object : IContactAidlCallback.Stub() {
        override fun onContactsLoaded(contacts: MutableList<Contact>) {
            val mapper = ContactMapper()
            _contacts.postValue(mapper.mapList(contacts))
        }

        override fun onRemovedDublicates(contacts: MutableList<Contact>) {
            val mapper = ContactMapper()
            _contacts.postValue(mapper.mapList(contacts))
        }

        override fun onError(error: String) {
            _error.postValue(error)
        }
    }

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            contactService = IContactAidlInterface.Stub.asInterface(service)
            loadContacts()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            contactService = null
        }
    }
    fun removeDuplicates(){
        contactService?.removeDublicates(callback)
    }

    fun bindService(context: Context) {
        Intent(context, ContactService::class.java).also { intent ->
            context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        }
    }

    fun unbindService(context: Context) {
        context.unbindService(serviceConnection)
    }

    private fun loadContacts() {
        contactService?.getContacts(callback)
    }
}