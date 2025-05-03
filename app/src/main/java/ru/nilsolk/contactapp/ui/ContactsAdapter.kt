package ru.nilsolk.contactapp.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.nilsolk.contactapp.databinding.ContactItemBinding

class ContactsAdapter : RecyclerView.Adapter<ContactsAdapter.ContactsHolder>() {
    private val contactList = mutableListOf<ContactModel>()

    fun setList(list: List<ContactModel>){
        contactList.addAll(list)
        notifyItemChanged(0, list.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactsHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ContactItemBinding.inflate(inflater, parent, false)
        return ContactsHolder(binding)
    }

    override fun getItemCount() = contactList.size

    override fun onBindViewHolder(holder: ContactsHolder, position: Int) {
        val item = contactList[position]
        with(holder.viewBinding){
            number.text =  item.number
            name.text = item.name
        }
    }

    class ContactsHolder(val viewBinding: ContactItemBinding) :
        RecyclerView.ViewHolder(viewBinding.root)
}